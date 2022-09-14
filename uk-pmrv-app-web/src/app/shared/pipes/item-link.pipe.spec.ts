import { ItemLinkPipe } from './item-link.pipe';

describe('ItemLinkPipe', () => {
  let pipe: ItemLinkPipe;

  beforeEach(() => (pipe = new ItemLinkPipe()));

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should map items to links', () => {
    expect(pipe.transform({ requestType: 'INSTALLATION_ACCOUNT_OPENING', taskId: 23 })).toEqual([
      '/installation-account',
      23,
    ]);
    expect(pipe.transform({ requestType: 'SYSTEM_MESSAGE_NOTIFICATION', taskId: 34 })).toEqual(['/message', 34]);
    expect(
      pipe.transform({
        requestType: 'PERMIT_ISSUANCE',
        taskType: 'PERMIT_ISSUANCE_WAIT_FOR_REVIEW',
        taskId: 45,
      }),
    ).toEqual(['/permit-application', 45, 'review']);
    expect(
      pipe.transform({
        requestType: 'PERMIT_ISSUANCE',
        taskType: 'PERMIT_ISSUANCE_APPLICATION_REVIEW',
        taskId: 45,
      }),
    ).toEqual(['/permit-application', 45, 'review']);
    expect(
      pipe.transform({
        requestType: 'PERMIT_ISSUANCE',
        taskType: 'PERMIT_ISSUANCE_WAIT_FOR_PEER_REVIEW',
        taskId: 45,
      }),
    ).toEqual(['/permit-application', 45, 'review']);
    expect(
      pipe.transform({
        requestType: 'PERMIT_ISSUANCE',
        taskType: 'PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW',
        taskId: 45,
      }),
    ).toEqual(['/permit-application', 45, 'review']);
    expect(
      pipe.transform({
        requestType: 'PERMIT_ISSUANCE',
        taskType: 'PERMIT_ISSUANCE_WAIT_FOR_AMENDS',
        taskId: 45,
      }),
    ).toEqual(['/permit-application', 45, 'review']);
    expect(pipe.transform({ requestType: 'PERMIT_ISSUANCE', taskId: 45 })).toEqual(['/permit-application', 45]);

    expect(
      pipe.transform({
        requestType: 'PERMIT_ISSUANCE',
        taskType: 'PERMIT_ISSUANCE_WAIT_FOR_RDE_RESPONSE',
        taskId: 45,
      }),
    ).toEqual(['/rde', 45, 'manual-approval']);

    expect(
      pipe.transform({
        requestType: 'PERMIT_ISSUANCE',
        taskType: 'PERMIT_ISSUANCE_MAKE_PAYMENT',
        taskId: 45,
      }),
    ).toEqual(['/payment', 45, 'make', 'details']);

    expect(
      pipe.transform({
        requestType: 'PERMIT_ISSUANCE',
        taskType: 'PERMIT_ISSUANCE_TRACK_PAYMENT',
        taskId: 45,
      }),
    ).toEqual(['/payment', 45, 'track']);

    expect(
      pipe.transform({
        requestType: 'PERMIT_ISSUANCE',
        taskType: 'PERMIT_ISSUANCE_CONFIRM_PAYMENT',
        taskId: 45,
      }),
    ).toEqual(['/payment', 45, 'track']);

    expect(
      pipe.transform({
        requestType: 'PERMIT_SURRENDER',
        taskType: 'PERMIT_SURRENDER_APPLICATION_SUBMIT',
        taskId: 45,
      }),
    ).toEqual(['/permit-surrender', 45]);

    expect(
      pipe.transform({
        requestType: 'PERMIT_SURRENDER',
        taskType: 'PERMIT_SURRENDER_APPLICATION_REVIEW',
        taskId: 45,
      }),
    ).toEqual(['/permit-surrender', 45, 'review']);

    expect(
      pipe.transform({
        requestType: 'PERMIT_SURRENDER',
        taskType: 'PERMIT_SURRENDER_WAIT_FOR_REVIEW',
        taskId: 45,
      }),
    ).toEqual(['/permit-surrender', 45, 'review', 'wait']);

    expect(
      pipe.transform({
        requestType: 'PERMIT_SURRENDER',
        taskType: 'PERMIT_SURRENDER_CESSATION_SUBMIT',
        taskId: 45,
      }),
    ).toEqual(['/permit-surrender', 45, 'cessation']);

    expect(
      pipe.transform({
        requestType: 'PERMIT_SURRENDER',
        taskType: 'PERMIT_SURRENDER_RFI_RESPONSE_SUBMIT',
        taskId: 45,
      }),
    ).toEqual(['/rfi', 45, 'responses']);

    expect(
      pipe.transform({
        requestType: 'PERMIT_SURRENDER',
        taskType: 'PERMIT_SURRENDER_RFI_RESPONSE_SUBMIT',
        taskId: 45,
      }),
    ).toEqual(['/rfi', 45, 'responses']);

    expect(
      pipe.transform({
        requestType: 'PERMIT_SURRENDER',
        taskType: 'PERMIT_SURRENDER_RDE_RESPONSE_SUBMIT',
        taskId: 45,
      }),
    ).toEqual(['/rde', 45, 'responses']);

    expect(
      pipe.transform({
        requestType: 'PERMIT_SURRENDER',
        taskType: 'PERMIT_SURRENDER_WAIT_FOR_RDE_RESPONSE',
        taskId: 45,
      }),
    ).toEqual(['/rde', 45, 'manual-approval']);

    expect(
      pipe.transform({
        requestType: 'PERMIT_SURRENDER',
        taskType: 'PERMIT_SURRENDER_MAKE_PAYMENT',
        taskId: 45,
      }),
    ).toEqual(['/payment', 45, 'make', 'details']);

    expect(
      pipe.transform({
        requestType: 'PERMIT_SURRENDER',
        taskType: 'PERMIT_SURRENDER_TRACK_PAYMENT',
        taskId: 45,
      }),
    ).toEqual(['/payment', 45, 'track']);

    expect(
      pipe.transform({
        requestType: 'PERMIT_SURRENDER',
        taskType: 'PERMIT_SURRENDER_CONFIRM_PAYMENT',
        taskId: 45,
      }),
    ).toEqual(['/payment', 45, 'track']);

    expect(
      pipe.transform({
        requestType: 'PERMIT_NOTIFICATION',
        taskType: 'PERMIT_NOTIFICATION_APPLICATION_SUBMIT',
        taskId: 45,
      }),
    ).toEqual(['/tasks', 45, 'permit-notification', 'submit']);

    expect(
      pipe.transform({
        requestType: 'PERMIT_NOTIFICATION',
        taskType: 'PERMIT_NOTIFICATION_APPLICATION_REVIEW',
        taskId: 45,
      }),
    ).toEqual(['/tasks', 45, 'permit-notification', 'review']);

    expect(
      pipe.transform({
        requestType: 'PERMIT_NOTIFICATION',
        taskType: 'PERMIT_NOTIFICATION_WAIT_FOR_REVIEW',
        taskId: 45,
      }),
    ).toEqual(['/tasks', 45, 'permit-notification', 'review-wait']);

    expect(
      pipe.transform({
        requestType: 'PERMIT_NOTIFICATION',
        taskType: 'PERMIT_NOTIFICATION_WAIT_FOR_PEER_REVIEW',
        taskId: 45,
      }),
    ).toEqual(['/tasks', 45, 'permit-notification', 'peer-review-wait']);

    expect(
      pipe.transform({
        requestType: 'PERMIT_NOTIFICATION',
        taskType: 'PERMIT_NOTIFICATION_APPLICATION_PEER_REVIEW',
        taskId: 45,
      }),
    ).toEqual(['/tasks', 45, 'permit-notification', 'peer-review']);

    expect(
      pipe.transform({
        requestType: 'PERMIT_NOTIFICATION',
        taskType: 'PERMIT_NOTIFICATION_FOLLOW_UP',
        taskId: 45,
      }),
    ).toEqual(['/tasks', 45, 'permit-notification', 'follow-up']);

    expect(
      pipe.transform({
        requestType: 'PERMIT_NOTIFICATION',
        taskType: 'PERMIT_NOTIFICATION_WAIT_FOR_FOLLOW_UP',
        taskId: 45,
      }),
    ).toEqual(['/tasks', 45, 'permit-notification', 'follow-up', 'wait']);

    expect(
      pipe.transform({
        requestType: 'PERMIT_NOTIFICATION',
        taskType: 'PERMIT_NOTIFICATION_RFI_RESPONSE_SUBMIT',
        taskId: 45,
      }),
    ).toEqual(['/rfi', 45, 'responses']);

    expect(
      pipe.transform({
        requestType: 'PERMIT_NOTIFICATION',
        taskType: 'PERMIT_NOTIFICATION_WAIT_FOR_RFI_RESPONSE',
        taskId: 45,
      }),
    ).toEqual(['/rfi', 45, 'wait']);

    expect(
      pipe.transform({
        requestType: 'PERMIT_NOTIFICATION',
        taskType: 'PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_REVIEW',
        taskId: 45,
      }),
    ).toEqual(['/tasks', 45, 'permit-notification', 'follow-up', 'review']);

    expect(
      pipe.transform({
        requestType: 'PERMIT_NOTIFICATION',
        taskType: 'PERMIT_NOTIFICATION_FOLLOW_UP_WAIT_FOR_REVIEW',
        taskId: 45,
      }),
    ).toEqual(['/tasks', 45, 'permit-notification', 'follow-up', 'review-wait']);

    expect(
      pipe.transform({
        requestType: 'PERMIT_NOTIFICATION',
        taskType: 'PERMIT_NOTIFICATION_FOLLOW_UP_WAIT_FOR_AMENDS',
        taskId: 45,
      }),
    ).toEqual(['/tasks', 45, 'permit-notification', 'follow-up', 'wait']);

    expect(
      pipe.transform({
        requestType: 'PERMIT_REVOCATION',
        taskType: 'PERMIT_REVOCATION_MAKE_PAYMENT',
        taskId: 45,
      }),
    ).toEqual(['/payment', 45, 'make', 'details']);

    expect(
      pipe.transform({
        requestType: 'PERMIT_REVOCATION',
        taskType: 'PERMIT_REVOCATION_TRACK_PAYMENT',
        taskId: 45,
      }),
    ).toEqual(['/payment', 45, 'track']);

    expect(
      pipe.transform({
        requestType: 'PERMIT_REVOCATION',
        taskType: 'PERMIT_REVOCATION_CONFIRM_PAYMENT',
        taskId: 45,
      }),
    ).toEqual(['/payment', 45, 'track']);

    expect(
      pipe.transform({
        requestType: 'PERMIT_VARIATION',
        taskType: 'PERMIT_VARIATION_OPERATOR_APPLICATION_SUBMIT',
        taskId: 45,
      }),
    ).toEqual(['/tasks', 45, 'permit-variation']);

    expect(
      pipe.transform({
        requestType: 'PERMIT_VARIATION',
        taskType: 'PERMIT_VARIATION_APPLICATION_REVIEW',
        taskId: 45,
      }),
    ).toEqual(['/permit-application', 45, 'review']);

    expect(
      pipe.transform({
        requestType: 'PERMIT_VARIATION',
        taskType: 'PERMIT_VARIATION_WAIT_FOR_REVIEW',
        taskId: 45,
      }),
    ).toEqual(['/permit-application', 45, 'review']);

    expect(pipe.transform(null)).toEqual(['.']);
  });
});
