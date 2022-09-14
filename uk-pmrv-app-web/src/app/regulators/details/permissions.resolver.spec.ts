import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BehaviorSubject, lastValueFrom } from 'rxjs';

import { RegulatorAuthoritiesService, UserStatusDTO } from 'pmrv-api';

import { ActivatedRouteSnapshotStub, asyncData } from '../../../testing';
import { AuthService } from '../../core/services/auth.service';
import { PermissionsResolver } from './permissions.resolver';

describe('PermissionsResolver', () => {
  let resolver: PermissionsResolver;
  let regulatorAuthService: Partial<jest.Mocked<RegulatorAuthoritiesService>>;

  const permissions = {
    editable: true,
    permissions: {
      ADD_OPERATOR_ADMIN: 'EXECUTE',
      ASSIGN_REASSIGN_TASKS: 'EXECUTE',
      MANAGE_USERS_AND_CONTACTS: 'EXECUTE',
      REVIEW_INSTALLATION_ACCOUNT: 'EXECUTE',
      REVIEW_PERMIT_APPLICATION: 'EXECUTE',
      PEER_REVIEW_PERMIT_APPLICATION: 'EXECUTE',
      REVIEW_PERMIT_SURRENDER: 'EXECUTE',
      PEER_REVIEW_PERMIT_SURRENDER: 'EXECUTE',
      SUBMIT_PERMIT_REVOCATION: 'EXECUTE',
      PEER_REVIEW_PERMIT_REVOCATION: 'EXECUTE',
      REVIEW_PERMIT_NOTIFICATION: 'EXECUTE',
      PEER_REVIEW_PERMIT_NOTIFICATION: 'EXECUTE',
    },
  };

  beforeEach(() => {
    regulatorAuthService = {
      getCurrentRegulatorUserPermissionsByCaUsingGET: jest.fn().mockReturnValue(asyncData(permissions)),
      getRegulatorUserPermissionsByCaAndIdUsingGET: jest.fn().mockReturnValue(asyncData(permissions)),
    };

    const authService = {
      userStatus: new BehaviorSubject<UserStatusDTO>({ loginStatus: 'ENABLED', userId: 'ABC1', roleType: 'REGULATOR' }),
    };

    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [
        { provide: RegulatorAuthoritiesService, useValue: regulatorAuthService },
        { provide: AuthService, useValue: authService },
      ],
    });
    resolver = TestBed.inject(PermissionsResolver);
  });

  it('should be created', () => {
    expect(resolver).toBeTruthy();
  });

  it('should provide the permissions of another user', async () => {
    await expect(
      lastValueFrom(resolver.resolve(new ActivatedRouteSnapshotStub({ userId: '1234567' }))),
    ).resolves.toEqual(permissions);

    expect(regulatorAuthService.getRegulatorUserPermissionsByCaAndIdUsingGET).toHaveBeenCalledWith('1234567');
  });

  it('should provide current user permissions', async () => {
    await expect(
      lastValueFrom(resolver.resolve(new ActivatedRouteSnapshotStub({ accountId: '1', userId: 'ABC1' }))),
    ).resolves.toEqual(permissions);

    expect(regulatorAuthService.getCurrentRegulatorUserPermissionsByCaUsingGET).toHaveBeenCalled();
  });
});
