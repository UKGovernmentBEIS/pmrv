import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { BehaviorSubject, lastValueFrom, of } from 'rxjs';

import { KeycloakService } from 'keycloak-angular';

import { mockClass, MockType } from '../../testing';
import { AuthService } from '../core/services/auth.service';
import { FeedbackGuard } from './feedback.guard';

describe('FeedbackGuard', () => {
  let guard: FeedbackGuard;
  let router: Router;

  const keycloakService = mockClass(KeycloakService);

  const isLoggedIn$ = new BehaviorSubject<boolean>(null);

  const authService: MockType<AuthService> = {
    isLoggedIn: isLoggedIn$,
    checkUser: jest.fn(() => of(undefined)),
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [
        { provide: KeycloakService, useValue: keycloakService },
        { provide: AuthService, useValue: authService },
      ],
    });
    guard = TestBed.inject(FeedbackGuard);
    router = TestBed.inject(Router);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should redirect to landing page if user is not logged in', async () => {
    isLoggedIn$.next(false);
    await expect(lastValueFrom(guard.canActivate())).resolves.toEqual(router.parseUrl(''));
  });

  it('should activate when user is logged in', async () => {
    isLoggedIn$.next(true);
    await expect(lastValueFrom(guard.canActivate())).resolves.toEqual(true);
  });
});
