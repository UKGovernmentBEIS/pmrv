import { CompetentAuthorityLocationPipe } from './competent-authority-location.pipe';

describe('CompetentAuthorityPipe', () => {
  it('create an instance', () => {
    const pipe = new CompetentAuthorityLocationPipe();
    expect(pipe).toBeTruthy();
  });

  it('should properly transform competent authorities', () => {
    const pipe = new CompetentAuthorityLocationPipe();
    let transformation: string;

    transformation = pipe.transform('ENGLAND');
    expect(transformation).toEqual(`England`);

    transformation = pipe.transform('WALES');
    expect(transformation).toEqual(`Wales`);

    transformation = pipe.transform('SCOTLAND');
    expect(transformation).toEqual(`Scotland`);

    transformation = pipe.transform('NORTHERN_IRELAND');
    expect(transformation).toEqual(`Northern Ireland`);
  });
});
