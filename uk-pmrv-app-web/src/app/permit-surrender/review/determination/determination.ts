import {
  PermitSurrenderReviewDetermination,
  PermitSurrenderReviewDeterminationGrant,
  PermitSurrenderReviewDeterminationReject,
} from 'pmrv-api';

import { isWizardComplete as isGrantWizardComplete } from './grant/wizard';
import { isWizardComplete as isRejectWizardComplete } from './reject/wizard';

export function isWizardComplete(determination: PermitSurrenderReviewDetermination): boolean {
  if (!determination?.type) {
    return false;
  }
  switch (determination.type) {
    case 'GRANTED':
      return isGrantWizardComplete(determination as PermitSurrenderReviewDeterminationGrant);
    case 'REJECTED':
      return isRejectWizardComplete(determination as PermitSurrenderReviewDeterminationReject);
  }
}
