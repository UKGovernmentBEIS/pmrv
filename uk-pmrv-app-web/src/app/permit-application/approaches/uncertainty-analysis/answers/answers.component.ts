import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, switchMap, takeUntil } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { BackLinkService } from '@shared/back-link/back-link.service';

import { PermitApplicationStore } from '../../../store/permit-application.store';
import { isReviewUrl } from '../../approaches';

@Component({
  selector: 'app-answers',
  template: `
    <app-permit-task>
      <app-page-heading caption="Uncertainty analysis">Check your answers</app-page-heading>
      <app-uncertainty-analysis-summary-details [isPreview]="false"></app-uncertainty-analysis-summary-details>
      <div class="govuk-button-group">
        <button
          appPendingButton
          govukButton
          type="button"
          (click)="confirm()"
          *ngIf="(store.isEditable$ | async) === true"
        >
          Confirm and complete
        </button>
      </div>
      <app-list-return-link
        reviewGroupTitle="Uncertainty analysis"
        reviewGroupUrl="uncertainty-analysis"
      ></app-list-return-link>
    </app-permit-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [BackLinkService, DestroySubject],
})
export class AnswersComponent implements OnInit {
  private readonly taskId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId'))));

  constructor(
    readonly store: PermitApplicationStore,
    readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly backLinkService: BackLinkService,
    private readonly destroy$: DestroySubject,
  ) {}

  ngOnInit(): void {
    combineLatest([this.taskId$, this.store])
      .pipe(takeUntil(this.destroy$))
      .subscribe(([taskId, state]) => {
        const url = isReviewUrl(state.requestTaskType)
          ? `/permit-application/${taskId}/review/uncertainty-analysis`
          : `/permit-application/${taskId}`;
        this.backLinkService.show(url);
      });
  }

  confirm(): void {
    this.route.data
      .pipe(
        first(),
        switchMap((data) => this.store.postStatus(data.permitTask, true)),
      )
      .subscribe(() => this.router.navigate(['../summary'], { relativeTo: this.route, state: { notification: true } }));
  }
}
