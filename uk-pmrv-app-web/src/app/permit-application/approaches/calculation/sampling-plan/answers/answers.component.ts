import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, switchMap, switchMapTo, takeUntil } from 'rxjs';

import { PendingRequestService } from '../../../../../core/guards/pending-request.service';
import { PendingRequest } from '../../../../../core/interfaces/pending-request.interface';
import { DestroySubject } from '../../../../../core/services/destroy-subject.service';
import { BackLinkService } from '../../../../../shared/back-link/back-link.service';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { isReviewUrl } from '../../../approaches';

@Component({
  selector: 'app-answers',
  template: `
    <app-permit-task>
      <app-page-heading caption="Calculation">Check your answers</app-page-heading>
      <app-calculation-plan-summary-details [changePerStage]="true"></app-calculation-plan-summary-details>
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
      <app-approach-return-link
        parentTitle="Calculation approach"
        reviewGroupUrl="calculation"
      ></app-approach-return-link>
    </app-permit-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [BackLinkService, DestroySubject],
})
export class AnswersComponent implements OnInit, PendingRequest {
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
          ? `/permit-application/${taskId}/review/calculation`
          : `/permit-application/${taskId}/calculation`;
        this.backLinkService.show(url);
      });
  }

  confirm(): void {
    this.route.data
      .pipe(
        first(),
        switchMap((data) => this.store.postStatus(data.statusKey, true).pipe(this.pendingRequest.trackRequest())),
        this.pendingRequest.trackRequest(),
        switchMapTo(this.store),
        first(),
      )
      .subscribe((state) =>
        this.router.navigate(
          [
            state.requestTaskType === 'PERMIT_ISSUANCE_APPLICATION_REVIEW'
              ? '../../../review/calculation'
              : '../summary',
          ],
          { relativeTo: this.route, state: { notification: true } },
        ),
      );
  }
}
