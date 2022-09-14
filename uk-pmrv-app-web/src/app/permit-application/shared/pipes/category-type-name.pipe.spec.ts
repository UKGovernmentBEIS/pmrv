import { CategoryTypeNamePipe } from './category-type-name.pipe';

describe('CategoryTypeNamePipe', () => {
  let pipe: CategoryTypeNamePipe;

  beforeEach(async () => {
    pipe = new CategoryTypeNamePipe();
  });

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should transform value', () => {
    expect(pipe.transform('DE_MINIMIS')).toEqual('De-minimis');
    expect(pipe.transform('MAJOR')).toEqual('Major');
    expect(pipe.transform('MARGINAL')).toEqual('Marginal');
    expect(pipe.transform('MINOR')).toEqual('Minor');
    expect(pipe.transform(undefined)).toEqual('');
  });
});
