import { ItemNamePipe } from './item-name.pipe';

describe('ItemNamePipe', () => {
  let pipe: ItemNamePipe;

  beforeEach(() => (pipe = new ItemNamePipe()));

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should map task types to item names', () => {
    expect(pipe.transform('ACCOUNT_USERS_SETUP')).toEqual('Set up');
    expect(pipe.transform('INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW')).toEqual('Application');
    expect(pipe.transform('INSTALLATION_ACCOUNT_OPENING_ARCHIVE')).toEqual('Application');
    expect(pipe.transform('PERMIT_ISSUANCE_APPLICATION_SUBMIT')).toEqual('Application');
    expect(pipe.transform('PERMIT_ISSUANCE_WAIT_FOR_REVIEW')).toEqual('Application');
    expect(pipe.transform('PERMIT_ISSUANCE_APPLICATION_REVIEW')).toEqual('Permit determination');
    expect(pipe.transform('PERMIT_ISSUANCE_WAIT_FOR_PEER_REVIEW')).toEqual('Permit determination');
    expect(pipe.transform('PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW')).toEqual('Permit peer review');
    expect(pipe.transform('NEW_VERIFICATION_BODY_INSTALLATION')).toEqual('New installation');
    expect(pipe.transform('VERIFIER_NO_LONGER_AVAILABLE')).toEqual('Verifier is no longer available');
    expect(pipe.transform('PERMIT_ISSUANCE_APPLICATION_AMENDS_SUBMIT')).toEqual('Permit application');
    expect(pipe.transform('PERMIT_ISSUANCE_WAIT_FOR_AMENDS')).toEqual('Permit determination');
    expect(pipe.transform('PERMIT_SURRENDER_APPLICATION_SUBMIT')).toEqual('Surrender application');
    expect(pipe.transform('PERMIT_SURRENDER_WAIT_FOR_REVIEW')).toEqual('Surrender application');
    expect(pipe.transform('PERMIT_SURRENDER_APPLICATION_REVIEW')).toEqual('Surrender determination');
    expect(pipe.transform('PERMIT_SURRENDER_CESSATION_SUBMIT')).toEqual('Surrender cessation');
    expect(pipe.transform('PERMIT_ISSUANCE_WAIT_FOR_RDE_RESPONSE')).toEqual('Extension request');
    expect(pipe.transform('PERMIT_ISSUANCE_RDE_RESPONSE_SUBMIT')).toEqual('Extension request');
    expect(pipe.transform('PERMIT_ISSUANCE_WAIT_FOR_RFI_RESPONSE')).toEqual('Request for information');
    expect(pipe.transform('PERMIT_ISSUANCE_RFI_RESPONSE_SUBMIT')).toEqual('RFI response');
    expect(pipe.transform('PERMIT_SURRENDER_WAIT_FOR_RFI_RESPONSE')).toEqual('RFI cancellation');
    expect(pipe.transform('PERMIT_SURRENDER_RFI_RESPONSE_SUBMIT')).toEqual('RFI response');
    expect(pipe.transform('PERMIT_SURRENDER_WAIT_FOR_RDE_RESPONSE')).toEqual('Extension request');
    expect(pipe.transform('PERMIT_SURRENDER_RDE_RESPONSE_SUBMIT')).toEqual('Extension request');
    expect(pipe.transform('PERMIT_NOTIFICATION_APPLICATION_SUBMIT')).toEqual('Notification Application');
    expect(pipe.transform('PERMIT_NOTIFICATION_WAIT_FOR_REVIEW')).toEqual('Notification Application');
    expect(pipe.transform('PERMIT_NOTIFICATION_APPLICATION_REVIEW')).toEqual('Notification determination');
    expect(pipe.transform('PERMIT_NOTIFICATION_WAIT_FOR_PEER_REVIEW')).toEqual('Notification determination');
    expect(pipe.transform('PERMIT_NOTIFICATION_APPLICATION_PEER_REVIEW')).toEqual('Notification peer review');
    expect(pipe.transform('PERMIT_NOTIFICATION_WAIT_FOR_RFI_RESPONSE')).toEqual('RFI cancellation');
    expect(pipe.transform('PERMIT_NOTIFICATION_RFI_RESPONSE_SUBMIT')).toEqual('RFI response');
    expect(pipe.transform('PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_REVIEW')).toEqual(
      'Follow up response determination',
    );
    expect(pipe.transform('PERMIT_NOTIFICATION_FOLLOW_UP_WAIT_FOR_REVIEW')).toEqual(
      'Awaiting for Follow up response determination',
    );
    expect(pipe.transform('PERMIT_NOTIFICATION_FOLLOW_UP_WAIT_FOR_AMENDS')).toEqual('Follow up response determination');
    expect(pipe.transform('PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_AMENDS_SUBMIT')).toEqual('Follow up response');

    expect(pipe.transform('PERMIT_ISSUANCE_MAKE_PAYMENT')).toEqual('Pay permit application fee');
    expect(pipe.transform('PERMIT_ISSUANCE_TRACK_PAYMENT')).toEqual('Payment for permit application');
    expect(pipe.transform('PERMIT_ISSUANCE_CONFIRM_PAYMENT')).toEqual('Payment for permit application');

    expect(pipe.transform('PERMIT_SURRENDER_MAKE_PAYMENT')).toEqual('Pay surrender permit application fee');
    expect(pipe.transform('PERMIT_SURRENDER_TRACK_PAYMENT')).toEqual('Payment for surrender permit application');
    expect(pipe.transform('PERMIT_SURRENDER_CONFIRM_PAYMENT')).toEqual('Payment for surrender permit application');

    expect(pipe.transform('PERMIT_REVOCATION_MAKE_PAYMENT')).toEqual('Pay permit revocation fee');
    expect(pipe.transform('PERMIT_REVOCATION_TRACK_PAYMENT')).toEqual('Payment for permit revocation');
    expect(pipe.transform('PERMIT_REVOCATION_CONFIRM_PAYMENT')).toEqual('Payment for permit revocation');

    expect(pipe.transform('PERMIT_VARIATION_OPERATOR_APPLICATION_SUBMIT')).toEqual('Permit variation');
    expect(pipe.transform('PERMIT_VARIATION_WAIT_FOR_REVIEW')).toEqual('Permit variation');
    expect(pipe.transform('PERMIT_VARIATION_APPLICATION_REVIEW')).toEqual('Variation determination');

    expect(pipe.transform('AER_APPLICATION_SUBMIT')).toEqual('Application');

    expect(pipe.transform(null)).toBeNull();
  });
});
