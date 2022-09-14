import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { BehaviorSubject, lastValueFrom } from 'rxjs';

import { UserStatusDTO } from 'pmrv-api';

import { AuthService } from '../../core/services/auth.service';
import { UserRoleTypeGuard } from './user-role-type.guard';

describe('UserRoleTypeGuard', () => {
  let guard: UserRoleTypeGuard;
  let router: Router;
  let authService: Partial<jest.Mocked<AuthService>>;

  const createModule = () => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [{ provide: AuthService, useValue: authService }],
    });
    guard = TestBed.inject(UserRoleTypeGuard);
    router = TestBed.inject(Router);
  };

  describe('for regulators', () => {
    beforeEach(async () => {
      authService = {
        userStatus: new BehaviorSubject<UserStatusDTO>({
          loginStatus: 'ENABLED',
          userId: 'ABC1',
          roleType: 'REGULATOR',
        }),
      };
    });

    beforeEach(createModule);

    it('should be created', () => {
      expect(guard).toBeTruthy();
    });

    it('should  activate for regulator user', async () => {
      await expect(lastValueFrom(guard.canActivate())).resolves.toBeTruthy();
    });
  });

  describe('for operators', () => {
    beforeEach(async () => {
      authService = {
        userStatus: new BehaviorSubject<UserStatusDTO>({
          loginStatus: 'ENABLED',
          userId: 'ABC1',
          roleType: 'OPERATOR',
        }),
      };
    });

    beforeEach(createModule);

    it('should be created', () => {
      expect(guard).toBeTruthy();
    });

    it('should NOT activate for OPERATOR user', async () => {
      await expect(lastValueFrom(guard.canActivate())).resolves.toEqual(router.parseUrl('/dashboard'));
    });
  });
});
