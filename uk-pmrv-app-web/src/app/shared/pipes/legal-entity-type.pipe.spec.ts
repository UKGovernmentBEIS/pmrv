import { LegalEntityTypePipe } from '@shared/pipes/legal-entity-type.pipe';

describe('LegalEntityTypePipe', () => {
  let pipe: LegalEntityTypePipe;

  beforeEach(() => (pipe = new LegalEntityTypePipe()));

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should map types to names', () => {
    expect(pipe.transform('LIMITED_COMPANY')).toEqual('Limited Company');
    expect(pipe.transform('OTHER')).toEqual('Other');
    expect(pipe.transform('PARTNERSHIP')).toEqual('Partnership');
    expect(pipe.transform('SOLE_TRADER')).toEqual('Sole trader');

    expect(pipe.transform(null)).toBeNull();
  });
});
