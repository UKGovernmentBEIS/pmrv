import { TestBed } from '@angular/core/testing';

import { GovukDatePipe } from './govuk-date.pipe';

describe('GovukDatePipe', () => {
  let pipe: GovukDatePipe;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [GovukDatePipe],
      declarations: [GovukDatePipe],
    });
    pipe = TestBed.inject(GovukDatePipe);
  });

  it('should create', () => {
    expect(pipe).toBeTruthy();
  });

  it('should handle null, undefiend, empty value', () => {
    let transformation = pipe.transform(null);

    expect(transformation).toEqual('');

    transformation = pipe.transform(undefined);

    expect(transformation).toEqual('');

    transformation = pipe.transform('');

    expect(transformation).toEqual('');
  });

  it('should transform Date object', () => {
    let transformation = pipe.transform(new Date(Date.UTC(2021, 5, 9, 14, 27)), 'datetime');

    expect(transformation).toEqual('9 Jun 2021, 3:27pm');

    transformation = pipe.transform(new Date(Date.UTC(2021, 5, 9, 14, 27)));

    expect(transformation).toEqual('9 Jun 2021');
  });

  it('should transform date string', () => {
    let transformation = pipe.transform('2021-04-09');

    expect(transformation).toEqual('9 Apr 2021');

    transformation = pipe.transform('2021-04-26T12:48:37.786771Z');

    expect(transformation).toEqual('26 Apr 2021');

    transformation = pipe.transform('2021-04-26T12:48:37.786771Z', 'datetime');

    expect(transformation).toEqual('26 Apr 2021, 1:48pm');
  });
});
