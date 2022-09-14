import { ChangeDetectorRef } from '@angular/core';
import { fakeAsync, inject, TestBed, tick } from '@angular/core/testing';

import { throwError } from 'rxjs';

import { CountryServiceStub } from '../../../testing';
import { CountryService } from '../../core/services/country.service';
import { CountryPipe } from './country.pipe';

describe('CountryPipe', () => {
  let pipe: CountryPipe;
  const changeDetectorSpy = {
    markForCheck: jest.fn(),
  };

  function transformCode(code: string): string {
    pipe.transform(code);
    tick();
    return pipe.transform(code);
  }

  beforeEach(() => {
    changeDetectorSpy.markForCheck.mockReset();

    TestBed.configureTestingModule({
      declarations: [CountryPipe],
      providers: [
        { provide: CountryService, useClass: CountryServiceStub },
        { provide: ChangeDetectorRef, useValue: changeDetectorSpy },
      ],
    });
  });

  beforeEach(() => (pipe = new CountryPipe(TestBed.inject(CountryService), TestBed.inject(ChangeDetectorRef))));

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should return the country name', fakeAsync(() => {
    expect(transformCode('GR')).toEqual('Greece');
  }));

  it('should return invalid country if country is not found', fakeAsync(() => {
    expect(transformCode('GO')).toEqual('Invalid country');
  }));

  it('should return empty string if not EmptyError', fakeAsync(
    inject([CountryService], (countryService: CountryService) => {
      jest
        .spyOn(countryService, 'getCountry')
        .mockImplementation(() => throwError(() => new Error('Not an EmptyError')));
      expect(transformCode('GO')).toEqual('');
    }),
  ));
});
