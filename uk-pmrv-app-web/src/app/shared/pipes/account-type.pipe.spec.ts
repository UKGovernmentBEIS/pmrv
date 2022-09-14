import { AccountTypePipe } from './account-type.pipe';

describe('AccountTypePipe', () => {
  let pipe: AccountTypePipe;

  beforeEach(() => (pipe = new AccountTypePipe()));

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('hould properly transform account types', () => {
    expect(pipe.transform('INSTALLATION')).toEqual('Installation');
    expect(pipe.transform('AVIATION')).toEqual('Aviation');
    expect(pipe.transform(undefined)).toEqual(null);
  });
});
