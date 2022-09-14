import { HttpErrorResponse, HttpStatusCode } from '@angular/common/http';
import { ErrorHandler } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, of } from 'rxjs';

import { mockClass } from '@testing';

import { AuthService } from '../services/auth.service';
import { GlobalErrorHandlingService } from './global-error-handling.service';

describe(`GlobalErrorHandlingService`, () => {
  let service: GlobalErrorHandlingService;
  let router: Router;
  const authService = mockClass(AuthService);

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [
        { provide: AuthService, useValue: authService },
        { provide: ErrorHandler, useClass: GlobalErrorHandlingService },
      ],
    });
    service = TestBed.inject(GlobalErrorHandlingService);
    router = TestBed.inject(Router);
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(service).toBeTruthy();
  });

  it('should handle uncaught application errors', () => {
    const navigateSpy = jest.spyOn(router, 'navigate').mockResolvedValueOnce(true);

    service.handleError(Error('Uncaught'));

    expect(navigateSpy).toHaveBeenCalledWith(['/error', '500'], {
      state: { forceNavigation: true },
      skipLocationChange: true,
    });
  });

  it('should handle uncaught http 404 error', () => {
    const navigateSpy = jest.spyOn(router, 'navigate').mockResolvedValueOnce(true);

    service.handleError(new HttpErrorResponse({ status: 404, statusText: 'test' }));

    expect(navigateSpy).toHaveBeenCalledWith(['/error', '404'], {
      state: { forceNavigation: true },
    });
  });

  it('should handle the 500 error', async () => {
    const navigateSpy = jest.spyOn(router, 'navigate').mockResolvedValueOnce(true);

    await expect(
      firstValueFrom(service.handleHttpError(new HttpErrorResponse({ status: 500, statusText: 'test' }))),
    ).rejects.toBeTruthy();

    expect(navigateSpy).toHaveBeenCalledTimes(1);
    expect(navigateSpy).toHaveBeenCalledWith(['/error', '500'], {
      state: { forceNavigation: true },
      skipLocationChange: true,
    });
  });

  it('should handle the 401 error', async () => {
    authService.login.mockResolvedValueOnce();

    await expect(
      firstValueFrom(
        service.handleHttpError(
          new HttpErrorResponse({
            status: HttpStatusCode.Unauthorized,
            statusText: 'test',
          }),
        ),
      ),
    ).rejects.toBeTruthy();

    expect(authService.login).toHaveBeenCalledTimes(1);
  });

  it('should handle the 403 error', async () => {
    authService.loadUserStatus.mockReturnValueOnce(of({}));
    const navigateSpy = jest.spyOn(router, 'navigate').mockResolvedValueOnce(true);

    await expect(
      firstValueFrom(
        service.handleHttpError(
          new HttpErrorResponse({
            status: HttpStatusCode.Forbidden,
            statusText: 'test',
          }),
        ),
      ),
    ).rejects.toBeTruthy();

    expect(authService.loadUserStatus).toHaveBeenCalledTimes(1);
    expect(navigateSpy).toHaveBeenCalledTimes(1);
    expect(navigateSpy).toHaveBeenCalledWith([''], { state: { forceNavigation: true } });
    expect(authService.logout).not.toHaveBeenCalled();
  });

  it('should logout after a 403 with DELETED status', async () => {
    authService.loadUserStatus.mockReturnValueOnce(of({ loginStatus: 'DELETED' }));
    authService.logout.mockResolvedValueOnce();
    const navigateSpy = jest.spyOn(router, 'navigate').mockResolvedValueOnce(true);

    await expect(
      firstValueFrom(service.handleHttpError(new HttpErrorResponse({ status: 403, statusText: 'test' }))),
    ).rejects.toBeTruthy();

    expect(authService.loadUserStatus).toHaveBeenCalledTimes(1);
    expect(navigateSpy).not.toHaveBeenCalled();
    expect(authService.logout).toHaveBeenCalledTimes(1);
  });

  it('should forward not handled errors', () => {
    const navigateSpy = jest.spyOn(router, 'navigate').mockResolvedValueOnce(true);
    const error = new HttpErrorResponse({ status: 400, statusText: 'test' });

    expect(firstValueFrom(service.handleHttpError(error))).rejects.toEqual(error);
    expect(navigateSpy).not.toHaveBeenCalled();
  });
});
