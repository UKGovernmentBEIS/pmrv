import { SiteEmissionsPercentagePipe } from './site-emissions-percentage.pipe';

describe('SiteEmissionsPercentagePipe', () => {
  let pipe: SiteEmissionsPercentagePipe;

  beforeEach(async () => {
    pipe = new SiteEmissionsPercentagePipe();
  });

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should transform value', () => {
    expect(
      pipe.transform(10, {
        approach: 'Total',
        marginal: 10,
        minimis: 0,
        minor: 0,
        major: 0,
      }),
    ).toEqual(1);

    expect(
      pipe.transform(10, {
        approach: 'Total',
        marginal: 10,
        minimis: 10,
        minor: 10,
        major: 10,
      }),
    ).toEqual(0.25);

    expect(
      pipe.transform(0, {
        approach: 'Total',
        marginal: 10,
        minimis: 0,
        minor: 0,
        major: 0,
      }),
    ).toEqual(0);

    expect(
      pipe.transform(0, {
        approach: 'Total',
        marginal: 0,
        minimis: 0,
        minor: 0,
        major: 0,
      }),
    ).toEqual(0);
  });
});
