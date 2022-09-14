import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { FormControl } from '@angular/forms';

import { PasswordStrengthMeterService } from 'angular-password-strength-meter';

import { PasswordService } from './password.service';

const mockResponse = '1E4C9B93F3F0682250B6CF8331B7EE68FD8:3759315';

describe('PasswordService', () => {
  let service: PasswordService;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [PasswordService, PasswordStrengthMeterService],
    });
    httpTestingController = TestBed.inject(HttpTestingController);
    service = TestBed.inject(PasswordService);
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should blacklist password', () => {
    service.isBlacklistedPassword('password').subscribe((v) => expect(v).toBeTruthy());

    const req = httpTestingController.expectOne('https://api.pwnedpasswords.com/range/5baa6');
    expect(req.request.method).toEqual('GET');
    req.flush(mockResponse);
  });

  it('should return blacklisted error if the password does not pass validation', () => {
    const formControl = new FormControl('password', [], [(control) => service.blacklisted(control)]);
    formControl.updateValueAndValidity();

    const [cancelledRequest, req] = httpTestingController.match('https://api.pwnedpasswords.com/range/5baa6');
    expect(cancelledRequest.cancelled).toBeTruthy();
    expect(req.request.method).toEqual('GET');
    req.flush(mockResponse);

    expect(formControl.errors.blacklisted).toBeTruthy();
  });

  it('should return strong error if the password is not strong', () => {
    const formControl = new FormControl('password', [(control) => service.strong(control)]);
    formControl.updateValueAndValidity();

    expect(formControl.errors.weakPassword).toBeTruthy();
  });
});
