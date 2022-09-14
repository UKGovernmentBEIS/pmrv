import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, switchMap } from 'rxjs';

import { PendingRequestService } from '../../../../core/guards/pending-request.service';
import { PERMIT_TASK_FORM } from '../../../shared/permit-task-form.token';
import { PermitApplicationStore } from '../../../store/permit-application.store';
import { significantChangesMonitoringMethodologyPlan } from '../about-variation';
import { nonSignificantChanges, significantChangesMonitoringPlan } from '../about-variation';
import { changesFormProvider } from './changes-form.provider';

@Component({
  selector: 'app-changes',
  templateUrl: './changes.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [changesFormProvider],
})
export class ChangesComponent {
  nonSignificantChanges = nonSignificantChanges;
  nonSignificantChangesKeys = Object.keys(nonSignificantChanges);

  significantChangesMonitoringPlan = significantChangesMonitoringPlan;
  significantChangesMonitoringPlanKeys = Object.keys(significantChangesMonitoringPlan);

  significantChangesMonitoringMethodologyPlan = significantChangesMonitoringMethodologyPlan;
  significantChangesMonitoringMethodologyPlanKeys = Object.keys(significantChangesMonitoringMethodologyPlan);
  constructor(
    readonly store: PermitApplicationStore,
    @Inject(PERMIT_TASK_FORM) readonly form: FormGroup,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly pendingRequest: PendingRequestService,
  ) {}

  onContinue(): void {
    this.store
      .pipe(
        first(),
        switchMap((state) => {
          return this.store.postVariationDetails({
            ...state.permitVariationDetails,
            modifications: this.buildData(),
          });
        }),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => this.router.navigate(['../answers'], { relativeTo: this.route }));
  }

  private buildData() {
    const result = [];

    Object.entries(this.form.value).forEach(([key, values]) => {
      if (key === 'nonSignificantChanges') {
        (values as Array<string>).forEach((value) => {
          if (value !== 'OTHER_NON_SIGNFICANT') {
            result.push({ type: value });
          } else {
            result.push({ type: value, otherSummary: this.form.get('otherNonSignficantSummary').value });
          }
        });
      }

      if (key === 'significantChangesMonitoringPlan') {
        (values as Array<string>).forEach((value) => {
          if (value !== 'OTHER_MONITORING_PLAN') {
            result.push({ type: value });
          } else {
            result.push({ type: value, otherSummary: this.form.get('otherMonitoringPlanSummary').value });
          }
        });
      }

      if (key === 'significantChangesMonitoringMethodologyPlan') {
        (values as Array<string>).forEach((value) => {
          if (value !== 'OTHER_MONITORING_METHODOLOGY_PLAN') {
            result.push({ type: value });
          } else {
            result.push({ type: value, otherSummary: this.form.get('otherMonitoringMethodologyPlanSummary').value });
          }
        });
      }
    });

    return result;
  }
}
