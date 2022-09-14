import { Component } from '@angular/core';
import { FormControl } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { ProcedureForm } from 'pmrv-api';

import { existingControlContainer } from '../../../shared/providers/control-container.factory';
import { GroupBuilderConfig } from '../../../shared/types';

// eslint-disable-next-line @angular-eslint/prefer-on-push-component-change-detection
@Component({
  selector: 'app-procedure-form',
  templateUrl: './procedure-form.component.html',
  providers: [existingControlContainer],
  viewProviders: [existingControlContainer],
})
export class ProcedureFormComponent {
  static controlsFactory(procedure: ProcedureForm, disabled = false): GroupBuilderConfig<ProcedureForm> {
    return {
      procedureDescription: new FormControl({ value: procedure?.procedureDescription ?? null, disabled }, [
        GovukValidators.required('Enter a brief description of the procedure'),
        GovukValidators.maxLength(10000, 'The procedure description should not be more than 10000 characters'),
      ]),
      procedureDocumentName: new FormControl({ value: procedure?.procedureDocumentName ?? null, disabled }, [
        GovukValidators.required('Enter the name of the procedure document'),
        GovukValidators.maxLength(1000, 'The procedure document name should not be more than 1000 characters'),
      ]),
      procedureReference: new FormControl({ value: procedure?.procedureReference ?? null, disabled }, [
        GovukValidators.required('Enter a procedure reference'),
        GovukValidators.maxLength(500, 'The procedure reference should not be more than 500 characters'),
      ]),
      diagramReference: new FormControl({ value: procedure?.diagramReference ?? null, disabled }, [
        GovukValidators.maxLength(500, 'The diagram reference should not be more than 500 characters'),
      ]),
      responsibleDepartmentOrRole: new FormControl(
        { value: procedure?.responsibleDepartmentOrRole ?? null, disabled },
        [
          GovukValidators.required('Enter the name of the department or role responsible'),
          GovukValidators.maxLength(
            1000,
            'The name of the department or role responsible should not be more than 1000 characters',
          ),
        ],
      ),
      locationOfRecords: new FormControl({ value: procedure?.locationOfRecords ?? null, disabled }, [
        GovukValidators.required('Enter the location of the records'),
        GovukValidators.maxLength(2000, 'The location of the records should not be more than 2000 characters'),
      ]),
      itSystemUsed: new FormControl({ value: procedure?.itSystemUsed ?? null, disabled }, [
        GovukValidators.maxLength(500, 'The IT system used should not be more than 500 characters'),
      ]),
      appliedStandards: new FormControl({ value: procedure?.appliedStandards ?? null, disabled }, [
        GovukValidators.maxLength(2000, 'The applied standards should not be more than 2000 characters'),
      ]),
    };
  }
}
