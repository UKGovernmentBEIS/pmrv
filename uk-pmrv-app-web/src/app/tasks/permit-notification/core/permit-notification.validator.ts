import { FormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';

import { GovukDatePipe } from '../../../shared/pipes/govuk-date.pipe';

export function endDateValidator(): ValidatorFn {
  return (group: FormGroup): ValidationErrors => {
    const startDate = group.get('startDateOfNonCompliance').value;
    const endDate = group.get('endDateOfNonCompliance').value;

    const govukDatePipe = new GovukDatePipe();
    const startDateToString = govukDatePipe.transform(new Date(startDate));

    if (startDate && endDate && endDate <= startDate) {
      group.controls['endDateOfNonCompliance'].setErrors({
        invalidDate: `The date cannot be earlier or the same date with ${startDateToString}`,
      });
      // if the start date is null or is before the end date then remove the error
    } else if (!startDate || (startDate && endDate && endDate > startDate)) {
      deleteInvalidDateError(group);
    }

    return null;
  };
}

export function endDateConditionalValidator(): ValidatorFn {
  return (group: FormGroup): ValidationErrors => {
    const reportingType = group.get('reportingType').value;

    if (
      reportingType === 'OTHER_ISSUE' &&
      group.get('startDateOfNonCompliance_OTHER_ISSUE').value &&
      group.get('endDateOfNonCompliance').value
    ) {
      const startDate = group.get('startDateOfNonCompliance_OTHER_ISSUE').value;
      const endDate = group.get('endDateOfNonCompliance').value;

      const govukDatePipe = new GovukDatePipe();
      const startDateToString = govukDatePipe.transform(new Date(startDate));

      if (startDate && endDate && endDate <= startDate) {
        group.controls['endDateOfNonCompliance'].setErrors({
          invalidDate: `The date cannot be earlier or the same date with ${startDateToString}`,
        });
        // if the start date is null or is before the endDate then remove the error
      } else if (startDate && endDate && endDate > startDate) {
        deleteInvalidDateError(group);
      }
    } else if (
      reportingType === 'OTHER_ISSUE' &&
      !group.get('startDateOfNonCompliance_OTHER_ISSUE').value &&
      group.get('endDateOfNonCompliance').value
    ) {
      deleteInvalidDateError(group);
    }

    return null;
  };
}

function deleteInvalidDateError(group: FormGroup) {
  const errors = group.controls['endDateOfNonCompliance'].errors;
  if (errors) {
    delete errors.invalidDate;
  }
  if (errors && Object.keys(errors).length === 0) {
    group.controls['endDateOfNonCompliance'].setErrors(null);
  }
}
