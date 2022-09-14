import { Pipe, PipeTransform } from '@angular/core';

import { RequestActionInfoDTO } from 'pmrv-api';

import { ItemActionTypePipe } from './item-action-type.pipe';

@Pipe({ name: 'itemActionHeader' })
export class ItemActionHeaderPipe implements PipeTransform {
  transform(item: RequestActionInfoDTO): string {
    const itemActionType = new ItemActionTypePipe();

    switch (item.type) {
      case 'PERMIT_ISSUANCE_APPLICATION_AMENDS_SUBMITTED':
      case 'PERMIT_ISSUANCE_APPLICATION_DEEMED_WITHDRAWN':
      case 'PERMIT_ISSUANCE_APPLICATION_GRANTED':
      case 'PERMIT_ISSUANCE_APPLICATION_PEER_REVIEWER_ACCEPTED':
      case 'PERMIT_ISSUANCE_APPLICATION_PEER_REVIEWER_REJECTED':
      case 'PERMIT_ISSUANCE_APPLICATION_REJECTED':
      case 'PERMIT_ISSUANCE_APPLICATION_RETURNED_FOR_AMENDS':
      case 'PERMIT_ISSUANCE_APPLICATION_SUBMITTED':
      case 'PERMIT_ISSUANCE_PEER_REVIEW_REQUESTED':
      case 'PERMIT_ISSUANCE_RECALLED_FROM_AMENDS':
        return `${itemActionType.transform(item.type)} by ${item.submitter}`;

      case 'PERMIT_SURRENDER_APPLICATION_CANCELLED':
      case 'PERMIT_SURRENDER_APPLICATION_PEER_REVIEWER_ACCEPTED':
      case 'PERMIT_SURRENDER_APPLICATION_PEER_REVIEWER_REJECTED':
      case 'PERMIT_SURRENDER_APPLICATION_SUBMITTED':
      case 'PERMIT_SURRENDER_PEER_REVIEW_REQUESTED':
      case 'PERMIT_SURRENDER_CESSATION_COMPLETED':
      case 'PERMIT_SURRENDER_APPLICATION_DEEMED_WITHDRAWN':
      case 'PERMIT_SURRENDER_APPLICATION_GRANTED':
      case 'PERMIT_SURRENDER_APPLICATION_REJECTED':
        return `${itemActionType.transform(item.type)} by ${item.submitter}`;

      case 'PERMIT_REVOCATION_APPLICATION_CANCELLED':
      case 'PERMIT_REVOCATION_PEER_REVIEW_REQUESTED':
      case 'PERMIT_REVOCATION_APPLICATION_PEER_REVIEWER_ACCEPTED':
      case 'PERMIT_REVOCATION_APPLICATION_PEER_REVIEWER_REJECTED':
      case 'PERMIT_REVOCATION_CESSATION_COMPLETED':
      case 'PERMIT_REVOCATION_APPLICATION_SUBMITTED':
      case 'PERMIT_REVOCATION_APPLICATION_WITHDRAWN':
        return `${itemActionType.transform(item.type)} by ${item.submitter}`;

      case 'PERMIT_NOTIFICATION_APPLICATION_SUBMITTED':
      case 'PERMIT_NOTIFICATION_FOLLOW_UP_RESPONSE_SUBMITTED':
      case 'PERMIT_NOTIFICATION_APPLICATION_GRANTED':
      case 'PERMIT_NOTIFICATION_APPLICATION_REJECTED':
      case 'PERMIT_NOTIFICATION_FOLLOW_UP_DATE_EXTENDED':
      case 'PERMIT_NOTIFICATION_PEER_REVIEW_REQUESTED':
      case 'PERMIT_NOTIFICATION_APPLICATION_PEER_REVIEWER_ACCEPTED':
      case 'PERMIT_NOTIFICATION_APPLICATION_PEER_REVIEWER_REJECTED':
      case 'PERMIT_NOTIFICATION_APPLICATION_COMPLETED':
      case 'PERMIT_NOTIFICATION_FOLLOW_UP_RETURNED_FOR_AMENDS':
      case 'PERMIT_NOTIFICATION_FOLLOW_UP_RECALLED_FROM_AMENDS':
      case 'PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_AMENDS_SUBMITTED':
        return `${itemActionType.transform(item.type)} by ${item.submitter}`;

      case 'PERMIT_VARIATION_APPLICATION_SUBMITTED':
        return `${itemActionType.transform(item.type)} by ${item.submitter}`;

      case 'PAYMENT_MARKED_AS_PAID':
        return `Payment marked as paid by ${item.submitter} (BACS)`;
      case 'PAYMENT_CANCELLED':
      case 'PAYMENT_MARKED_AS_RECEIVED':
        return `${itemActionType.transform(item.type)} by ${item.submitter}`;

      case 'RDE_ACCEPTED':
      case 'RDE_CANCELLED':
      case 'RDE_REJECTED':
      case 'RDE_FORCE_REJECTED':
      case 'RDE_FORCE_ACCEPTED':
      case 'RDE_SUBMITTED':
        return `${itemActionType.transform(item.type)} by ${item.submitter}`;

      case 'RFI_CANCELLED':
      case 'RFI_RESPONSE_SUBMITTED':
      case 'RFI_SUBMITTED':
        return `${itemActionType.transform(item.type)} by ${item.submitter}`;

      default:
        return itemActionType.transform(item.type);
    }
  }
}
