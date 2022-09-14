import { InjectionToken } from '@angular/core';
import { AbstractControl, AsyncValidatorFn, FormBuilder, FormGroup, ValidationErrors } from '@angular/forms';

import { map, Observable } from 'rxjs';

import { AddressInputComponent } from '@shared/address-input/address-input.component';
import { LegalEntity, LegalEntityDetails, LegalEntitySelect } from '@shared/interfaces/legal-entity';
import { GroupBuilderConfig } from '@shared/types';
import { RecursivePartial } from '@shared/types/recursive-partial.type';

import { GovukValidators } from 'govuk-components';

import { LegalEntitiesService } from 'pmrv-api';

export const LEGAL_ENTITY_FORM = new InjectionToken<FormGroup>('Legal entity form');

export const legalEntityFormFactory = {
  provide: LEGAL_ENTITY_FORM,
  useFactory: (fb: FormBuilder, legalEntitiesService: LegalEntitiesService) =>
    fb.group({
      selectGroup: fb.group(selectGroupControls),
      detailsGroup: fb.group({
        ...infoControls,
        name: [
          null,
          [
            GovukValidators.required('Enter the operator name'),
            GovukValidators.maxLength(255, 'The legal entity name should not be more than 255 characters'),
          ],
          [legalEntityNameNotExists(legalEntitiesService)],
        ],
        address: fb.group(AddressInputComponent.controlsFactory(null)),
      }),
    }),
  deps: [FormBuilder, LegalEntitiesService],
};

export const legalEntityInitialValue: RecursivePartial<LegalEntity> = {
  selectGroup: { isNew: false },
};

const selectGroupControls: GroupBuilderConfig<LegalEntitySelect> = {
  isNew: [legalEntityInitialValue.selectGroup.isNew ?? null],
  id: [null, GovukValidators.required('Select an existing legal entity or create a new one')],
};

const infoControls: GroupBuilderConfig<LegalEntityDetails> = {
  type: [null, GovukValidators.required('Select the operator type')],
  referenceNumber: [null, GovukValidators.maxLength(15, 'Company registration number must be 15 characters or less')],
  noReferenceNumberReason: [
    null,
    GovukValidators.maxLength(500, 'Your explanation should not be more than 500 characters'),
  ],
};

function legalEntityNameNotExists(legalEntitiesService: LegalEntitiesService): AsyncValidatorFn {
  return (control: AbstractControl): Observable<ValidationErrors | null> =>
    legalEntitiesService.isExistingLegalEntityNameUsingGET(control.value).pipe(
      map((res) =>
        res
          ? {
              legalEntityExists:
                'The legal entity name already exists. Return to previous page and select this legal entity from the legal entity list or enter a new legal entity on this page.',
            }
          : null,
      ),
    );
}
