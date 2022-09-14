import { InstallationTypePipe } from './installation-type.pipe';

describe('InstallationTypePipe', () => {
  const pipe = new InstallationTypePipe();

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should transform installation type', () => {
    expect(pipe.transform('RECEIVING')).toEqual('Receiving installation');
    expect(pipe.transform('TRANSFERRING')).toEqual('Transferring installation');
  });
});
