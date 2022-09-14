import { PermitRevocationState } from '@permit-revocation/store/permit-revocation.state';
import { TaskItemStatus } from '@shared/task-list/task-list.interface';
import moment from 'moment';

export function resolveApplyStatus(state: PermitRevocationState): TaskItemStatus {
  const permitRevocation = state.permitRevocation;
  return !checkForReviewStatus(state)
    ? 'needs review'
    : state.sectionsCompleted?.REVOCATION_APPLY
    ? 'complete'
    : permitRevocation?.reason !== undefined
    ? 'in progress'
    : 'not started';
}

export function resolveWithDrawStatus(state: PermitRevocationState): TaskItemStatus {
  return state?.reason ? 'complete' : 'not started';
}

// Check if the effective date of the permit revocation notice is at least 28days after today
// Also checks if fee date is after effective date and returns a boolean
export const checkForReviewStatus = (state: PermitRevocationState): boolean => {
  const effectiveDate = state?.permitRevocation?.effectiveDate;
  const feeDate = state?.permitRevocation?.feeDate;

  const add28Days = moment().add(28, 'd');
  const setHours = add28Days.set({ hour: 0, minute: 0, second: 0, millisecond: 0 });
  setHours.toISOString();

  const minDate = moment(setHours).format('YYYY-MM-DD');
  return effectiveDate && feeDate
    ? moment(feeDate).isAfter(effectiveDate) && moment(minDate).isSameOrBefore(effectiveDate)
    : effectiveDate
    ? moment(minDate).isSameOrBefore(effectiveDate)
    : true;
};
