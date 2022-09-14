import { SubmitIfEmptyPipe } from './submit-if-empty.pipe';

describe('SubmitIfEmptyPipe', () => {
  const pipe = new SubmitIfEmptyPipe();

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should return submit on empty value', () => {
    expect(pipe.transform(null)).toEqual('Submit');
    expect(pipe.transform(undefined)).toEqual('Submit');
  });

  it('should return save on value', () => {
    expect(pipe.transform('test')).toEqual('Save');
    expect(pipe.transform({ test: 'test' })).toEqual('Save');
  });
});
