import { PermitRevocationState } from '../../../store/permit-revocation.state';

export type SectionStatus = 'not started' | 'in progress' | 'complete';

export function resolveConfirmSectionStatus(state: PermitRevocationState): SectionStatus {
  return state.cessationCompleted ? 'complete' : state.cessation ? 'in progress' : 'not started';
}
