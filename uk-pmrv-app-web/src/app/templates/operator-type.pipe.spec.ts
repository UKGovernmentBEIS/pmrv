import { OperatorTypePipe } from './operator-type.pipe';

describe('OperatorTypePipe', () => {
  let pipe: OperatorTypePipe;

  beforeEach(() => (pipe = new OperatorTypePipe()));

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should map request types to item types', () => {
    expect(pipe.transform('INSTALLATION')).toEqual('Installations');
    expect(pipe.transform('AVIATION')).toEqual('Aviations');
    expect(pipe.transform(null)).toBeNull();
  });
});
