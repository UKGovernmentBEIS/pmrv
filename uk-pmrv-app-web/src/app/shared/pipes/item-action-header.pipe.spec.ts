import { RequestActionDTO } from 'pmrv-api';

import { ItemActionHeaderPipe } from './item-action-header.pipe';

describe('ItemActionHeaderPipe', () => {
  let pipe: ItemActionHeaderPipe;

  const baseRquestAction: Omit<RequestActionDTO, 'type'> = {
    id: 1,
    payload: {},
    submitter: 'John Bolt',
    creationDate: '2021-03-29T12:26:36.000Z',
  };

  beforeEach(() => (pipe = new ItemActionHeaderPipe()));

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should return the installation accounts', () => {
    expect(
      pipe.transform({
        ...baseRquestAction,
        type: 'INSTALLATION_ACCOUNT_OPENING_ACCEPTED',
      }),
    ).toEqual('The regulator accepted the installation account application');

    expect(
      pipe.transform({
        ...baseRquestAction,
        type: 'INSTALLATION_ACCOUNT_OPENING_ACCOUNT_APPROVED',
      }),
    ).toEqual('Installation application approved');

    expect(
      pipe.transform({
        ...baseRquestAction,
        type: 'INSTALLATION_ACCOUNT_OPENING_REJECTED',
      }),
    ).toEqual('The regulator rejected the installation account application');

    expect(
      pipe.transform({
        ...baseRquestAction,
        type: 'INSTALLATION_ACCOUNT_OPENING_APPLICATION_SUBMITTED',
      }),
    ).toEqual('Original application');
  });

  it('should display the approved application title', () => {
    expect(pipe.transform({})).toEqual('Approved Application');
  });

  it('should return the permit issuance application returned for amends title', () => {
    expect(
      pipe.transform({
        ...baseRquestAction,
        type: 'PERMIT_ISSUANCE_APPLICATION_RETURNED_FOR_AMENDS',
      }),
    ).toEqual('Permit application returned for amends by John Bolt');
  });

  it('should return the permit surrender applications', () => {
    expect(
      pipe.transform({
        ...baseRquestAction,
        type: 'PERMIT_SURRENDER_APPLICATION_CANCELLED',
      }),
    ).toEqual('Surrender request cancelled by John Bolt');

    expect(
      pipe.transform({
        ...baseRquestAction,
        type: 'PERMIT_SURRENDER_APPLICATION_PEER_REVIEWER_ACCEPTED',
      }),
    ).toEqual('Peer review agreement submitted by John Bolt');

    expect(
      pipe.transform({
        ...baseRquestAction,
        type: 'PERMIT_SURRENDER_APPLICATION_PEER_REVIEWER_REJECTED',
      }),
    ).toEqual('Peer review disagreement submitted by John Bolt');

    expect(
      pipe.transform({
        ...baseRquestAction,
        type: 'PERMIT_SURRENDER_APPLICATION_SUBMITTED',
      }),
    ).toEqual('Surrender request submitted by John Bolt');

    expect(
      pipe.transform({
        ...baseRquestAction,
        type: 'PERMIT_SURRENDER_PEER_REVIEW_REQUESTED',
      }),
    ).toEqual('Peer review requested by John Bolt');

    expect(
      pipe.transform({
        ...baseRquestAction,
        type: 'PERMIT_SURRENDER_CESSATION_COMPLETED',
      }),
    ).toEqual('Cessation completed by John Bolt');

    expect(
      pipe.transform({
        ...baseRquestAction,
        type: 'PERMIT_SURRENDER_APPLICATION_DEEMED_WITHDRAWN',
      }),
    ).toEqual('Surrender request deemed withdrawn by John Bolt');

    expect(
      pipe.transform({
        ...baseRquestAction,
        type: 'PERMIT_SURRENDER_APPLICATION_GRANTED',
      }),
    ).toEqual('Surrender request approved by John Bolt');

    expect(
      pipe.transform({
        ...baseRquestAction,
        type: 'PERMIT_SURRENDER_APPLICATION_REJECTED',
      }),
    ).toEqual('Surrender request rejected by John Bolt');
  });

  it('should return the permit notifications', () => {
    expect(
      pipe.transform({
        ...baseRquestAction,
        type: 'PERMIT_NOTIFICATION_APPLICATION_SUBMITTED',
      }),
    ).toEqual('Notification submitted by John Bolt');

    expect(
      pipe.transform({
        ...baseRquestAction,
        type: 'PERMIT_NOTIFICATION_FOLLOW_UP_DATE_EXTENDED',
      }),
    ).toEqual('Follow up response due date updated by John Bolt');

    expect(
      pipe.transform({
        ...baseRquestAction,
        type: 'PERMIT_NOTIFICATION_APPLICATION_CANCELLED',
      }),
    ).toEqual('Notification cancelled');

    expect(
      pipe.transform({
        ...baseRquestAction,
        type: 'PERMIT_NOTIFICATION_FOLLOW_UP_RESPONSE_SUBMITTED',
      }),
    ).toEqual('Follow up response submitted by John Bolt');

    expect(
      pipe.transform({
        ...baseRquestAction,
        type: 'PERMIT_NOTIFICATION_APPLICATION_COMPLETED',
      }),
    ).toEqual('Notification completed by John Bolt');

    expect(
      pipe.transform({
        ...baseRquestAction,
        type: 'PERMIT_NOTIFICATION_FOLLOW_UP_RETURNED_FOR_AMENDS',
      }),
    ).toEqual('Follow up response returned for amends by John Bolt');

    expect(
      pipe.transform({
        ...baseRquestAction,
        type: 'PERMIT_NOTIFICATION_FOLLOW_UP_RECALLED_FROM_AMENDS',
      }),
    ).toEqual('Follow up response recalled by John Bolt');

    expect(
      pipe.transform({
        ...baseRquestAction,
        type: 'PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_AMENDS_SUBMITTED',
      }),
    ).toEqual('Amended follow up response submitted by John Bolt');
  });

  it('should return the permit variation actions', () => {
    expect(
      pipe.transform({
        ...baseRquestAction,
        type: 'PERMIT_VARIATION_APPLICATION_SUBMITTED',
      }),
    ).toEqual('Permit variation submitted by John Bolt');
  });

  it('should return the payments', () => {
    expect(
      pipe.transform({
        ...baseRquestAction,
        type: 'PAYMENT_MARKED_AS_PAID',
      }),
    ).toEqual('Payment marked as paid by John Bolt (BACS)');

    expect(
      pipe.transform({
        ...baseRquestAction,
        type: 'PAYMENT_CANCELLED',
      }),
    ).toEqual('Payment task cancelled by John Bolt');

    expect(
      pipe.transform({
        ...baseRquestAction,
        type: 'PAYMENT_MARKED_AS_RECEIVED',
      }),
    ).toEqual('Payment marked as received by John Bolt');

    expect(
      pipe.transform({
        ...baseRquestAction,
        type: 'PAYMENT_COMPLETED',
      }),
    ).toEqual('Payment confirmed via GOV.UK pay');
  });
});
