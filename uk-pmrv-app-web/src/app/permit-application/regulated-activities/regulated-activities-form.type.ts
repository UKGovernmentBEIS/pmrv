import { RegulatedActivitiesFormGroup } from '@shared/components/regulated-activities/regulated-activities-form-options';

import { RegulatedActivity } from 'pmrv-api';

export type RegulatedActivitiesForm = RegulatedActivitiesFormGroup & RegulatedActivitiesFormCapacity;

type RegulatedActivityCapacity = {
  [RegulatedActivityType in RegulatedActivity['type'] as `${RegulatedActivityType}_CAPACITY`]: number;
};

type RegulatedActivityCapacityUnit = {
  [RegulatedActivityType in RegulatedActivity['type'] as `${RegulatedActivityType}_CAPACITY_UNIT`]: RegulatedActivity['capacityUnit'];
};

type RegulatedActivitiesFormCapacity = RegulatedActivityCapacity & RegulatedActivityCapacityUnit;
