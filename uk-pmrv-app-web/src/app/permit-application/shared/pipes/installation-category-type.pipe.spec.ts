import { InstallationCategoryTypePipe } from './installation-category-type.pipe';

describe('InstallationCategoryTypePipe', () => {
  it('create an instance', () => {
    const pipe = new InstallationCategoryTypePipe();
    expect(pipe).toBeTruthy();
  });

  it('shoult properly transform category values to respective literals', () => {
    const pipe = new InstallationCategoryTypePipe();
    expect(pipe.transform(10)).toEqual('A_LOW_EMITTER');
    expect(pipe.transform(26000)).toEqual('A');
    expect(pipe.transform(100000)).toEqual('B');
    expect(pipe.transform(600000)).toEqual('C');
  });
});
