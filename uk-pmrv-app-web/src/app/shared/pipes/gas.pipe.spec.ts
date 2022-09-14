import { GasPipe } from './gas.pipe';

describe('GasPipe', () => {
  const pipe = new GasPipe();

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should transform greenhouse gases', () => {
    expect(pipe.transform('AMMONIA_PRODUCTION')).toEqual('Carbon dioxide');
    expect(pipe.transform('PAPER_OR_CARDBOARD_PRODUCTION')).toEqual('Carbon dioxide');
    expect(pipe.transform('ADIPIC_ACID_PRODUCTION')).toEqual('Carbon dioxide and nitrous oxide');
    expect(pipe.transform('SECONDARY_ALUMINIUM_PRODUCTION')).toEqual('Carbon dioxide and perfluorocarbons');
  });

  it('should handle empty value', () => {
    const transformation = pipe.transform(null);

    expect(transformation).toEqual('');
  });

  it('should handle undefined value', () => {
    const transformation = pipe.transform(undefined);

    expect(transformation).toEqual('');
  });
});
