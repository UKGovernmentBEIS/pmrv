import { AccountStatusPipe } from './account-status.pipe';

describe('AccountStatusPipe', () => {
  const pipe = new AccountStatusPipe();

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should properly transform account statuses', () => {
    let transformation = pipe.transform(null);
    expect(transformation).toEqual(null);

    transformation = pipe.transform(undefined);
    expect(transformation).toEqual(null);

    transformation = pipe.transform('AWAITING_RATIONALISATION');
    expect(transformation).toEqual('Awaiting Rationalisation');

    transformation = pipe.transform('AWAITING_REVOCATION');
    expect(transformation).toEqual('Awaiting Revocation');

    transformation = pipe.transform('AWAITING_SURRENDER');
    expect(transformation).toEqual('Awaiting surrender');

    transformation = pipe.transform('AWAITING_TRANSFER');
    expect(transformation).toEqual('Awaiting transfer');

    transformation = pipe.transform('CEASED_OPERATIONS');
    expect(transformation).toEqual('Ceased operations');

    transformation = pipe.transform('COMMISSION_LIST');
    expect(transformation).toEqual('Commission list');

    transformation = pipe.transform('DEEMED_WITHDRAWN');
    expect(transformation).toEqual('Deemed Withdrawn');

    transformation = pipe.transform('DENIED');
    expect(transformation).toEqual('Denied');

    transformation = pipe.transform('EXEMPT');
    expect(transformation).toEqual('Exempt');

    transformation = pipe.transform('EXEMPT_COMMERCIAL');
    expect(transformation).toEqual('Exempt commercial');

    transformation = pipe.transform('EXEMPT_NON_COMMERCIAL');
    expect(transformation).toEqual('Exempt non commercial');

    transformation = pipe.transform('LIVE');
    expect(transformation).toEqual('Live');

    transformation = pipe.transform('NEW');
    expect(transformation).toEqual('New');

    transformation = pipe.transform('PERMIT_REFUSED');
    expect(transformation).toEqual('Permit refused');

    transformation = pipe.transform('PRIOR_COMPLIANCE_LIST');
    expect(transformation).toEqual('Prior compliance list');

    transformation = pipe.transform('RATIONALISED');
    expect(transformation).toEqual('Rationalised');

    transformation = pipe.transform('REMOVED_FROM_COMMISSION_LIST');
    expect(transformation).toEqual('Removed from commission list');

    transformation = pipe.transform('REVOKED');
    expect(transformation).toEqual('Revoked');

    transformation = pipe.transform('SURRENDERED');
    expect(transformation).toEqual('Surrendered');

    transformation = pipe.transform('TRANSFERRED');
    expect(transformation).toEqual('Transferred');

    transformation = pipe.transform('UNAPPROVED');
    expect(transformation).toEqual('Unapproved');
  });
});
