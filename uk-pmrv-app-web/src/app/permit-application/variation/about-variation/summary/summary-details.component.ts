import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';

import { PermitApplicationStore } from '../../../store/permit-application.store';
import { significantChangesMonitoringMethodologyPlan } from '../about-variation';
import { nonSignificantChanges, significantChangesMonitoringPlan } from '../about-variation';

@Component({
  selector: 'app-about-variation-summary-details',
  templateUrl: './summary-details.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryDetailsComponent implements OnInit {
  permitVariationDetails = this.store.getState().permitVariationDetails;
  nonSignificantChangesText = [];
  significantChangesMonitoringPlanText = [];
  significantChangesMonitoringMethodologyPlanText = [];

  constructor(readonly store: PermitApplicationStore) {}

  ngOnInit(): void {
    const modifications = this.permitVariationDetails?.modifications;

    const nonSignificantChangesKeys = modifications ? Object.keys(nonSignificantChanges) : [];
    this.nonSignificantChangesText = modifications
      ?.filter((change) => nonSignificantChangesKeys.includes(change.type))
      .map((change) =>
        change.type === 'OTHER_NON_SIGNFICANT' ? change.otherSummary : nonSignificantChanges[change.type],
      );

    const significantChangesMonitoringPlanKeys = modifications ? Object.keys(significantChangesMonitoringPlan) : [];
    this.significantChangesMonitoringPlanText = modifications
      ?.filter((change) => significantChangesMonitoringPlanKeys.includes(change.type))
      .map((change) =>
        change.type === 'OTHER_MONITORING_PLAN' ? change.otherSummary : significantChangesMonitoringPlan[change.type],
      );

    const significantChangesMonitoringMethodologyPlanKeys = modifications
      ? Object.keys(significantChangesMonitoringMethodologyPlan)
      : [];
    this.significantChangesMonitoringMethodologyPlanText = modifications
      ?.filter((change) => significantChangesMonitoringMethodologyPlanKeys.includes(change.type))
      .map((change) =>
        change.type === 'OTHER_MONITORING_METHODOLOGY_PLAN'
          ? change.otherSummary
          : significantChangesMonitoringMethodologyPlan[change.type],
      );
  }
}
