import { Pipe, PipeTransform } from '@angular/core';

import { ItemDTO } from 'pmrv-api';

@Pipe({ name: 'itemName' })
export class ItemNamePipe implements PipeTransform {
  transform(value: ItemDTO['taskType']): string {
    switch (value) {
      case 'ACCOUNT_USERS_SETUP':
        return 'Set up';
      case 'INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW':
      case 'INSTALLATION_ACCOUNT_OPENING_ARCHIVE':
      case 'PERMIT_ISSUANCE_APPLICATION_SUBMIT':
      case 'PERMIT_ISSUANCE_WAIT_FOR_REVIEW':
        return 'Application';
      case 'PERMIT_ISSUANCE_APPLICATION_REVIEW':
      case 'PERMIT_ISSUANCE_WAIT_FOR_AMENDS':
      case 'PERMIT_ISSUANCE_WAIT_FOR_PEER_REVIEW':
        return 'Permit determination';
      case 'PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW':
        return 'Permit peer review';
      case 'PERMIT_ISSUANCE_APPLICATION_AMENDS_SUBMIT':
        return 'Permit application';
      case 'PERMIT_REVOCATION_APPLICATION_SUBMIT':
      case 'PERMIT_REVOCATION_WAIT_FOR_PEER_REVIEW':
        return 'Revoke permit';
      case 'PERMIT_REVOCATION_WAIT_FOR_APPEAL':
        return 'Withdraw permit revocation';
      case 'PERMIT_REVOCATION_APPLICATION_PEER_REVIEW':
        return 'Revoke permit review';
      case 'PERMIT_REVOCATION_CESSATION_SUBMIT':
        return 'Revocation cessation';
      case 'PERMIT_SURRENDER_APPLICATION_SUBMIT':
      case 'PERMIT_SURRENDER_WAIT_FOR_REVIEW':
        return 'Surrender application';
      case 'PERMIT_SURRENDER_WAIT_FOR_PEER_REVIEW':
      case 'PERMIT_SURRENDER_APPLICATION_REVIEW':
        return 'Surrender determination';
      case 'PERMIT_SURRENDER_CESSATION_SUBMIT':
        return 'Surrender cessation';
      case 'PERMIT_SURRENDER_APPLICATION_PEER_REVIEW':
        return 'Surrender peer review';
      case 'NEW_VERIFICATION_BODY_INSTALLATION':
        return 'New installation';
      case 'VERIFIER_NO_LONGER_AVAILABLE':
        return 'Verifier is no longer available';
      case 'PERMIT_ISSUANCE_WAIT_FOR_RDE_RESPONSE':
      case 'PERMIT_ISSUANCE_RDE_RESPONSE_SUBMIT':
      case 'PERMIT_SURRENDER_WAIT_FOR_RDE_RESPONSE':
      case 'PERMIT_SURRENDER_RDE_RESPONSE_SUBMIT':
      case 'PERMIT_VARIATION_WAIT_FOR_RDE_RESPONSE':
      case 'PERMIT_VARIATION_RDE_RESPONSE_SUBMIT':
        return 'Extension request';
      case 'PERMIT_ISSUANCE_WAIT_FOR_RFI_RESPONSE':
      case 'PERMIT_VARIATION_WAIT_FOR_RFI_RESPONSE':
        return 'Request for information';
      case 'PERMIT_SURRENDER_WAIT_FOR_RFI_RESPONSE':
      case 'PERMIT_NOTIFICATION_WAIT_FOR_RFI_RESPONSE':
        return 'RFI cancellation';
      case 'PERMIT_ISSUANCE_RFI_RESPONSE_SUBMIT':
      case 'PERMIT_SURRENDER_RFI_RESPONSE_SUBMIT':
      case 'PERMIT_NOTIFICATION_RFI_RESPONSE_SUBMIT':
      case 'PERMIT_VARIATION_RFI_RESPONSE_SUBMIT':
        return 'RFI response';
      case 'PERMIT_NOTIFICATION_APPLICATION_SUBMIT':
      case 'PERMIT_NOTIFICATION_WAIT_FOR_REVIEW':
        return 'Notification Application';
      case 'PERMIT_NOTIFICATION_APPLICATION_REVIEW':
      case 'PERMIT_NOTIFICATION_WAIT_FOR_PEER_REVIEW':
        return 'Notification determination';
      case 'PERMIT_NOTIFICATION_APPLICATION_PEER_REVIEW':
        return 'Notification peer review';

      case 'PERMIT_NOTIFICATION_FOLLOW_UP':
        return 'Follow up response';
      case 'PERMIT_NOTIFICATION_WAIT_FOR_FOLLOW_UP':
        return 'Awaiting for operator follow up response';
      case 'PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_REVIEW':
        return 'Follow up response determination';
      case 'PERMIT_NOTIFICATION_FOLLOW_UP_WAIT_FOR_REVIEW':
        return 'Awaiting for Follow up response determination';
      case 'PERMIT_NOTIFICATION_FOLLOW_UP_WAIT_FOR_AMENDS':
        return 'Follow up response determination';
      case 'PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_AMENDS_SUBMIT':
        return 'Follow up response';

      case 'PERMIT_ISSUANCE_MAKE_PAYMENT':
        return 'Pay permit application fee';
      case 'PERMIT_ISSUANCE_TRACK_PAYMENT':
      case 'PERMIT_ISSUANCE_CONFIRM_PAYMENT':
        return 'Payment for permit application';

      case 'PERMIT_SURRENDER_MAKE_PAYMENT':
        return 'Pay surrender permit application fee';
      case 'PERMIT_SURRENDER_TRACK_PAYMENT':
      case 'PERMIT_SURRENDER_CONFIRM_PAYMENT':
        return 'Payment for surrender permit application';

      case 'PERMIT_REVOCATION_MAKE_PAYMENT':
        return 'Pay permit revocation fee';
      case 'PERMIT_REVOCATION_TRACK_PAYMENT':
      case 'PERMIT_REVOCATION_CONFIRM_PAYMENT':
        return 'Payment for permit revocation';

      case 'PERMIT_VARIATION_OPERATOR_APPLICATION_SUBMIT':
      case 'PERMIT_VARIATION_WAIT_FOR_REVIEW':
        return 'Permit variation';
      case 'PERMIT_VARIATION_APPLICATION_REVIEW':
        return 'Variation determination';

      case 'AER_APPLICATION_SUBMIT':
        return 'Application';

      default:
        return null;
    }
  }
}
