import { RequestActionInfoDTO } from 'pmrv-api';

import { TimelineItemLinkPipe } from './timeline-item-link.pipe';

describe('TimelineItemLinkPipe', () => {
  let pipe: TimelineItemLinkPipe;

  const requestAction: RequestActionInfoDTO = {
    id: 1,
    submitter: 'John Bolt',
    creationDate: '2021-03-29T12:26:36.000Z',
  };

  beforeEach(() => (pipe = new TimelineItemLinkPipe()));

  it('should create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should return link with relative path reference to previous directory', () => {
    requestAction.type = 'PERMIT_ISSUANCE_APPLICATION_SUBMITTED';
    expect(pipe.transform(requestAction)).toEqual(['/permit-application', 'action', requestAction.id]);
  });

  it('should return link with relative path reference two directories up', () => {
    requestAction.type = 'PERMIT_ISSUANCE_APPLICATION_SUBMITTED';
    expect(pipe.transform(requestAction)).toEqual(['/permit-application', 'action', requestAction.id]);
  });

  it('should return link for amends', () => {
    requestAction.type = 'PERMIT_ISSUANCE_APPLICATION_RETURNED_FOR_AMENDS';
    expect(pipe.transform(requestAction)).toEqual([
      '/permit-application',
      'action',
      requestAction.id,
      'review',
      'return-for-amends',
    ]);
  });

  it('should return link for peer review submission', () => {
    requestAction.type = 'PERMIT_ISSUANCE_APPLICATION_PEER_REVIEWER_ACCEPTED';
    expect(pipe.transform(requestAction)).toEqual([
      '/permit-application',
      'action',
      requestAction.id,
      'review',
      'peer-reviewer-submitted',
    ]);

    requestAction.type = 'PERMIT_ISSUANCE_APPLICATION_PEER_REVIEWER_REJECTED';
    expect(pipe.transform(requestAction)).toEqual([
      '/permit-application',
      'action',
      requestAction.id,
      'review',
      'peer-reviewer-submitted',
    ]);
  });

  it('should return empty link', () => {
    const noLinkActionTypes: RequestActionInfoDTO['type'][] = [
      'PERMIT_ISSUANCE_APPLICATION_AMENDS_SUBMITTED',
      'PERMIT_ISSUANCE_PEER_REVIEW_REQUESTED',
      'PERMIT_ISSUANCE_RECALLED_FROM_AMENDS',

      'PERMIT_SURRENDER_APPLICATION_CANCELLED',
      'PERMIT_SURRENDER_PEER_REVIEW_REQUESTED',

      'PERMIT_REVOCATION_APPLICATION_CANCELLED',

      'PERMIT_NOTIFICATION_APPLICATION_CANCELLED',
      'PERMIT_NOTIFICATION_FOLLOW_UP_DATE_EXTENDED',
      'PERMIT_NOTIFICATION_FOLLOW_UP_RECALLED_FROM_AMENDS',
      'PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_AMENDS_SUBMITTED',

      'RDE_ACCEPTED',
      'RDE_CANCELLED',
      'RDE_EXPIRED',

      'RFI_CANCELLED',
      'RFI_EXPIRED',

      'REQUEST_TERMINATED',
    ];

    noLinkActionTypes.forEach((type) => {
      requestAction.type = type;
      expect(pipe.transform(requestAction)).toBeNull();
    });
  });

  it('should return link for permit issuance decision', () => {
    requestAction.type = 'PERMIT_ISSUANCE_APPLICATION_GRANTED';
    expect(pipe.transform(requestAction)).toEqual([
      '/permit-application',
      'action',
      requestAction.id,
      'review',
      'decision-summary',
    ]);

    requestAction.type = 'PERMIT_ISSUANCE_APPLICATION_DEEMED_WITHDRAWN';
    expect(pipe.transform(requestAction)).toEqual([
      '/permit-application',
      'action',
      requestAction.id,
      'review',
      'decision-summary',
    ]);

    requestAction.type = 'PERMIT_ISSUANCE_APPLICATION_REJECTED';
    expect(pipe.transform(requestAction)).toEqual([
      '/permit-application',
      'action',
      requestAction.id,
      'review',
      'decision-summary',
    ]);
  });

  it('should return link for installation account', () => {
    requestAction.type = 'INSTALLATION_ACCOUNT_OPENING_ACCEPTED';
    expect(pipe.transform(requestAction)).toEqual(['/installation-account', 'submitted-decision', requestAction.id]);

    requestAction.type = 'INSTALLATION_ACCOUNT_OPENING_REJECTED';
    expect(pipe.transform(requestAction)).toEqual(['/installation-account', 'submitted-decision', requestAction.id]);

    requestAction.type = 'INSTALLATION_ACCOUNT_OPENING_ACCOUNT_APPROVED';
    expect(pipe.transform(requestAction)).toEqual([
      '/installation-account',
      'application',
      'summary',
      requestAction.id,
    ]);

    requestAction.type = 'INSTALLATION_ACCOUNT_OPENING_APPLICATION_SUBMITTED';
    expect(pipe.transform(requestAction)).toEqual([
      '/installation-account',
      'application',
      'summary',
      requestAction.id,
    ]);

    requestAction.type = 'PERMIT_SURRENDER_CESSATION_COMPLETED';
    expect(pipe.transform(requestAction)).toEqual([
      '/permit-surrender',
      'action',
      requestAction.id,
      'cessation',
      'completed',
    ]);
  });

  it('should return link for payment', () => {
    requestAction.type = 'PAYMENT_MARKED_AS_PAID';
    expect(pipe.transform(requestAction)).toEqual(['/payment', 'action', requestAction.id, 'paid']);

    requestAction.type = 'PAYMENT_CANCELLED';
    expect(pipe.transform(requestAction)).toEqual(['/payment', 'action', requestAction.id, 'cancelled']);

    requestAction.type = 'PAYMENT_MARKED_AS_RECEIVED';
    expect(pipe.transform(requestAction)).toEqual(['/payment', 'action', requestAction.id, 'received']);

    requestAction.type = 'PAYMENT_COMPLETED';
    expect(pipe.transform(requestAction)).toEqual(['/payment', 'action', requestAction.id, 'completed']);
  });

  it('should return link for notification', () => {
    requestAction.type = 'PERMIT_NOTIFICATION_FOLLOW_UP_RETURNED_FOR_AMENDS';
    expect(pipe.transform(requestAction)).toEqual([
      '/actions',
      requestAction.id,
      'permit-notification',
      'follow-up-return-for-amends',
    ]);
  });

  it('should return link for variation submitted', () => {
    requestAction.type = 'PERMIT_VARIATION_APPLICATION_SUBMITTED';
    expect(pipe.transform(requestAction)).toEqual(['/permit-application', 'action', requestAction.id]);
  });
});
