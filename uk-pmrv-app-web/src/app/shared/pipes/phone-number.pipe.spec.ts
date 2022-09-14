import { ChangeDetectorRef } from '@angular/core';
import { fakeAsync, TestBed, tick } from '@angular/core/testing';

import { CountryServiceStub } from '../../../testing';
import { CountryService } from '../../core/services/country.service';
import { PhoneNumberPipe } from './phone-number.pipe';

describe('PhoneNumberPipe', () => {
  let pipe: PhoneNumberPipe;
  const changeDetectorSpy = {
    markForCheck: jest.fn(),
  };

  function transformCode(callingCode: string): string {
    pipe.transform(callingCode);
    tick();
    return pipe.transform(callingCode);
  }

  beforeEach(() => {
    changeDetectorSpy.markForCheck.mockReset();

    TestBed.configureTestingModule({
      declarations: [PhoneNumberPipe],
      providers: [
        { provide: CountryService, useClass: CountryServiceStub },
        { provide: ChangeDetectorRef, useValue: changeDetectorSpy },
      ],
    });
  });

  beforeEach(() => (pipe = new PhoneNumberPipe()));

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should return the country code and calling code', fakeAsync(() => {
    expect(transformCode('30')).toEqual('GR (30)');
  }));

  it('should return invalid country if country is not found', fakeAsync(() => {
    expect(transformCode('12')).toEqual('ZZ (12)');
  }));
});
