import { NotificationTypePipe } from './notification-type.pipe';

describe('NotificationTypePipe', () => {
  let pipe: NotificationTypePipe;

  beforeEach(async () => {
    pipe = new NotificationTypePipe();
  });

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should transform value', () => {
    expect(pipe.transform('TEMPORARY_FACTOR')).toEqual(
      'Temporary factor preventing compliance with a permit condition',
    );
    expect(pipe.transform('TEMPORARY_CHANGE')).toEqual('Temporary change to the permitted installation');
    expect(pipe.transform('TEMPORARY_SUSPENSION')).toEqual('Temporary suspension of a regulated activity');
    expect(pipe.transform('NON_SIGNIFICANT_CHANGE')).toEqual('Non-significant change');
    expect(pipe.transform('OTHER_FACTOR')).toEqual('Some other factor');
    expect(pipe.transform(undefined)).toEqual('');
  });
});
