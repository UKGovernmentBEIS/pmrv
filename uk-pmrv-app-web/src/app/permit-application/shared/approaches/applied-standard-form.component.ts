import { Component } from '@angular/core';
import { ControlContainer, FormControl, FormGroupDirective } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { AppliedStandard } from 'pmrv-api';

import { GroupBuilderConfig } from '../../../shared/types';

// eslint-disable-next-line @angular-eslint/prefer-on-push-component-change-detection
@Component({
  selector: 'app-applied-standard-form',
  templateUrl: './applied-standard-form.component.html',
  viewProviders: [{ provide: ControlContainer, useExisting: FormGroupDirective }],
})
export class AppliedStandardFormComponent {
  static controlsFactory(appliedStandard: AppliedStandard, disabled = false): GroupBuilderConfig<AppliedStandard> {
    return {
      parameter: new FormControl({ value: appliedStandard?.parameter ?? null, disabled }, [
        GovukValidators.required('Enter a parameter'),
        GovukValidators.maxLength(250, 'Enter up to 250 characters'),
      ]),
      appliedStandard: new FormControl({ value: appliedStandard?.appliedStandard ?? null, disabled }, [
        GovukValidators.required('Enter an applied standard'),
        GovukValidators.maxLength(50, 'Enter up to 50 characters'),
      ]),
      deviationFromAppliedStandardExist: new FormControl(
        { value: appliedStandard?.deviationFromAppliedStandardExist ?? null, disabled },
        { validators: GovukValidators.required('Select Yes or No'), updateOn: 'change' },
      ),
      deviationFromAppliedStandardDetails: new FormControl(
        {
          value: appliedStandard?.deviationFromAppliedStandardDetails ?? null,
          disabled: !appliedStandard?.deviationFromAppliedStandardExist,
        },
        [GovukValidators.required('Enter details'), GovukValidators.maxLength(50, 'Enter up to 50 characters')],
      ),
      laboratoryName: new FormControl({ value: appliedStandard?.laboratoryName ?? null, disabled }, [
        GovukValidators.required('Enter a laboratory name'),
        GovukValidators.maxLength(250, 'Enter up to 250 characters'),
      ]),
      laboratoryAccredited: new FormControl(
        { value: appliedStandard?.laboratoryAccredited ?? null, disabled },
        { validators: GovukValidators.required('Select Yes or No'), updateOn: 'change' },
      ),
      laboratoryAccreditationEvidence: new FormControl(
        {
          value: appliedStandard?.laboratoryAccreditationEvidence ?? null,
          disabled: !!appliedStandard?.laboratoryAccredited,
        },
        [GovukValidators.required('Enter evidence'), GovukValidators.maxLength(10000, 'Enter up to 10000 characters')],
      ),
    };
  }
}
