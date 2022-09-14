import { NotificationTypeDescriptionPipe } from './notification-type-description.pipe';

describe('NotificationTypeDescriptionPipe', () => {
  let pipe: NotificationTypeDescriptionPipe;

  beforeEach(async () => {
    pipe = new NotificationTypeDescriptionPipe();
  });

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should transform value', () => {
    expect(pipe.transform('TEMPORARY_FACTOR')).toEqual(
      'For a temporary deviation from your current monitoring plan.  For example, a broken meter makes it impossible to report emissions as specified in your plan',
    );
    expect(pipe.transform('TEMPORARY_CHANGE')).toEqual(
      'For a temporary change not directly related to the contents of your monitoring plan. For example, wanting to trial a new fuel or temporary generator over a short term period',
    );
    expect(pipe.transform('TEMPORARY_SUSPENSION')).toEqual('');
    expect(pipe.transform('NON_SIGNIFICANT_CHANGE')).toEqual(
      'For non-significant changes to the monitoring plan or monitoring methodology plan - those which do not change what and how you monitor or report your emissions.  For example, the name of a meter or procedure has changed',
    );
    expect(pipe.transform('OTHER_FACTOR')).toEqual(
      'For example, <br/> - GHGE low emitter permit holder who exceeds a threshold <br/> - HSE permit holder who exceeds a threshold <br/> - renounce free allocations <br/> - any other issue not covered in one of the other categories listed above <br/> ',
    );
    expect(pipe.transform(undefined)).toEqual('');
  });
});
