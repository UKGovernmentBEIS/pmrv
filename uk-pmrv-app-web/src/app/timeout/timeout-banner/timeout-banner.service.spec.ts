import { TestBed } from '@angular/core/testing';

import { firstValueFrom, Subject } from 'rxjs';
import { TestScheduler } from 'rxjs/testing';

import { KeycloakEvent, KeycloakEventType, KeycloakService } from 'keycloak-angular';
import { KeycloakInstance } from 'keycloak-js';

import { mockClass } from '../../../testing';
import { testSchedulerFactory } from '../../../testing/marble-helpers';
import { AuthService } from '../../core/services/auth.service';
import { TimeoutBannerService } from './timeout-banner.service';

describe('TimeoutBannerService', () => {
  let service: TimeoutBannerService;
  let testScheduler: TestScheduler;

  const mockRefreshTokenParsed = { iat: 0, exp: 120 };
  const mockRefreshTokenParsedNoExtension = { iat: 0, exp: 100 };
  const keycloakEvents$ = new Subject<KeycloakEvent>();

  const keycloakService: Partial<jest.Mocked<KeycloakService>> = {
    getKeycloakInstance: jest.fn().mockReturnValue({ refreshTokenParsed: mockRefreshTokenParsed }),
    keycloakEvents$,
    updateToken: jest.fn().mockReturnValue(Promise.resolve(true)),
    logout: jest.fn().mockImplementation(),
  };
  const authService = mockClass(AuthService);

  beforeEach(() => {
    testScheduler = testSchedulerFactory();
    TestBed.configureTestingModule({
      providers: [
        { provide: KeycloakService, useValue: keycloakService },
        { provide: AuthService, useValue: authService },
      ],
    });
    service = TestBed.inject(TimeoutBannerService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should logout user if time limit exceeded', () => {
    testScheduler.run(({ cold, expectObservable, flush }) => {
      jest.useFakeTimers();
      jest.setSystemTime(testScheduler.now());
      cold('-a- 200s', { a: { type: KeycloakEventType.OnAuthRefreshSuccess } }).subscribe((event) => {
        jest.setSystemTime(testScheduler.now());
        keycloakService.keycloakEvents$.next(event);
      });
      expectObservable(service.isVisible$).toBe('a 89s 999ms b 29s 999ms a', { a: false, b: true });

      flush();

      expect(keycloakService.logout).toHaveBeenCalled();
    });
  });

  it('should extend time session', async () => {
    await service.extendSession();

    expect(keycloakService.updateToken).toHaveBeenCalled();
    await expect(firstValueFrom(service.isVisible$)).resolves.toBeFalsy();
  });

  it('should not allow time extension', () => {
    testScheduler.run(({ cold, expectObservable }) => {
      jest.useFakeTimers();
      jest.setSystemTime(testScheduler.now());
      keycloakService.getKeycloakInstance.mockReturnValue({
        refreshTokenParsed: mockRefreshTokenParsedNoExtension,
      } as KeycloakInstance);
      cold('-a- 200s', { a: { type: KeycloakEventType.OnAuthRefreshSuccess } }).subscribe((event) => {
        jest.setSystemTime(testScheduler.now());
        keycloakService.keycloakEvents$.next(event);
      });
      expectObservable(service.timeExtensionAllowed$).toBe('ab', { a: true, b: false });
    });
  });
});
