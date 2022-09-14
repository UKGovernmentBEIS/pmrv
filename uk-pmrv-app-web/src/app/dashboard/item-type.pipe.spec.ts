import { ItemTypePipe } from './item-type.pipe';

describe('ItemTypePipe', () => {
  let pipe: ItemTypePipe;

  beforeEach(() => (pipe = new ItemTypePipe()));

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should map request types to item types', () => {
    expect(pipe.transform('INSTALLATION_ACCOUNT_OPENING')).toEqual('Installation account');
    expect(pipe.transform('SYSTEM_MESSAGE_NOTIFICATION')).toEqual('Message');
    expect(pipe.transform('PERMIT_ISSUANCE')).toEqual('Permit');
    expect(pipe.transform('PERMIT_SURRENDER')).toEqual('Permit');
    expect(pipe.transform('PERMIT_VARIATION')).toEqual('Permit');
    expect(pipe.transform(null)).toBeNull();
  });
});
