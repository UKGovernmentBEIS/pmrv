import { ReportingTypePipe } from './reporting-type.pipe';

describe('ReportingTypePipe', () => {
  let pipe: ReportingTypePipe;

  beforeEach(async () => {
    pipe = new ReportingTypePipe();
  });

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should transform value', () => {
    expect(pipe.transform('EXCEEDED_THRESHOLD_STATED_GHGE_PERMIT')).toEqual(
      'Exceeded a threshold stated in the GHGE Permit',
    );
    expect(pipe.transform('EXCEEDED_THRESHOLD_STATED_HSE_PERMIT')).toEqual(
      'Exceeded a threshold stated in the HSE Permit',
    );
    expect(pipe.transform('RENOUNCE_FREE_ALLOCATIONS')).toEqual('Renounce free allocations');
    expect(pipe.transform('OTHER_ISSUE')).toEqual('Some other issue');
    expect(pipe.transform(undefined)).toEqual('');
  });
});
