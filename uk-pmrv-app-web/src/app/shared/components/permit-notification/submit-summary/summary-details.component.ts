import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { BreadcrumbService } from '@shared/breadcrumbs/breadcrumb.service';

import { PermitNotification } from 'pmrv-api';

interface SummaryDetails {
  description: string;
  documents?: Array<string>;
  relatedChanges: Array<'MONITORING_METHODOLOGY_PLAN' | 'MONITORING_PLAN'>;
  reportingType?:
    | 'EXCEEDED_THRESHOLD_STATED_GHGE_PERMIT'
    | 'EXCEEDED_THRESHOLD_STATED_HSE_PERMIT'
    | 'OTHER_ISSUE'
    | 'RENOUNCE_FREE_ALLOCATIONS';
  justification?: string;
  inRespectOfMonitoringMethodology: boolean;
  details?: string;
  measures?: string;
  proof?: string;
  startDateOfNonCompliance?: string;
  endDateOfNonCompliance?: string;
  type?: 'NON_SIGNIFICANT_CHANGE' | 'OTHER_FACTOR' | 'TEMPORARY_CHANGE' | 'TEMPORARY_FACTOR' | 'TEMPORARY_SUSPENSION';
}

@Component({
  selector: 'app-permit-notification-summary-details[notification][files]',
  templateUrl: './summary-details.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [BreadcrumbService, DestroySubject],
})
export class SummaryDetailsComponent {
  @Input() cssClass: string;
  @Input() allowChange = true;
  @Input() notification: PermitNotification;
  @Input() files: { downloadUrl: string; fileName: string }[];
  @Input() sectionHeading: string;

  get summary() {
    return this.notification as SummaryDetails;
  }

  relatedChangesLabelMap: Partial<Record<'MONITORING_METHODOLOGY_PLAN' | 'MONITORING_PLAN', string>> = {
    MONITORING_PLAN: 'Monitoring plan',
    MONITORING_METHODOLOGY_PLAN: 'Monitoring methodology plan (MMP)',
  };

  descriptionLabelMap: Partial<Record<PermitNotification['type'], string>> = {
    NON_SIGNIFICANT_CHANGE: 'Description of the change',
    OTHER_FACTOR: 'Description of the issue',
    TEMPORARY_CHANGE: 'Description of the temporary change',
    TEMPORARY_FACTOR: 'Description of the factors preventing compliance',
    TEMPORARY_SUSPENSION: 'Description of the regulated activities which are temporarily suspended',
  };

  startDateLabelMap: Partial<Record<PermitNotification['type'], string>> = {
    OTHER_FACTOR: 'Date started',
    TEMPORARY_CHANGE: 'Date the temporary change started',
    TEMPORARY_FACTOR: 'Date the non-compliance started',
    TEMPORARY_SUSPENSION: 'Date the regulated activity stopped',
  };

  endDateLabelMap: Partial<Record<PermitNotification['type'], string>> = {
    OTHER_FACTOR: 'Date ends',
    TEMPORARY_CHANGE: 'Date the temporary change ends',
    TEMPORARY_FACTOR: 'Date the non-compliance ends',
    TEMPORARY_SUSPENSION: 'Date the regulated activity is proposed to restart',
  };

  createRelatedChangesLabel(notification: SummaryDetails) {
    return notification?.relatedChanges?.map((t) => this.relatedChangesLabelMap[t]).join(', ') || '';
  }
}
