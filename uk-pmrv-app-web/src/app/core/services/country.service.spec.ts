import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { Country } from '../models/country';
import { CountryService } from './country.service';

const mockCountries = {
  COUNTRIES: [
    {
      code: 'PT',
      name: 'Portugal',
      officialName: 'The Portuguese Republic',
    },
    {
      code: 'PW',
      name: 'Palau',
      officialName: 'The Republic of Palau',
    },
    {
      code: 'GB',
      name: 'United Kingdom',
      officialName: 'United Kingdom',
    },
  ],
};
describe('CountryService', () => {
  let service: CountryService;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    httpTestingController = TestBed.inject(HttpTestingController);
    service = TestBed.inject(CountryService);
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should map countries to valid format', () => {
    service.getUkCountries().subscribe((c: Country[]) => {
      expect(c[0].code).toEqual('PT');
      expect(c[1].code).toEqual('PW');
    });

    const request = httpTestingController.expectOne('http://localhost:8080/api/v1.0/data?types=COUNTRIES');
    expect(request.request.method).toEqual('GET');
    request.flush(mockCountries);
  });

  it('should return country by code', () => {
    service.getCountry('GB').subscribe((country) => expect(country.name).toEqual('United Kingdom'));

    const request = httpTestingController.expectOne('http://localhost:8080/api/v1.0/data?types=COUNTRIES');
    expect(request.request.method).toEqual('GET');
    request.flush(mockCountries);
  });
});
