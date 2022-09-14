import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { first, Observable, pluck, switchMap, takeUntil } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';

import { PermitSurrenderReviewDeterminationGrant } from 'pmrv-api';

import { PendingRequestService } from '../../../../../core/guards/pending-request.service';
import { PendingRequest } from '../../../../../core/interfaces/pending-request.interface';
import { BackLinkService } from '../../../../../shared/back-link/back-link.service';
import { PermitSurrenderStore } from '../../../../store/permit-surrender.store';

@Component({
  selector: 'app-answers',
  template: `<app-page-heading>Confirm your answers</app-page-heading>
    <app-grant-determination-summary-details
      [grantDetermination$]="grantDetermination$"
    ></app-grant-determination-summary-details>
    <div class="govuk-button-group">
      <button appPendingButton govukButton type="button" (click)="confirm()" *ngIf="isEditable$ | async">
        Confirm and complete
      </button>
    </div>
    <a govukLink routerLink="../../..">Return to: Permit surrender review</a> `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [BackLinkService, DestroySubject],
})
export class AnswersComponent implements OnInit, PendingRequest {
  grantDetermination$ = this.store.pipe(
    pluck('reviewDetermination'),
  ) as Observable<PermitSurrenderReviewDeterminationGrant>;
  isEditable$ = this.store.pipe(pluck('isEditable'));

  constructor(
    readonly pendingRequest: PendingRequestService,
    private readonly store: PermitSurrenderStore,
    private readonly backLinkService: BackLinkService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly destroy$: DestroySubject,
  ) {}

  ngOnInit(): void {
    this.route.paramMap
      .pipe(takeUntil(this.destroy$))
      .subscribe((paramMap) => this.backLinkService.show(`/permit-surrender/${paramMap.get('taskId')}/review`));
  }

  confirm(): void {
    this.store
      .pipe(
        first(),
        switchMap((state) => this.store.postReviewDetermination(state.reviewDetermination, true)),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => this.router.navigate(['../summary'], { relativeTo: this.route, state: { notification: true } }));
  }
}
