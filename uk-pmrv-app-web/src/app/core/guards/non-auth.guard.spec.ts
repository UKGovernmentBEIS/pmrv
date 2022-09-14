import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { BehaviorSubject, lastValueFrom, of } from 'rxjs';

import { MockType } from '../../../testing';
import { AuthService } from '../services/auth.service';
import { NonAuthGuard } from './non-auth.guard';

describe('NonAuthGuard', () => {
  let guard: NonAuthGuard;
  let router: Router;

  const authService: MockType<AuthService> = {
    isLoggedIn: new BehaviorSubject<boolean>(null),
    checkUser: jest.fn(() => of(undefined)),
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [{ provide: AuthService, useValue: authService }],
    });
    guard = TestBed.inject(NonAuthGuard);
    router = TestBed.inject(Router);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should allow access if the user is not logged in', () => {
    authService.isLoggedIn.next(false);

    return expect(lastValueFrom(guard.canActivate())).resolves.toEqual(true);
  });

  it('should redirect to main route if the user is logged in', async () => {
    authService.isLoggedIn.next(true);
    const navigateSpy = jest.spyOn(router, 'parseUrl').mockImplementation();

    await lastValueFrom(guard.canActivate());

    expect(navigateSpy).toHaveBeenCalled();
  });
});
