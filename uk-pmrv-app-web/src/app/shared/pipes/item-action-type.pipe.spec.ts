import { ItemActionTypePipe } from './item-action-type.pipe';

describe('ItemActionTypePipe', () => {
  let pipe: ItemActionTypePipe;

  beforeEach(() => (pipe = new ItemActionTypePipe()));

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('hould properly transform action types', () => {
    expect(pipe.transform('INSTALLATION_ACCOUNT_OPENING_ACCEPTED')).toEqual(
      'The regulator accepted the installation account application',
    );
    expect(pipe.transform('INSTALLATION_ACCOUNT_OPENING_ACCOUNT_APPROVED')).toEqual(
      'Installation application approved',
    );
    expect(pipe.transform('INSTALLATION_ACCOUNT_OPENING_REJECTED')).toEqual(
      'The regulator rejected the installation account application',
    );
    expect(pipe.transform('INSTALLATION_ACCOUNT_OPENING_APPLICATION_SUBMITTED')).toEqual('Original application');

    expect(pipe.transform('PERMIT_ISSUANCE_APPLICATION_AMENDS_SUBMITTED')).toEqual(
      'Amended permit application submitted',
    );
    expect(pipe.transform('PERMIT_ISSUANCE_APPLICATION_DEEMED_WITHDRAWN')).toEqual(
      'Permit application deemed withdrawn',
    );
    expect(pipe.transform('PERMIT_ISSUANCE_APPLICATION_GRANTED')).toEqual('Permit application approved');
    expect(pipe.transform('PERMIT_ISSUANCE_APPLICATION_PEER_REVIEWER_ACCEPTED')).toEqual(
      'Peer review agreement submitted',
    );
    expect(pipe.transform('PERMIT_ISSUANCE_APPLICATION_PEER_REVIEWER_REJECTED')).toEqual(
      'Peer review disagreement submitted',
    );
    expect(pipe.transform('PERMIT_ISSUANCE_APPLICATION_REJECTED')).toEqual('Permit application rejected');
    expect(pipe.transform('PERMIT_ISSUANCE_APPLICATION_RETURNED_FOR_AMENDS')).toEqual(
      'Permit application returned for amends',
    );
    expect(pipe.transform('PERMIT_ISSUANCE_APPLICATION_SUBMITTED')).toEqual('New permit submitted');
    expect(pipe.transform('PERMIT_ISSUANCE_PEER_REVIEW_REQUESTED')).toEqual('Peer review requested');
    expect(pipe.transform('PERMIT_ISSUANCE_RECALLED_FROM_AMENDS')).toEqual('Permit application recalled');

    expect(pipe.transform('PERMIT_SURRENDER_APPLICATION_CANCELLED')).toEqual('Surrender request cancelled');
    expect(pipe.transform('PERMIT_SURRENDER_APPLICATION_PEER_REVIEWER_ACCEPTED')).toEqual(
      'Peer review agreement submitted',
    );
    expect(pipe.transform('PERMIT_SURRENDER_APPLICATION_PEER_REVIEWER_REJECTED')).toEqual(
      'Peer review disagreement submitted',
    );
    expect(pipe.transform('PERMIT_SURRENDER_APPLICATION_SUBMITTED')).toEqual('Surrender request submitted');
    expect(pipe.transform('PERMIT_SURRENDER_PEER_REVIEW_REQUESTED')).toEqual('Peer review requested');
    expect(pipe.transform('PERMIT_SURRENDER_CESSATION_COMPLETED')).toEqual('Cessation completed');
    expect(pipe.transform('PERMIT_SURRENDER_APPLICATION_DEEMED_WITHDRAWN')).toEqual(
      'Surrender request deemed withdrawn',
    );
    expect(pipe.transform('PERMIT_SURRENDER_APPLICATION_GRANTED')).toEqual('Surrender request approved');
    expect(pipe.transform('PERMIT_SURRENDER_APPLICATION_REJECTED')).toEqual('Surrender request rejected');

    expect(pipe.transform('PERMIT_REVOCATION_APPLICATION_CANCELLED')).toEqual('Revocation request cancelled');
    expect(pipe.transform('PERMIT_REVOCATION_PEER_REVIEW_REQUESTED')).toEqual('Peer review requested');
    expect(pipe.transform('PERMIT_REVOCATION_APPLICATION_PEER_REVIEWER_ACCEPTED')).toEqual(
      'Peer review agreement submitted',
    );
    expect(pipe.transform('PERMIT_REVOCATION_APPLICATION_PEER_REVIEWER_REJECTED')).toEqual(
      'Peer review disagreement submitted',
    );
    expect(pipe.transform('PERMIT_REVOCATION_CESSATION_COMPLETED')).toEqual('Cessation completed');
    expect(pipe.transform('PERMIT_REVOCATION_APPLICATION_SUBMITTED')).toEqual('Permit revocation submitted');
    expect(pipe.transform('PERMIT_REVOCATION_APPLICATION_WITHDRAWN')).toEqual('Revocation request withdrawn');

    expect(pipe.transform('PERMIT_NOTIFICATION_APPLICATION_SUBMITTED')).toEqual('Notification submitted');
    expect(pipe.transform('PERMIT_NOTIFICATION_APPLICATION_CANCELLED')).toEqual('Notification cancelled');
    expect(pipe.transform('PERMIT_NOTIFICATION_FOLLOW_UP_RESPONSE_SUBMITTED')).toEqual('Follow up response submitted');
    expect(pipe.transform('PERMIT_NOTIFICATION_APPLICATION_GRANTED')).toEqual('Notification approved');
    expect(pipe.transform('PERMIT_NOTIFICATION_APPLICATION_REJECTED')).toEqual('Notification rejected');
    expect(pipe.transform('PERMIT_NOTIFICATION_FOLLOW_UP_DATE_EXTENDED')).toEqual(
      'Follow up response due date updated',
    );
    expect(pipe.transform('PERMIT_NOTIFICATION_PEER_REVIEW_REQUESTED')).toEqual('Peer review requested');
    expect(pipe.transform('PERMIT_NOTIFICATION_APPLICATION_PEER_REVIEWER_ACCEPTED')).toEqual(
      'Peer review agreement submitted',
    );
    expect(pipe.transform('PERMIT_NOTIFICATION_APPLICATION_PEER_REVIEWER_REJECTED')).toEqual(
      'Peer review disagreement submitted',
    );
    expect(pipe.transform('PERMIT_NOTIFICATION_APPLICATION_COMPLETED')).toEqual('Notification completed');
    expect(pipe.transform('PERMIT_NOTIFICATION_FOLLOW_UP_RETURNED_FOR_AMENDS')).toEqual(
      'Follow up response returned for amends',
    );
    expect(pipe.transform('PERMIT_NOTIFICATION_FOLLOW_UP_RECALLED_FROM_AMENDS')).toEqual('Follow up response recalled');
    expect(pipe.transform('PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_AMENDS_SUBMITTED')).toEqual(
      'Amended follow up response submitted',
    );

    expect(pipe.transform('PERMIT_VARIATION_APPLICATION_SUBMITTED')).toEqual('Permit variation submitted');
    expect(pipe.transform('PERMIT_VARIATION_APPLICATION_CANCELLED')).toEqual('Variation application cancelled');

    expect(pipe.transform('PAYMENT_MARKED_AS_PAID')).toEqual('Payment marked as paid (BACS)');
    expect(pipe.transform('PAYMENT_CANCELLED')).toEqual('Payment task cancelled');
    expect(pipe.transform('PAYMENT_MARKED_AS_RECEIVED')).toEqual('Payment marked as received');
    expect(pipe.transform('PAYMENT_COMPLETED')).toEqual('Payment confirmed via GOV.UK pay');

    expect(pipe.transform('RDE_ACCEPTED')).toEqual('Deadline extension date accepted');
    expect(pipe.transform('RDE_CANCELLED')).toEqual('Deadline extension date rejected');
    expect(pipe.transform('RDE_REJECTED')).toEqual('Deadline extension date rejected');
    expect(pipe.transform('RDE_FORCE_REJECTED')).toEqual('Deadline extension date rejected');
    expect(pipe.transform('RDE_EXPIRED')).toEqual('Deadline extension date expired');
    expect(pipe.transform('RDE_FORCE_ACCEPTED')).toEqual('Deadline extension date approved');
    expect(pipe.transform('RDE_SUBMITTED')).toEqual('Deadline extension date requested');

    expect(pipe.transform('RFI_CANCELLED')).toEqual('Request for information withdrawn');
    expect(pipe.transform('RFI_EXPIRED')).toEqual('Request for information expired');
    expect(pipe.transform('RFI_RESPONSE_SUBMITTED')).toEqual('Request for information responded');
    expect(pipe.transform('RFI_SUBMITTED')).toEqual('Request for information sent');
    expect(pipe.transform('REQUEST_TERMINATED')).toEqual('Workflow terminated by the system');

    expect(pipe.transform(undefined)).toEqual('Approved Application');
  });
});
