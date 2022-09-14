import { TagColorPipe } from './tag-color.pipe';

describe('TagColorPipe', () => {
  it('create an instance', () => {
    const pipe = new TagColorPipe();

    expect(pipe).toBeTruthy();
  });

  it('should transform colors based on status', () => {
    const pipe = new TagColorPipe();

    expect(pipe.transform('not started')).toBe('grey');
    expect(pipe.transform('cannot start yet')).toBe('grey');
    expect(pipe.transform('in progress')).toBe('blue');
    expect(pipe.transform('incomplete')).toBe('red');
    expect(pipe.transform('needs review')).toBe('yellow');
    expect(pipe.transform('complete')).toBeNull();
    expect(pipe.transform('undecided')).toBe('grey');

    expect(pipe.transform('COMPLETED')).toBe('green');
    expect(pipe.transform('APPROVED')).toBe('green');
    expect(pipe.transform('IN_PROGRESS')).toBe('blue');
    expect(pipe.transform('CANCELLED')).toBe('red');
    expect(pipe.transform('WITHDRAWN')).toBe('red');
    expect(pipe.transform('REJECTED')).toBe('red');
  });
});
