import { Component } from '@angular/core';
import { FormControl } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { NonSignificantChange } from 'pmrv-api';

import { existingControlContainer } from '../../../../../shared/providers/control-container.factory';
import { GroupBuilderConfig } from '../../../../../shared/types';

// eslint-disable-next-line @angular-eslint/prefer-on-push-component-change-detection
@Component({
  selector: 'app-non-significant-change',
  templateUrl: './non-significant-change.component.html',
  providers: [existingControlContainer],
  viewProviders: [existingControlContainer],
})
export class NonSignificantChangeComponent {
  public readonly relatedChangesTypes = RelatedChangesTypes;

  static controlsFactory(notification: NonSignificantChange, disabled = false): GroupBuilderConfig<any> {
    return {
      relatedChanges: new FormControl(
        {
          value: [...(notification?.relatedChanges || [])],
          disabled,
        },
        [GovukValidators.required('Select one or more')],
      ),
      description: new FormControl({ value: notification?.description ?? null, disabled }, [
        GovukValidators.required('Enter description for the change'),
        GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
      ]),
    };
  }
}

enum RelatedChangesTypes {
  MonitoringPlan = 'MONITORING_PLAN',
  MonitoringMethodologyPlan = 'MONITORING_METHODOLOGY_PLAN',
}
