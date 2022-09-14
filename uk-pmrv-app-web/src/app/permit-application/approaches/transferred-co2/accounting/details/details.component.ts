import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, Observable, pluck, switchMap } from 'rxjs';

import { AccountingEmissionsDetails, TransferredCO2MonitoringApproach } from 'pmrv-api';

import { PendingRequestService } from '../../../../../core/guards/pending-request.service';
import { PendingRequest } from '../../../../../core/interfaces/pending-request.interface';
import { PERMIT_TASK_FORM } from '../../../../shared/permit-task-form.token';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { detailsFormProvider } from './details-form.provider';

@Component({
  selector: 'app-details',
  templateUrl: './details.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [detailsFormProvider],
})
export class DetailsComponent implements PendingRequest {
  typeOptions: { value: AccountingEmissionsDetails['samplingFrequency']; label: string }[] = [
    { value: 'CONTINUOUS', label: 'Continuous' },
    { value: 'DAILY', label: 'Daily' },
    { value: 'WEEKLY', label: 'Weekly' },
    { value: 'MONTHLY', label: 'Monthly' },
    { value: 'BI_ANNUALLY', label: 'Bi annually' },
    { value: 'ANNUALLY', label: 'Annually' },
  ];
  estimatedAnnualEmissions$: Observable<number> = this.store
    .findTask('estimatedAnnualEmissions')
    .pipe(pluck('quantity'));

  constructor(
    @Inject(PERMIT_TASK_FORM) readonly form: FormGroup,
    readonly store: PermitApplicationStore,
    readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onContinue(): void {
    if (!this.form.dirty) {
      this.router.navigate(['../justification'], { relativeTo: this.route });
    } else {
      combineLatest([this.route.data, this.store.getTask('monitoringApproaches')])
        .pipe(
          first(),
          switchMap(([data, monitoringApproaches]) =>
            this.store.patchTask(
              data.taskKey,
              {
                chemicallyBound: false,
                ...(this.isHighestRequiredTierFalse()
                  ? {
                      accountingEmissionsDetails: {
                        ...(monitoringApproaches.TRANSFERRED_CO2 as TransferredCO2MonitoringApproach)
                          .accountingEmissions.accountingEmissionsDetails,
                        ...this.getData(),
                      },
                    }
                  : {
                      accountingEmissionsDetails: this.getData(),
                    }),
              },
              false,
              data.statusKey,
            ),
          ),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.router.navigate(['../justification'], { relativeTo: this.route }));
    }
  }

  private getData(): AccountingEmissionsDetails {
    return {
      measurementDevicesOrMethods: this.form.value.measurementDevicesOrMethods,
      samplingFrequency: this.form.value.samplingFrequency,
      otherSamplingFrequency: this.form.value.otherSamplingFrequency,
      tier: this.form.value.tier,
      ...{
        isHighestRequiredTier:
          this.form.value.isHighestRequiredTierT1 ??
          this.form.value.isHighestRequiredTierT2 ??
          this.form.value.isHighestRequiredTierT3,
      },
      noTierJustification: this.form.value.noTierJustification,
    } as AccountingEmissionsDetails;
  }

  private isHighestRequiredTierFalse(): boolean {
    return (
      this.form.value.isHighestRequiredTierT1 === false ||
      this.form.value.isHighestRequiredTierT2 === false ||
      this.form.value.isHighestRequiredTierT3 === false
    );
  }
}
