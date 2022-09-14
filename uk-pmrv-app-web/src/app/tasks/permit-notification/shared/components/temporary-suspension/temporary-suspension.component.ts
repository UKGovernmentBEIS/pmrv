import { Component, Input } from '@angular/core';
import { FormControl } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { TemporarySuspension } from 'pmrv-api';

import { existingControlContainer } from '../../../../../shared/providers/control-container.factory';
import { GroupBuilderConfig } from '../../../../../shared/types';

// eslint-disable-next-line @angular-eslint/prefer-on-push-component-change-detection
@Component({
  selector: 'app-temporary-suspension',
  templateUrl: './temporary-suspension.component.html',
  providers: [existingControlContainer],
  viewProviders: [existingControlContainer],
})
export class TemporarySuspensionComponent {
  @Input() today: Date;

  static controlsFactory(notification: TemporarySuspension, disabled = false): GroupBuilderConfig<TemporarySuspension> {
    return {
      description: new FormControl({ value: notification?.description ?? null, disabled }, [
        GovukValidators.required('Enter the regulated activities which are temporarily suspended'),
        GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
      ]),
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
        [GovukValidators.required('Enter a date')],
      ),
    };
  }
}
