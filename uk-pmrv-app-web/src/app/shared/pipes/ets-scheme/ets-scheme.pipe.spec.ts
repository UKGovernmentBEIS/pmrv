import { EtsSchemePipe } from './ets-scheme.pipe';

describe('EtsSchemePipe', () => {
  it('create an instance', () => {
    const pipe = new EtsSchemePipe();
    expect(pipe).toBeTruthy();
  });

  it('should transform UK installation', () => {
    const pipe = new EtsSchemePipe();
    const transformation = pipe.transform('UK_ETS_INSTALLATIONS');

    expect(transformation).toEqual(`UK ETS`);
  });

  it('should transform EU installation', () => {
    const pipe = new EtsSchemePipe();
    const transformation = pipe.transform('EU_ETS_INSTALLATIONS');

    expect(transformation).toEqual(`EU ETS`);
  });
});
