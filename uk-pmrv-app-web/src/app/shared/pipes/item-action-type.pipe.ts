import { Pipe, PipeTransform } from '@angular/core';

import { RequestActionInfoDTO } from 'pmrv-api';

@Pipe({ name: 'itemActionType' })
export class ItemActionTypePipe implements PipeTransform {
  transform(type: RequestActionInfoDTO['type']): string {
    switch (type) {
      case 'INSTALLATION_ACCOUNT_OPENING_ACCEPTED':
        return `The regulator accepted the installation account application`;
      case 'INSTALLATION_ACCOUNT_OPENING_ACCOUNT_APPROVED':
        return 'Installation application approved';
      case 'INSTALLATION_ACCOUNT_OPENING_REJECTED':
        return 'The regulator rejected the installation account application';
      case 'INSTALLATION_ACCOUNT_OPENING_APPLICATION_SUBMITTED':
        return 'Original application';

      case 'PERMIT_ISSUANCE_APPLICATION_AMENDS_SUBMITTED':
        return 'Amended permit application submitted';
      case 'PERMIT_ISSUANCE_APPLICATION_DEEMED_WITHDRAWN':
        return 'Permit application deemed withdrawn';
      case 'PERMIT_ISSUANCE_APPLICATION_GRANTED':
        return 'Permit application approved';
      case 'PERMIT_ISSUANCE_APPLICATION_PEER_REVIEWER_ACCEPTED':
        return 'Peer review agreement submitted';
      case 'PERMIT_ISSUANCE_APPLICATION_PEER_REVIEWER_REJECTED':
        return 'Peer review disagreement submitted';
      case 'PERMIT_ISSUANCE_APPLICATION_REJECTED':
        return 'Permit application rejected';
      case 'PERMIT_ISSUANCE_APPLICATION_RETURNED_FOR_AMENDS':
        return 'Permit application returned for amends';
      case 'PERMIT_ISSUANCE_APPLICATION_SUBMITTED':
        return 'New permit submitted';
      case 'PERMIT_ISSUANCE_PEER_REVIEW_REQUESTED':
        return 'Peer review requested';
      case 'PERMIT_ISSUANCE_RECALLED_FROM_AMENDS':
        return 'Permit application recalled';

      case 'PERMIT_SURRENDER_APPLICATION_CANCELLED':
        return 'Surrender request cancelled';
      case 'PERMIT_SURRENDER_APPLICATION_PEER_REVIEWER_ACCEPTED':
        return 'Peer review agreement submitted';
      case 'PERMIT_SURRENDER_APPLICATION_PEER_REVIEWER_REJECTED':
        return 'Peer review disagreement submitted';
      case 'PERMIT_SURRENDER_APPLICATION_SUBMITTED':
        return 'Surrender request submitted';
      case 'PERMIT_SURRENDER_PEER_REVIEW_REQUESTED':
        return 'Peer review requested';
      case 'PERMIT_SURRENDER_CESSATION_COMPLETED':
        return 'Cessation completed';
      case 'PERMIT_SURRENDER_APPLICATION_DEEMED_WITHDRAWN':
        return 'Surrender request deemed withdrawn';
      case 'PERMIT_SURRENDER_APPLICATION_GRANTED':
        return 'Surrender request approved';
      case 'PERMIT_SURRENDER_APPLICATION_REJECTED':
        return 'Surrender request rejected';

      case 'PERMIT_REVOCATION_APPLICATION_CANCELLED':
        return 'Revocation request cancelled';
      case 'PERMIT_REVOCATION_PEER_REVIEW_REQUESTED':
        return 'Peer review requested';
      case 'PERMIT_REVOCATION_APPLICATION_PEER_REVIEWER_ACCEPTED':
        return 'Peer review agreement submitted';
      case 'PERMIT_REVOCATION_APPLICATION_PEER_REVIEWER_REJECTED':
        return 'Peer review disagreement submitted';
      case 'PERMIT_REVOCATION_CESSATION_COMPLETED':
        return 'Cessation completed';
      case 'PERMIT_REVOCATION_APPLICATION_SUBMITTED':
        return 'Permit revocation submitted';
      case 'PERMIT_REVOCATION_APPLICATION_WITHDRAWN':
        return 'Revocation request withdrawn';

      case 'PERMIT_NOTIFICATION_APPLICATION_SUBMITTED':
        return 'Notification submitted';
      case 'PERMIT_NOTIFICATION_APPLICATION_CANCELLED':
        return 'Notification cancelled';
      case 'PERMIT_NOTIFICATION_FOLLOW_UP_RESPONSE_SUBMITTED':
        return 'Follow up response submitted';
      case 'PERMIT_NOTIFICATION_APPLICATION_GRANTED':
        return 'Notification approved';
      case 'PERMIT_NOTIFICATION_APPLICATION_REJECTED':
        return 'Notification rejected';
      case 'PERMIT_NOTIFICATION_FOLLOW_UP_DATE_EXTENDED':
        return 'Follow up response due date updated';
      case 'PERMIT_NOTIFICATION_PEER_REVIEW_REQUESTED':
        return 'Peer review requested';
      case 'PERMIT_NOTIFICATION_APPLICATION_PEER_REVIEWER_ACCEPTED':
        return 'Peer review agreement submitted';
      case 'PERMIT_NOTIFICATION_APPLICATION_PEER_REVIEWER_REJECTED':
        return 'Peer review disagreement submitted';
      case 'PERMIT_NOTIFICATION_APPLICATION_COMPLETED':
        return 'Notification completed';
      case 'PERMIT_NOTIFICATION_FOLLOW_UP_RETURNED_FOR_AMENDS':
        return 'Follow up response returned for amends';
      case 'PERMIT_NOTIFICATION_FOLLOW_UP_RECALLED_FROM_AMENDS':
        return 'Follow up response recalled';
      case 'PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_AMENDS_SUBMITTED':
        return 'Amended follow up response submitted';

      case 'PERMIT_VARIATION_APPLICATION_SUBMITTED':
          return 'Permit variation submitted';
      case 'PERMIT_VARIATION_APPLICATION_CANCELLED':
        return 'Variation application cancelled';

      case 'PAYMENT_MARKED_AS_PAID':
        return 'Payment marked as paid (BACS)';
      case 'PAYMENT_CANCELLED':
        return 'Payment task cancelled';
      case 'PAYMENT_MARKED_AS_RECEIVED':
        return 'Payment marked as received';
      case 'PAYMENT_COMPLETED':
        return 'Payment confirmed via GOV.UK pay';

      case 'RDE_ACCEPTED':
        return 'Deadline extension date accepted';
      case 'RDE_CANCELLED':
      case 'RDE_REJECTED':
      case 'RDE_FORCE_REJECTED':
        return 'Deadline extension date rejected';
      case 'RDE_EXPIRED':
        return 'Deadline extension date expired';
      case 'RDE_FORCE_ACCEPTED':
        return 'Deadline extension date approved';
      case 'RDE_SUBMITTED':
        return 'Deadline extension date requested';

      case 'RFI_CANCELLED':
        return 'Request for information withdrawn';
      case 'RFI_EXPIRED':
        return 'Request for information expired';
      case 'RFI_RESPONSE_SUBMITTED':
        return 'Request for information responded';
      case 'RFI_SUBMITTED':
        return 'Request for information sent';
      case 'REQUEST_TERMINATED':
        return 'Workflow terminated by the system';
      default:
        return 'Approved Application';
    }
  }
}
