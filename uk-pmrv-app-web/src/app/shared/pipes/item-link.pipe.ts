import { Pipe, PipeTransform } from '@angular/core';

import { ItemDTO } from 'pmrv-api';

@Pipe({ name: 'itemLink' })
export class ItemLinkPipe implements PipeTransform {
  transform(value: ItemDTO): any[] {
    switch (value?.requestType) {
      case 'INSTALLATION_ACCOUNT_OPENING':
        return ['/installation-account', value.taskId];
      case 'SYSTEM_MESSAGE_NOTIFICATION':
        return ['/message', value.taskId];
      case 'PERMIT_ISSUANCE':
        switch (value?.taskType) {
          case 'PERMIT_ISSUANCE_WAIT_FOR_REVIEW':
          case 'PERMIT_ISSUANCE_APPLICATION_REVIEW':
          case 'PERMIT_ISSUANCE_WAIT_FOR_PEER_REVIEW':
          case 'PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW':
          case 'PERMIT_ISSUANCE_WAIT_FOR_AMENDS':
            return ['/permit-application', value.taskId, 'review'];
          case 'PERMIT_ISSUANCE_RFI_RESPONSE_SUBMIT':
            return ['/rfi', value.taskId, 'responses'];
          case 'PERMIT_ISSUANCE_WAIT_FOR_RFI_RESPONSE':
            return ['/rfi', value.taskId, 'wait'];
          case 'PERMIT_ISSUANCE_RDE_RESPONSE_SUBMIT':
            return ['/rde', value.taskId, 'responses'];
          case 'PERMIT_ISSUANCE_WAIT_FOR_RDE_RESPONSE':
            return ['/rde', value.taskId, 'manual-approval'];
          case 'PERMIT_ISSUANCE_MAKE_PAYMENT':
            return ['/payment', value.taskId, 'make', 'details'];
          case 'PERMIT_ISSUANCE_TRACK_PAYMENT':
          case 'PERMIT_ISSUANCE_CONFIRM_PAYMENT':
            return ['/payment', value.taskId, 'track'];
          default:
            return ['/permit-application', value.taskId];
        }
      case 'PERMIT_REVOCATION':
        switch (value?.taskType) {
          case 'PERMIT_REVOCATION_APPLICATION_SUBMIT':
          case 'PERMIT_REVOCATION_WAIT_FOR_PEER_REVIEW':
          case 'PERMIT_REVOCATION_APPLICATION_PEER_REVIEW':
            return ['/permit-revocation', value.taskId];
          case 'PERMIT_REVOCATION_WAIT_FOR_APPEAL':
            return ['/permit-revocation', value.taskId, 'wait-for-appeal'];
          case 'PERMIT_REVOCATION_CESSATION_SUBMIT':
            return ['/permit-revocation', value.taskId, 'cessation'];
          case 'PERMIT_REVOCATION_MAKE_PAYMENT':
            return ['/payment', value.taskId, 'make', 'details'];
          case 'PERMIT_REVOCATION_TRACK_PAYMENT':
          case 'PERMIT_REVOCATION_CONFIRM_PAYMENT':
            return ['/payment', value.taskId, 'track'];
          default:
            return ['.'];
        }
      case 'PERMIT_SURRENDER':
        switch (value?.taskType) {
          case 'PERMIT_SURRENDER_WAIT_FOR_PEER_REVIEW':
          case 'PERMIT_SURRENDER_APPLICATION_REVIEW':
          case 'PERMIT_SURRENDER_APPLICATION_PEER_REVIEW':
            return ['/permit-surrender', value.taskId, 'review'];
          case 'PERMIT_SURRENDER_WAIT_FOR_REVIEW':
            return ['/permit-surrender', value.taskId, 'review', 'wait'];
          case 'PERMIT_SURRENDER_RFI_RESPONSE_SUBMIT':
            return ['/rfi', value.taskId, 'responses'];
          case 'PERMIT_SURRENDER_WAIT_FOR_RFI_RESPONSE':
            return ['/rfi', value.taskId, 'wait'];
          case 'PERMIT_SURRENDER_RDE_RESPONSE_SUBMIT':
            return ['/rde', value.taskId, 'responses'];
          case 'PERMIT_SURRENDER_WAIT_FOR_RDE_RESPONSE':
            return ['/rde', value.taskId, 'manual-approval'];
          case 'PERMIT_SURRENDER_CESSATION_SUBMIT':
            return ['/permit-surrender', value.taskId, 'cessation'];
          case 'PERMIT_SURRENDER_MAKE_PAYMENT':
            return ['/payment', value.taskId, 'make', 'details'];
          case 'PERMIT_SURRENDER_TRACK_PAYMENT':
          case 'PERMIT_SURRENDER_CONFIRM_PAYMENT':
            return ['/payment', value.taskId, 'track'];
          default:
            return ['/permit-surrender', value.taskId];
        }
      case 'PERMIT_NOTIFICATION': {
        switch (value?.taskType) {
          case 'PERMIT_NOTIFICATION_APPLICATION_SUBMIT':
            return ['/tasks', value.taskId, 'permit-notification', 'submit'];
          case 'PERMIT_NOTIFICATION_APPLICATION_REVIEW':
            return ['/tasks', value.taskId, 'permit-notification', 'review'];
          case 'PERMIT_NOTIFICATION_FOLLOW_UP':
          case 'PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_AMENDS_SUBMIT':
            return ['/tasks', value.taskId, 'permit-notification', 'follow-up'];
          case 'PERMIT_NOTIFICATION_WAIT_FOR_FOLLOW_UP':
          case 'PERMIT_NOTIFICATION_FOLLOW_UP_WAIT_FOR_AMENDS':
            return ['/tasks', value.taskId, 'permit-notification', 'follow-up', 'wait'];
          case 'PERMIT_NOTIFICATION_WAIT_FOR_PEER_REVIEW':
            return ['/tasks', value.taskId, 'permit-notification', 'peer-review-wait'];
          case 'PERMIT_NOTIFICATION_APPLICATION_PEER_REVIEW':
            return ['/tasks', value.taskId, 'permit-notification', 'peer-review'];
          case 'PERMIT_NOTIFICATION_WAIT_FOR_REVIEW':
            return ['/tasks', value.taskId, 'permit-notification', 'review-wait'];
          case 'PERMIT_NOTIFICATION_RFI_RESPONSE_SUBMIT':
            return ['/rfi', value.taskId, 'responses'];
          case 'PERMIT_NOTIFICATION_WAIT_FOR_RFI_RESPONSE':
            return ['/rfi', value.taskId, 'wait'];
          case 'PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_REVIEW':
            return ['/tasks', value.taskId, 'permit-notification', 'follow-up', 'review'];
          case 'PERMIT_NOTIFICATION_FOLLOW_UP_WAIT_FOR_REVIEW':
            return ['/tasks', value.taskId, 'permit-notification', 'follow-up', 'review-wait'];
          default:
            return ['.'];
        }
      }
      case 'PERMIT_VARIATION': {
        switch (value?.taskType) {
          case 'PERMIT_VARIATION_OPERATOR_APPLICATION_SUBMIT':
            return ['/tasks', value.taskId, 'permit-variation'];
          case 'PERMIT_VARIATION_APPLICATION_REVIEW':
          case 'PERMIT_VARIATION_WAIT_FOR_REVIEW':
            return ['/permit-application', value.taskId, 'review'];
          case 'PERMIT_VARIATION_RFI_RESPONSE_SUBMIT':
            return ['/rfi', value.taskId, 'responses'];
          case 'PERMIT_VARIATION_WAIT_FOR_RFI_RESPONSE':
            return ['/rfi', value.taskId, 'wait'];
          case 'PERMIT_VARIATION_RDE_RESPONSE_SUBMIT':
            return ['/rde', value.taskId, 'responses'];
          case 'PERMIT_VARIATION_WAIT_FOR_RDE_RESPONSE':
            return ['/rde', value.taskId, 'manual-approval'];
          default:
            return ['.'];
        }
      }
      case 'AER': {
        switch (value?.taskType) {
          case 'AER_APPLICATION_SUBMIT':
            return ['/tasks', value.taskId, 'aer', 'submit'];
          default:
            return ['.'];
        }
      }
      default:
        return ['.'];
    }
  }
}
