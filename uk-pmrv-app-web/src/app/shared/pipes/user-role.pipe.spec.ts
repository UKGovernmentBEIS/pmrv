import { UserRolePipe } from './user-role.pipe';

describe('UserRolePipe', () => {
  let pipe: UserRolePipe;

  beforeEach(() => (pipe = new UserRolePipe()));

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should map task types to item names', () => {
    expect(pipe.transform('operator_admin')).toEqual('Operator admin');
    expect(pipe.transform('operator')).toEqual('Operator');
  });
});
