import { Pipe, PipeTransform } from '@angular/core';

import { RequestActionInfoDTO } from 'pmrv-api';

@Pipe({ name: 'timelineItemLink' })
export class TimelineItemLinkPipe implements PipeTransform {
  transform(value: RequestActionInfoDTO): any[] {
    switch (value.type) {
      case 'INSTALLATION_ACCOUNT_OPENING_ACCEPTED':
      case 'INSTALLATION_ACCOUNT_OPENING_REJECTED':
        return ['/installation-account', 'submitted-decision', value.id];
      case 'INSTALLATION_ACCOUNT_OPENING_ACCOUNT_APPROVED':
      case 'INSTALLATION_ACCOUNT_OPENING_APPLICATION_SUBMITTED':
        return ['/installation-account', 'application', 'summary', value.id];

      case 'PERMIT_ISSUANCE_APPLICATION_AMENDS_SUBMITTED':
        return null;
      case 'PERMIT_ISSUANCE_APPLICATION_DEEMED_WITHDRAWN':
      case 'PERMIT_ISSUANCE_APPLICATION_GRANTED':
      case 'PERMIT_ISSUANCE_APPLICATION_REJECTED':
        return ['/permit-application', 'action', value.id, 'review', 'decision-summary'];
      case 'PERMIT_ISSUANCE_APPLICATION_PEER_REVIEWER_ACCEPTED':
      case 'PERMIT_ISSUANCE_APPLICATION_PEER_REVIEWER_REJECTED':
        return ['/permit-application', 'action', value.id, 'review', 'peer-reviewer-submitted'];
      case 'PERMIT_ISSUANCE_APPLICATION_RETURNED_FOR_AMENDS':
        return ['/permit-application', 'action', value.id, 'review', 'return-for-amends'];
      case 'PERMIT_ISSUANCE_PEER_REVIEW_REQUESTED':
      case 'PERMIT_ISSUANCE_RECALLED_FROM_AMENDS':
        return null;

      case 'PERMIT_SURRENDER_APPLICATION_CANCELLED':
        return null;
      case 'PERMIT_SURRENDER_APPLICATION_PEER_REVIEWER_ACCEPTED':
      case 'PERMIT_SURRENDER_APPLICATION_PEER_REVIEWER_REJECTED':
        return ['/permit-surrender', 'action', value.id, 'review', 'peer-reviewer-submitted'];
      case 'PERMIT_SURRENDER_APPLICATION_SUBMITTED':
        return ['/permit-surrender', 'action', value.id, 'surrender-submitted'];
      case 'PERMIT_SURRENDER_PEER_REVIEW_REQUESTED':
        return null;
      case 'PERMIT_SURRENDER_APPLICATION_REJECTED':
      case 'PERMIT_SURRENDER_APPLICATION_GRANTED':
      case 'PERMIT_SURRENDER_APPLICATION_DEEMED_WITHDRAWN':
        return ['/permit-surrender', 'action', value.id, 'determination-submitted'];
      case 'PERMIT_SURRENDER_CESSATION_COMPLETED':
        return ['/permit-surrender', 'action', value.id, 'cessation', 'completed'];

      case 'PERMIT_REVOCATION_APPLICATION_CANCELLED':
      case 'PERMIT_REVOCATION_PEER_REVIEW_REQUESTED':
        return null;
      case 'PERMIT_REVOCATION_APPLICATION_PEER_REVIEWER_ACCEPTED':
      case 'PERMIT_REVOCATION_APPLICATION_PEER_REVIEWER_REJECTED':
        return ['/permit-revocation', 'action', value.id, 'peer-reviewer-submitted'];
      case 'PERMIT_REVOCATION_APPLICATION_SUBMITTED':
        return ['/permit-revocation', 'action', value.id, 'revocation-submitted'];
      case 'PERMIT_REVOCATION_APPLICATION_WITHDRAWN':
        return ['/permit-revocation', 'action', value.id, 'withdraw-completed'];
      case 'PERMIT_REVOCATION_CESSATION_COMPLETED':
        return ['/permit-revocation', 'action', value.id, 'cessation', 'completed'];

      case 'PERMIT_NOTIFICATION_APPLICATION_CANCELLED':
        return null;
      case 'PERMIT_NOTIFICATION_APPLICATION_SUBMITTED':
        return ['/actions', value.id, 'permit-notification', 'submitted'];
      case 'PERMIT_NOTIFICATION_APPLICATION_GRANTED':
      case 'PERMIT_NOTIFICATION_APPLICATION_REJECTED':
        return ['/actions', value.id, 'permit-notification', 'decision'];
      case 'PERMIT_NOTIFICATION_PEER_REVIEW_REQUESTED':
        return null;
      case 'PERMIT_NOTIFICATION_APPLICATION_PEER_REVIEWER_ACCEPTED':
      case 'PERMIT_NOTIFICATION_APPLICATION_PEER_REVIEWER_REJECTED':
        return ['/actions', value.id, 'permit-notification', 'peer-review-decision'];
      case 'PERMIT_NOTIFICATION_FOLLOW_UP_RESPONSE_SUBMITTED':
        return ['/actions', value.id, 'permit-notification', 'follow-up-response'];
      case 'PERMIT_NOTIFICATION_FOLLOW_UP_RETURNED_FOR_AMENDS':
        return ['/actions', value.id, 'permit-notification', 'follow-up-return-for-amends'];
      case 'PERMIT_NOTIFICATION_FOLLOW_UP_DATE_EXTENDED':
      case 'PERMIT_NOTIFICATION_FOLLOW_UP_RECALLED_FROM_AMENDS':
      case 'PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_AMENDS_SUBMITTED':
        return null;
      case 'PERMIT_NOTIFICATION_APPLICATION_COMPLETED':
        return ['/actions', value.id, 'permit-notification', 'completed'];

      case 'PERMIT_VARIATION_APPLICATION_SUBMITTED':
        return ['/permit-application', 'action', value.id];
      case 'PERMIT_VARIATION_APPLICATION_CANCELLED':
        return null;

      case 'PAYMENT_MARKED_AS_PAID':
        return ['/payment', 'action', value.id, 'paid'];
      case 'PAYMENT_CANCELLED':
        return ['/payment', 'action', value.id, 'cancelled'];
      case 'PAYMENT_MARKED_AS_RECEIVED':
        return ['/payment', 'action', value.id, 'received'];
      case 'PAYMENT_COMPLETED':
        return ['/payment', 'action', value.id, 'completed'];

      case 'RDE_ACCEPTED':
      case 'RDE_CANCELLED':
      case 'RDE_EXPIRED':
        return null;
      case 'RDE_FORCE_ACCEPTED':
      case 'RDE_FORCE_REJECTED':
        return ['/rde', 'action', value.id, 'rde-manual-approval-submitted'];
      case 'RDE_REJECTED':
        return ['/rde', 'action', value.id, 'rde-response-submitted'];
      case 'RDE_SUBMITTED':
        return ['/rde', 'action', value.id, 'rde-submitted'];

      case 'RFI_CANCELLED':
      case 'RFI_EXPIRED':
        return null;
      case 'RFI_RESPONSE_SUBMITTED':
        return ['/rfi', 'action', value.id, 'rfi-response-submitted'];
      case 'RFI_SUBMITTED':
        return ['/rfi', 'action', value.id, 'rfi-submitted'];
      case 'REQUEST_TERMINATED':
        return null;

      default:
        return ['/permit-application', 'action', value.id];
    }
  }
}
