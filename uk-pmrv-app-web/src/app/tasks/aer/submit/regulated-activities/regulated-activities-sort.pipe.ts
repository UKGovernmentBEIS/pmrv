import { Pipe, PipeTransform } from '@angular/core';

import { formGroupOptions } from '@shared/components/regulated-activities/regulated-activities-form-options';

import { AerRegulatedActivity } from 'pmrv-api';

@Pipe({
  name: 'regulatedActivitiesSort',
})
export class RegulatedActivitiesSortPipe implements PipeTransform {
  transform(array: Array<AerRegulatedActivity>): Array<AerRegulatedActivity> {
    if (!array) {
      return array;
    }
    const activities = Object.values(formGroupOptions).reduce((acc, value) => acc.concat(value), []);
    array.sort(function (a, b) {
      return activities.indexOf(a.type) - activities.indexOf(b.type);
    });
    return array;
  }
}
