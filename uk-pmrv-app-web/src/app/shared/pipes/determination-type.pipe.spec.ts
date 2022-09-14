import { DeterminationTypePipe } from './determination-type.pipe';

describe('DeterminationTypePipe', () => {
  const pipe = new DeterminationTypePipe();
  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should transform the type literal', () => {
    expect(pipe.transform('GRANTED')).toEqual('Grant');
    expect(pipe.transform('REJECTED')).toEqual('Reject');
    expect(pipe.transform('DEEMED_WITHDRAWN')).toEqual('Deem withdrawn');
  });
});
