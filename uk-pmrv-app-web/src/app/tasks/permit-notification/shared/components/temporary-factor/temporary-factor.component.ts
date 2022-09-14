import { Component, Input } from '@angular/core';
import { FormControl } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { TemporaryFactor } from 'pmrv-api';

import { existingControlContainer } from '../../../../../shared/providers/control-container.factory';
import { GroupBuilderConfig } from '../../../../../shared/types';

// eslint-disable-next-line @angular-eslint/prefer-on-push-component-change-detection
@Component({
  selector: 'app-temporary-factor',
  templateUrl: './temporary-factor.component.html',
  providers: [existingControlContainer],
  viewProviders: [existingControlContainer],
})
export class TemporaryFactorComponent {
  @Input() today: Date;

  static controlsFactory(notification: TemporaryFactor, disabled = false): GroupBuilderConfig<TemporaryFactor> {
    return {
      description: new FormControl(
        {
          value: notification?.description ?? null,
          disabled,
        },
        [
          GovukValidators.required('Enter the factors preventing compliance'),
          GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
        ],
      ),
      startDateOfNonCompliance: new FormControl(
        {
          value: notification?.startDateOfNonCompliance ? new Date(notification.startDateOfNonCompliance) : null,
          disabled,
        },
        [GovukValidators.required('Enter a date')],
      ),
      endDateOfNonCompliance: new FormControl(
        {
          value: notification?.endDateOfNonCompliance ? new Date(notification.endDateOfNonCompliance) : null,
          disabled,
        },
        [GovukValidators.required('Enter the date non-compliance expected to end')],
      ),
      inRespectOfMonitoringMethodology: new FormControl(
        {
          value: notification?.inRespectOfMonitoringMethodology ?? null,
          disabled,
        },
        [GovukValidators.required('Select Yes or No')],
      ),
      details: new FormControl(
        {
          value: notification?.details ?? null,
          disabled,
        },
        [
          GovukValidators.required('Enter details of the interim monitoring and reporting methodology adopted'),
          GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
        ],
      ),
      proof: new FormControl(
        {
          value: notification?.proof ?? null,
          disabled,
        },
        [
          GovukValidators.required(
            'Enter proof of the necessity for a change to the monitoring and reporting methodology',
          ),
          GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
        ],
      ),
      measures: new FormControl(
        {
          value: notification?.measures ?? null,
          disabled,
        },
        [
          GovukValidators.required('Enter the measures taken to ensure prompt restoration of compliance'),
          GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
        ],
      ),
    };
  }
}
