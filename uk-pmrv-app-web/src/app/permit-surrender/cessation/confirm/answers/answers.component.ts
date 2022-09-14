import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { first, switchMap, takeUntil } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';

import { PendingRequestService } from '../../../../core/guards/pending-request.service';
import { PendingRequest } from '../../../../core/interfaces/pending-request.interface';
import { BackLinkService } from '../../../../shared/back-link/back-link.service';
import { PermitSurrenderStore } from '../../../store/permit-surrender.store';

@Component({
  selector: 'app-answers',
  template: `<app-page-heading>Confirm your answers</app-page-heading>
    <app-cessation-summary-details
      [cessation]="store.select('cessation') | async"
      [allowancesSurrenderRequired]="store.select('allowancesSurrenderRequired') | async"
      [isEditable]="store.select('isEditable') | async"
    ></app-cessation-summary-details>
    <div class="govuk-button-group">
      <button appPendingButton govukButton type="button" (click)="confirm()" *ngIf="store.select('isEditable') | async">
        Confirm and complete
      </button>
    </div>
    <a govukLink routerLink="../..">Return to: Surrender cessation</a> `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [BackLinkService, DestroySubject],
})
export class AnswersComponent implements OnInit, PendingRequest {
  constructor(
    readonly pendingRequest: PendingRequestService,
    readonly store: PermitSurrenderStore,
    private readonly backLinkService: BackLinkService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly destroy$: DestroySubject,
  ) {}

  ngOnInit(): void {
    this.route.paramMap
      .pipe(takeUntil(this.destroy$))
      .subscribe((paramMap) => this.backLinkService.show(`/permit-surrender/${paramMap.get('taskId')}/cessation`));
  }

  confirm(): void {
    this.store
      .pipe(
        first(),
        switchMap((state) => this.store.postSaveCessation(state.cessation, true)),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => this.router.navigate(['../summary'], { relativeTo: this.route, state: { notification: true } }));
  }
}
