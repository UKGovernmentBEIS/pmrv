import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Router } from '@angular/router';

import { PendingRequestService } from '../../../../core/guards/pending-request.service';
import { PendingRequest } from '../../../../core/interfaces/pending-request.interface';
import { BackLinkService } from '../../../../shared/back-link/back-link.service';
import { PermitRevocationStore } from '../../../store/permit-revocation-store';

@Component({
  selector: 'app-revocation-cessation-summary',
  template: `<app-page-heading>Revocation cessation</app-page-heading>

    <govuk-notification-banner *ngIf="notification" type="success">
      <h1 class="govuk-notification-banner__heading">Details updated</h1>
    </govuk-notification-banner>

    <app-revocation-cessation-summary-details
      [cessation]="store.select('cessation') | async"
      [allowancesSurrenderRequired]="store.select('allowancesSurrenderRequired') | async"
      [isEditable]="store.select('isEditable') | async"
    ></app-revocation-cessation-summary-details>

    <a govukLink routerLink="../..">Return to: Revocation cessation</a> `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [BackLinkService],
})
export class SummaryComponent implements PendingRequest {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;

  constructor(
    readonly pendingRequest: PendingRequestService,
    readonly store: PermitRevocationStore,
    private readonly router: Router,
  ) {}
}
