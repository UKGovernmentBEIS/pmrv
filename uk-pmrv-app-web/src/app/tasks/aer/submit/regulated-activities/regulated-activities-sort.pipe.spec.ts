import { RegulatedActivitiesSortPipe } from '@tasks/aer/submit/regulated-activities/regulated-activities-sort.pipe';

import { AerRegulatedActivity } from 'pmrv-api';

describe('RegulatedActivitiesSortPipe', () => {
  const pipe = new RegulatedActivitiesSortPipe();

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should sort regulated activities', () => {
    expect(
      pipe.transform([
        { type: 'CEMENT_CLINKER_PRODUCTION' } as AerRegulatedActivity,
        { type: 'COMBUSTION' } as AerRegulatedActivity,
        { type: 'COKE_PRODUCTION' } as AerRegulatedActivity,
      ]),
    ).toEqual([
      { type: 'COMBUSTION' } as AerRegulatedActivity,
      { type: 'COKE_PRODUCTION' } as AerRegulatedActivity,
      { type: 'CEMENT_CLINKER_PRODUCTION' } as AerRegulatedActivity,
    ]);
  });

  it('should handle empty value', () => {
    const transformation = pipe.transform(null);
    expect(transformation).toEqual(null);
  });

  it('should handle undefined value', () => {
    const transformation = pipe.transform(undefined);
    expect(transformation).toEqual(undefined);
  });
});
