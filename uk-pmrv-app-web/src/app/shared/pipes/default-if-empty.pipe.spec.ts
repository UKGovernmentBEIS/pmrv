import { DefaultIfEmptyPipe } from './default-if-empty.pipe';

describe('DefaultIfEmpty', () => {
  const pipe = new DefaultIfEmptyPipe();

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should return minus(-) if null value', () => {
    const transformation = pipe.transform(null);

    expect(transformation).toEqual('-');
  });

  it('should return minus(-) if empty value', () => {
    const transformation = pipe.transform('');

    expect(transformation).toEqual('-');
  });

  it('should return minus(-) if undefined value', () => {
    const transformation = pipe.transform(undefined);

    expect(transformation).toEqual('-');
  });

  it('should return replacement if null value', () => {
    const transformation = pipe.transform(null, 'test');

    expect(transformation).toEqual('test');
  });

  it('should return replacement if undefined value', () => {
    const transformation = pipe.transform(undefined, 'test');

    expect(transformation).toEqual('test');
  });

  it('should return value if not null', () => {
    const transformation = pipe.transform(10, 'test');

    expect(transformation).toEqual(10);
  });
});
