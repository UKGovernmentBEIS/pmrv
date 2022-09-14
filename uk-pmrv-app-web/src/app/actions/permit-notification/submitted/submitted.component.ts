import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { PermitNotificationService } from '../core/permit-notification.service';

@Component({
  selector: 'app-permit-notification-submitted',
  template: `
    <app-base-action-container-component
      [header]="(route.data | async)?.pageTitle"
      [customContentTemplate]="customContentTemplate"
      [expectedActionType]="['PERMIT_NOTIFICATION_APPLICATION_SUBMITTED']"
    >
    </app-base-action-container-component>
    <ng-template #customContentTemplate>
      <h2 class="govuk-heading-m">Details</h2>
      <ng-container *ngIf="notification$ | async as notification">
        <app-permit-notification-summary-details
          [allowChange]="false"
          [notification]="notification"
          [files]="permitNotificationService.getDownloadUrlFiles(notification.documents)"
        >
        </app-permit-notification-summary-details>
      </ng-container>
    </ng-template>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SubmittedComponent {
  constructor(readonly route: ActivatedRoute, public readonly permitNotificationService: PermitNotificationService) {}

  notification$ = this.permitNotificationService.notification$;
}
