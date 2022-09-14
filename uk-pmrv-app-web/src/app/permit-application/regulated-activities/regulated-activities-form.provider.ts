import { FormBuilder, FormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';

import { RegulatedActivitiesForm } from '@permit-application/regulated-activities/regulated-activities-form.type';
import {
  formGroupOptions,
  RegulatedActivitiesFormGroup,
} from '@shared/components/regulated-activities/regulated-activities-form-options';
import { GroupBuilderConfig } from '@shared/types';

import { GovukValidators } from 'govuk-components';

import { RegulatedActivity } from 'pmrv-api';

import { PERMIT_TASK_FORM } from '../shared/permit-task-form.token';
import { PermitApplicationStore } from '../store/permit-application.store';

export const regulatedActivitiesFormProvider = {
  provide: PERMIT_TASK_FORM,
  deps: [FormBuilder, PermitApplicationStore],
  useFactory: (fb: FormBuilder, store: PermitApplicationStore) => {
    const state = store.getState();
    const group = fb.group(controlsConfig(mapToForm(state.permit.regulatedActivities)), {
      validators: atLeastOneValidator(),
    });

    if (!state.isEditable) {
      group.disable();
    }

    return group;
  },
};

function controlsConfig(form: Partial<RegulatedActivitiesForm>): GroupBuilderConfig<RegulatedActivitiesForm> {
  return {
    COMBUSTION_GROUP: [form?.COMBUSTION_GROUP ?? null],
    REFINING_GROUP: [form?.REFINING_GROUP ?? null],
    METAL_GROUP: [form?.METAL_GROUP ?? null],
    MINERAL_GROUP: [form?.MINERAL_GROUP ?? null],
    GLASS_GROUP: [form?.GLASS_GROUP ?? null],
    PULP_GROUP: [form?.PULP_GROUP ?? null],
    CHEMICAL_GROUP: [form?.CHEMICAL_GROUP ?? null],
    CARBON_GROUP: [form?.CARBON_GROUP ?? null],
    COMBUSTION_CAPACITY: [
      form?.COMBUSTION_CAPACITY ?? null,
      [GovukValidators.required('Enter the capacity for combustion'), GovukValidators.positiveNumber()],
    ],
    COMBUSTION_CAPACITY_UNIT: [
      form?.COMBUSTION_CAPACITY_UNIT ?? null,
      [GovukValidators.required('Enter the capacity unit for combustion')],
    ],

    MINERAL_OIL_REFINING_CAPACITY: [
      form?.MINERAL_OIL_REFINING_CAPACITY ?? null,
      [GovukValidators.required('Enter the capacity for mineral oil refining'), GovukValidators.positiveNumber()],
    ],
    MINERAL_OIL_REFINING_CAPACITY_UNIT: [
      form?.MINERAL_OIL_REFINING_CAPACITY_UNIT ?? null,
      [GovukValidators.required('Enter the capacity unit for mineral oil refining')],
    ],

    COKE_PRODUCTION_CAPACITY: [
      form?.COKE_PRODUCTION_CAPACITY ?? null,
      [GovukValidators.required('Enter the capacity for production of coke'), GovukValidators.positiveNumber()],
    ],
    COKE_PRODUCTION_CAPACITY_UNIT: [
      form?.COKE_PRODUCTION_CAPACITY_UNIT ?? null,
      [GovukValidators.required('Enter the capacity unit for production of coke')],
    ],

    ORE_ROASTING_OR_SINTERING_CAPACITY: [
      form?.ORE_ROASTING_OR_SINTERING_CAPACITY ?? null,
      [
        GovukValidators.required('Enter the capacity for metal ore roasting or sintering'),
        GovukValidators.positiveNumber(),
      ],
    ],
    ORE_ROASTING_OR_SINTERING_CAPACITY_UNIT: [
      form?.ORE_ROASTING_OR_SINTERING_CAPACITY_UNIT ?? null,
      [GovukValidators.required('Enter the capacity unit for metal ore roasting or sintering')],
    ],

    PIG_IRON_STEEL_PRODUCTION_CAPACITY: [
      form?.PIG_IRON_STEEL_PRODUCTION_CAPACITY ?? null,
      [
        GovukValidators.required('Enter the capacity for production of pig iron or steel'),
        GovukValidators.positiveNumber(),
      ],
    ],
    PIG_IRON_STEEL_PRODUCTION_CAPACITY_UNIT: [
      form?.PIG_IRON_STEEL_PRODUCTION_CAPACITY_UNIT ?? null,
      [GovukValidators.required('Enter the capacity unit for production of pig iron or steel')],
    ],

    FERROUS_METALS_PRODUCTION_CAPACITY: [
      form?.FERROUS_METALS_PRODUCTION_CAPACITY ?? null,
      [
        GovukValidators.required('Enter the capacity for production or processing of ferrous metals'),
        GovukValidators.positiveNumber(),
      ],
    ],
    FERROUS_METALS_PRODUCTION_CAPACITY_UNIT: [
      form?.FERROUS_METALS_PRODUCTION_CAPACITY_UNIT ?? null,
      [GovukValidators.required('Enter the capacity unit for production or processing of ferrous metals')],
    ],

    NON_FERROUS_METALS_PRODUCTION_CAPACITY: [
      form?.NON_FERROUS_METALS_PRODUCTION_CAPACITY ?? null,
      [
        GovukValidators.required('Enter the capacity for production or processing of non-ferrous metals'),
        GovukValidators.positiveNumber(),
      ],
    ],
    NON_FERROUS_METALS_PRODUCTION_CAPACITY_UNIT: [
      form?.NON_FERROUS_METALS_PRODUCTION_CAPACITY_UNIT ?? null,
      [GovukValidators.required('Enter the capacity unit for production or processing of non-ferrous metals')],
    ],

    PRIMARY_ALUMINIUM_PRODUCTION_CAPACITY: [
      form?.PRIMARY_ALUMINIUM_PRODUCTION_CAPACITY ?? null,
      [
        GovukValidators.required('Enter the capacity for production of primary aluminium'),
        GovukValidators.positiveNumber(),
      ],
    ],
    PRIMARY_ALUMINIUM_PRODUCTION_CAPACITY_UNIT: [
      form?.PRIMARY_ALUMINIUM_PRODUCTION_CAPACITY_UNIT ?? null,
      [GovukValidators.required('Enter the capacity unit for production of primary aluminium')],
    ],

    SECONDARY_ALUMINIUM_PRODUCTION_CAPACITY: [
      form?.SECONDARY_ALUMINIUM_PRODUCTION_CAPACITY ?? null,
      [
        GovukValidators.required('Enter the capacity for production of secondary aluminium'),
        GovukValidators.positiveNumber(),
      ],
    ],
    SECONDARY_ALUMINIUM_PRODUCTION_CAPACITY_UNIT: [
      form?.SECONDARY_ALUMINIUM_PRODUCTION_CAPACITY_UNIT ?? null,
      [GovukValidators.required('Enter the capacity unit for production of secondary aluminium')],
    ],

    CEMENT_CLINKER_PRODUCTION_CAPACITY: [
      form?.CEMENT_CLINKER_PRODUCTION_CAPACITY ?? null,
      [
        GovukValidators.required('Enter the capacity for production of cement clinker'),
        GovukValidators.positiveNumber(),
      ],
    ],
    CEMENT_CLINKER_PRODUCTION_CAPACITY_UNIT: [
      form?.CEMENT_CLINKER_PRODUCTION_CAPACITY_UNIT ?? null,
      [GovukValidators.required('Enter the capacity unit for production of cement clinker')],
    ],

    LIME_OR_CALCINATION_OF_DOLOMITE_OR_MAGNESITE_CAPACITY: [
      form?.LIME_OR_CALCINATION_OF_DOLOMITE_OR_MAGNESITE_CAPACITY ?? null,
      [
        GovukValidators.required('Enter the capacity for production of lime or calcination of dolomite or magnesite'),
        GovukValidators.positiveNumber(),
      ],
    ],
    LIME_OR_CALCINATION_OF_DOLOMITE_OR_MAGNESITE_CAPACITY_UNIT: [
      form?.LIME_OR_CALCINATION_OF_DOLOMITE_OR_MAGNESITE_CAPACITY_UNIT ?? null,
      [
        GovukValidators.required(
          'Enter the capacity unit for production of lime or calcination of dolomite or magnesite',
        ),
      ],
    ],

    CERAMICS_MANUFACTURING_CAPACITY: [
      form?.CERAMICS_MANUFACTURING_CAPACITY ?? null,
      [GovukValidators.required('Enter the capacity for manufacture of ceramics'), GovukValidators.positiveNumber()],
    ],
    CERAMICS_MANUFACTURING_CAPACITY_UNIT: [
      form?.CERAMICS_MANUFACTURING_CAPACITY_UNIT ?? null,
      [GovukValidators.required('Enter the capacity unit for manufacture of ceramics')],
    ],

    GYPSUM_OR_PLASTERBOARD_PRODUCTION_CAPACITY: [
      form?.GYPSUM_OR_PLASTERBOARD_PRODUCTION_CAPACITY ?? null,
      [
        GovukValidators.required('Enter the capacity for production or processing of gypsum or plasterboard'),
        GovukValidators.positiveNumber(),
      ],
    ],
    GYPSUM_OR_PLASTERBOARD_PRODUCTION_CAPACITY_UNIT: [
      form?.GYPSUM_OR_PLASTERBOARD_PRODUCTION_CAPACITY_UNIT ?? null,
      [GovukValidators.required('Enter the capacity unit for production or processing of gypsum or plasterboard')],
    ],

    GLASS_MANUFACTURING_CAPACITY: [
      form?.GLASS_MANUFACTURING_CAPACITY ?? null,
      [GovukValidators.required('Enter the capacity for manufacture of glass'), GovukValidators.positiveNumber()],
    ],
    GLASS_MANUFACTURING_CAPACITY_UNIT: [
      form?.GLASS_MANUFACTURING_CAPACITY_UNIT ?? null,
      [GovukValidators.required('Enter the capacity unit for manufacture of glass')],
    ],

    MINERAL_WOOL_MANUFACTURING_CAPACITY: [
      form?.MINERAL_WOOL_MANUFACTURING_CAPACITY ?? null,
      [
        GovukValidators.required('Enter the capacity for manufacture of mineral wool'),
        GovukValidators.positiveNumber(),
      ],
    ],
    MINERAL_WOOL_MANUFACTURING_CAPACITY_UNIT: [
      form?.MINERAL_WOOL_MANUFACTURING_CAPACITY_UNIT ?? null,
      [GovukValidators.required('Enter the capacity unit for manufacture of mineral wool')],
    ],

    PULP_PRODUCTION_CAPACITY: [
      form?.PULP_PRODUCTION_CAPACITY ?? null,
      [GovukValidators.required('Enter the capacity for production of pulp'), GovukValidators.positiveNumber()],
    ],
    PULP_PRODUCTION_CAPACITY_UNIT: [
      form?.PULP_PRODUCTION_CAPACITY_UNIT ?? null,
      [GovukValidators.required('Enter the capacity unit for production of pulp')],
    ],

    PAPER_OR_CARDBOARD_PRODUCTION_CAPACITY: [
      form?.PAPER_OR_CARDBOARD_PRODUCTION_CAPACITY ?? null,
      [
        GovukValidators.required('Enter the capacity for production of paper or cardboard'),
        GovukValidators.positiveNumber(),
      ],
    ],
    PAPER_OR_CARDBOARD_PRODUCTION_CAPACITY_UNIT: [
      form?.PAPER_OR_CARDBOARD_PRODUCTION_CAPACITY_UNIT ?? null,
      [GovukValidators.required('Enter the capacity unit for production of paper or cardboard')],
    ],

    CARBON_BLACK_PRODUCTION_CAPACITY: [
      form?.CARBON_BLACK_PRODUCTION_CAPACITY ?? null,
      [GovukValidators.required('Enter the capacity for production of carbon black'), GovukValidators.positiveNumber()],
    ],
    CARBON_BLACK_PRODUCTION_CAPACITY_UNIT: [
      form?.CARBON_BLACK_PRODUCTION_CAPACITY_UNIT ?? null,
      [GovukValidators.required('Enter the capacity unit for production of carbon black')],
    ],

    BULK_ORGANIC_CHEMICAL_PRODUCTION_CAPACITY: [
      form?.BULK_ORGANIC_CHEMICAL_PRODUCTION_CAPACITY ?? null,
      [
        GovukValidators.required('Enter the capacity for production of bulk organic chemicals'),
        GovukValidators.positiveNumber(),
      ],
    ],
    BULK_ORGANIC_CHEMICAL_PRODUCTION_CAPACITY_UNIT: [
      form?.BULK_ORGANIC_CHEMICAL_PRODUCTION_CAPACITY_UNIT ?? null,
      [GovukValidators.required('Enter the capacity unit for production of bulk organic chemicals')],
    ],

    GLYOXAL_GLYOXYLIC_ACID_PRODUCTION_CAPACITY: [
      form?.GLYOXAL_GLYOXYLIC_ACID_PRODUCTION_CAPACITY ?? null,
      [
        GovukValidators.required('Enter the capacity for production of glyoxal and glyoxylic acid'),
        GovukValidators.positiveNumber(),
      ],
    ],
    GLYOXAL_GLYOXYLIC_ACID_PRODUCTION_CAPACITY_UNIT: [
      form?.GLYOXAL_GLYOXYLIC_ACID_PRODUCTION_CAPACITY_UNIT ?? null,
      [GovukValidators.required('Enter the capacity unit for production of glyoxal and glyoxylic acid')],
    ],

    NITRIC_ACID_PRODUCTION_CAPACITY: [
      form?.NITRIC_ACID_PRODUCTION_CAPACITY ?? null,
      [GovukValidators.required('Enter the capacity for production of nitric acid'), GovukValidators.positiveNumber()],
    ],
    NITRIC_ACID_PRODUCTION_CAPACITY_UNIT: [
      form?.NITRIC_ACID_PRODUCTION_CAPACITY_UNIT ?? null,
      [GovukValidators.required('Enter the capacity unit for production of nitric acid')],
    ],

    ADIPIC_ACID_PRODUCTION_CAPACITY: [
      form?.ADIPIC_ACID_PRODUCTION_CAPACITY ?? null,
      [GovukValidators.required('Enter the capacity for production of adipic acid'), GovukValidators.positiveNumber()],
    ],
    ADIPIC_ACID_PRODUCTION_CAPACITY_UNIT: [
      form?.ADIPIC_ACID_PRODUCTION_CAPACITY_UNIT ?? null,
      [GovukValidators.required('Enter the capacity unit for production of adipic acid')],
    ],

    AMMONIA_PRODUCTION_CAPACITY: [
      form?.AMMONIA_PRODUCTION_CAPACITY ?? null,
      [GovukValidators.required('Enter the capacity for production of ammonia'), GovukValidators.positiveNumber()],
    ],
    AMMONIA_PRODUCTION_CAPACITY_UNIT: [
      form?.AMMONIA_PRODUCTION_CAPACITY_UNIT ?? null,
      [GovukValidators.required('Enter the capacity unit for production of ammonia')],
    ],

    SODA_ASH_AND_SODIUM_BICARBONATE_PRODUCTION_CAPACITY: [
      form?.SODA_ASH_AND_SODIUM_BICARBONATE_PRODUCTION_CAPACITY ?? null,
      [
        GovukValidators.required('Enter the capacity for production of soda ash and sodium bicarbonate'),
        GovukValidators.positiveNumber(),
      ],
    ],
    SODA_ASH_AND_SODIUM_BICARBONATE_PRODUCTION_CAPACITY_UNIT: [
      form?.SODA_ASH_AND_SODIUM_BICARBONATE_PRODUCTION_CAPACITY_UNIT ?? null,
      [GovukValidators.required('Enter the capacity unit for production of soda ash and sodium bicarbonate')],
    ],

    HYDROGEN_AND_SYNTHESIS_GAS_PRODUCTION_CAPACITY: [
      form?.HYDROGEN_AND_SYNTHESIS_GAS_PRODUCTION_CAPACITY ?? null,
      [
        GovukValidators.required('Enter the capacity for production of hydrogen and synthesis gas'),
        GovukValidators.positiveNumber(),
      ],
    ],
    HYDROGEN_AND_SYNTHESIS_GAS_PRODUCTION_CAPACITY_UNIT: [
      form?.HYDROGEN_AND_SYNTHESIS_GAS_PRODUCTION_CAPACITY_UNIT ?? null,
      [GovukValidators.required('Enter the capacity unit for production of hydrogen and synthesis gas')],
    ],

    CAPTURE_OF_GREENHOUSE_GASES_UNDER_DIRECTIVE_CAPACITY: [
      form?.CAPTURE_OF_GREENHOUSE_GASES_UNDER_DIRECTIVE_CAPACITY ?? null,
      [
        GovukValidators.required('Enter the capacity for capture of greenhouse gases'),
        GovukValidators.positiveNumber(),
      ],
    ],
    CAPTURE_OF_GREENHOUSE_GASES_UNDER_DIRECTIVE_CAPACITY_UNIT: [
      form?.CAPTURE_OF_GREENHOUSE_GASES_UNDER_DIRECTIVE_CAPACITY_UNIT ?? null,
      [GovukValidators.required('Enter the capacity unit for capture of greenhouse gases')],
    ],

    TRANSPORT_OF_GREENHOUSE_GASES_UNDER_DIRECTIVE_CAPACITY: [
      form?.TRANSPORT_OF_GREENHOUSE_GASES_UNDER_DIRECTIVE_CAPACITY ?? null,
      [
        GovukValidators.required('Enter the capacity for transport of greenhouse gases'),
        GovukValidators.positiveNumber(),
      ],
    ],
    TRANSPORT_OF_GREENHOUSE_GASES_UNDER_DIRECTIVE_CAPACITY_UNIT: [
      form?.TRANSPORT_OF_GREENHOUSE_GASES_UNDER_DIRECTIVE_CAPACITY_UNIT ?? null,
      [GovukValidators.required('Enter the capacity unit for transport of greenhouse gases')],
    ],

    STORAGE_OF_GREENHOUSE_GASES_UNDER_DIRECTIVE_CAPACITY: [
      form?.STORAGE_OF_GREENHOUSE_GASES_UNDER_DIRECTIVE_CAPACITY ?? null,
      [GovukValidators.required('Enter the capacity for transport of storage gases'), GovukValidators.positiveNumber()],
    ],
    STORAGE_OF_GREENHOUSE_GASES_UNDER_DIRECTIVE_CAPACITY_UNIT: [
      form?.STORAGE_OF_GREENHOUSE_GASES_UNDER_DIRECTIVE_CAPACITY_UNIT ?? null,
      [GovukValidators.required('Enter the capacity unit for transport of storage gases')],
    ],
  };
}

function atLeastOneValidator(): ValidatorFn {
  return (group: FormGroup): ValidationErrors =>
    Object.values(group.controls)
      .map((control) => control.value)
      .filter((value) => Array.isArray(value))
      .some((value) => value.length > 0)
      ? null
      : { atLeastOne: 'Select all the regulated activities carried out the installation' };
}

function mapToForm(regulatedActivities: RegulatedActivity[]): Partial<RegulatedActivitiesForm> {
  return regulatedActivities?.reduce(
    (form: Partial<RegulatedActivitiesForm>, activity) => ({
      ...form,
      ...(activity.capacity ? { [`${activity.type}_CAPACITY`]: activity.capacity } : null),
      ...(activity.capacityUnit ? { [`${activity.type}_CAPACITY_UNIT`]: activity.capacityUnit } : null),
      [findGroupByType(activity.type)]: [...(form?.[findGroupByType(activity.type)] ?? []), ...[activity.type]],
    }),
    {},
  );
}

function findTypesByGroupName(groupName: keyof RegulatedActivitiesFormGroup): RegulatedActivity['type'][] {
  return formGroupOptions[groupName];
}

function findGroupByType(type: RegulatedActivity['type']): keyof RegulatedActivitiesFormGroup {
  return Object.keys(formGroupOptions).find(
    (key: keyof RegulatedActivitiesFormGroup): key is keyof RegulatedActivitiesFormGroup =>
      findTypesByGroupName(key).includes(type),
  );
}
