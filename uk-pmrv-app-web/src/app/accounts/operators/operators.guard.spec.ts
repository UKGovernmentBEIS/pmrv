import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { AccountOperatorsUsersAuthoritiesInfoDTO, OperatorAuthoritiesService, UserAuthorityInfoDTO } from 'pmrv-api';

import { mockClass, MockType } from '../../../testing';
import { OperatorsGuard } from './operators.guard';

describe('OperatorsGuard', () => {
  let guard: OperatorsGuard;
  let operatorAuthoritiesService: MockType<OperatorAuthoritiesService>;
  const callResponse: AccountOperatorsUsersAuthoritiesInfoDTO = {
    authorities: [
      {
        authorityStatus: 'ACTIVE',
        firstName: 'user 1',
        lastName: 'lastname 1',
        roleCode: 'test',
        roleName: 'test',
        userId: 'testing',
      },
    ] as UserAuthorityInfoDTO[],
    editable: true,
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [OperatorsGuard, { provide: OperatorAuthoritiesService, useValue: operatorAuthoritiesService }],
    });
    operatorAuthoritiesService = mockClass(OperatorAuthoritiesService);
    operatorAuthoritiesService.getAccountOperatorAuthoritiesUsingGET.mockReturnValueOnce(of(callResponse));
    guard = TestBed.inject(OperatorsGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });
});
