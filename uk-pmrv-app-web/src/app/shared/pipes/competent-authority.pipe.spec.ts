import { CompetentAuthorityPipe } from './competent-authority.pipe';

describe('CompetentAuthorityPipe', () => {
  it('create an instance', () => {
    const pipe = new CompetentAuthorityPipe();
    expect(pipe).toBeTruthy();
  });

  it('should properly transform competent authorities', () => {
    const pipe = new CompetentAuthorityPipe();
    let transformation: string;

    transformation = pipe.transform('ENGLAND');
    expect(transformation).toEqual(`Environment Agency`);

    transformation = pipe.transform('SCOTLAND');
    expect(transformation).toEqual(`Scottish Environment Protection Agency`);

    transformation = pipe.transform('NORTHERN_IRELAND');
    expect(transformation).toEqual(`Northern Ireland Environment Agency`);

    transformation = pipe.transform('WALES');
    expect(transformation).toEqual(`Natural Resources Wales`);
  });
});
