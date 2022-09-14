import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { BreadcrumbService } from '@shared/breadcrumbs/breadcrumb.service';

import { PermitNotificationReviewDecision } from 'pmrv-api';

@Component({
  selector: 'app-permit-notification-review-decision-summary-details[reviewDecision]',
  templateUrl: './review-decision-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [BreadcrumbService, DestroySubject],
})
export class ReviewDecisionSummaryComponent {
  @Input() reviewDecision: PermitNotificationReviewDecision;
  @Input() notesVisible = true;
}
