import { PermitSurrenderState } from '../../store/permit-surrender.state';
import { SectionStatus } from './cessation';

export function resolveConfirmSectionStatus(state: PermitSurrenderState): SectionStatus {
  return state.cessationCompleted ? 'complete' : state.cessation ? 'in progress' : 'not started';
}
