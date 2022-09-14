import { InstallationCategoryPipe } from './installation-category.pipe';

describe('InstallationCategoryPipe', () => {
  it('create an instance', () => {
    const pipe = new InstallationCategoryPipe();
    expect(pipe).toBeTruthy();
  });

  it('shoult properly transform category values to respective literals', () => {
    const pipe = new InstallationCategoryPipe();
    expect(pipe.transform('A_LOW_EMITTER')).toEqual('Category A (Low emitter)');
    expect(pipe.transform('A')).toEqual('Category A');
    expect(pipe.transform('B')).toEqual('Category B');
    expect(pipe.transform('C')).toEqual('Category C');
    expect(pipe.transform('N_A')).toEqual('-');
  });
});
