import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, switchMap, takeUntil } from 'rxjs';

import { PendingRequestService } from '../../../core/guards/pending-request.service';
import { DestroySubject } from '../../../core/services/destroy-subject.service';
import { BackLinkService } from '../../../shared/back-link/back-link.service';
import { PermitSurrenderStore } from '../../store/permit-surrender.store';

@Component({
  selector: 'app-answers',
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-full">
        <app-page-heading>Confirm your answers</app-page-heading>
        <app-permit-surrender-summary-details [isPreview]="false"></app-permit-surrender-summary-details>
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
        <a govukLink routerLink="../..">Return to: Permit surrender task list</a>
      </div>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [BackLinkService, DestroySubject],
})
export class AnswersComponent implements OnInit {
  constructor(
    readonly store: PermitSurrenderStore,
    readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly backLinkService: BackLinkService,
    private readonly destroy$: DestroySubject,
  ) {}

  ngOnInit(): void {
    this.route.paramMap
      .pipe(takeUntil(this.destroy$))
      .subscribe((paramMap) => this.backLinkService.show(`/permit-surrender/${paramMap.get('taskId')}`));
  }

  confirm(): void {
    combineLatest([this.route.data, this.store])
      .pipe(
        first(),
        switchMap(([data, state]) =>
          this.store.postApplyPermitSurrender({
            ...state,
            sectionsCompleted: {
              ...state.sectionsCompleted,
              [data.statusKey]: true,
            },
          }),
        ),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => this.router.navigate(['../summary'], { relativeTo: this.route, state: { notification: true } }));
  }
}
