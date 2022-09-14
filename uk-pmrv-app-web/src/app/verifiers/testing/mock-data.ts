import {
  AccountContactVbInfoResponse,
  RoleDTO,
  UserAuthorityInfoDTO,
  UsersAuthoritiesInfoDTO,
  UserStatusDTO,
  VerifierUserDTO,
} from 'pmrv-api';

export const verifierUserRole: UserStatusDTO = {
  loginStatus: 'ENABLED',
  roleType: 'VERIFIER',
  userId: 'asdf1',
};

export const mockVerifierRoleCodes: RoleDTO[] = [
  {
    name: 'Verifier admin',
    code: 'verifier_admin',
  },
  {
    name: 'Verifier',
    code: 'verifier',
  },
];

export const mockVerifiersRouteData: { verifiers: UsersAuthoritiesInfoDTO } = {
  verifiers: {
    authorities: [
      {
        roleCode: 'verifier',
        roleName: 'Verifier',
        userId: '1reg',
        firstName: 'Alfyn',
        lastName: 'Octo',
        authorityStatus: 'DISABLED',
        locked: false,
        authorityCreationDate: '2020-12-14T12:38:12.846716Z',
      },
      {
        roleCode: 'verifier_admin',
        roleName: 'Verifier admin',
        userId: '2reg',
        firstName: 'Therion',
        lastName: 'Path',
        authorityStatus: 'ACTIVE',
        locked: true,
        authorityCreationDate: '2020-12-15T12:38:12.846716Z',
      },
      {
        roleCode: 'verifier',
        roleName: 'Verifier',
        userId: '3reg',
        firstName: 'Olberik',
        lastName: 'Traveler',
        authorityStatus: 'PENDING',
        locked: true,
        authorityCreationDate: '2020-11-10T12:38:12.846716Z',
      },
      {
        roleCode: 'verifier',
        roleName: 'Verifier',
        userId: '4reg',
        firstName: 'Tyrion',
        lastName: 'Lanister',
        authorityStatus: 'ACTIVE',
        locked: false,
        authorityCreationDate: '2020-12-16T12:38:12.846716Z',
      },
    ] as UserAuthorityInfoDTO[],
    editable: true,
  },
};

export const mockVerifier: VerifierUserDTO = {
  firstName: 'Tyrion',
  lastName: 'Lanister',
  email: 'tyrion@got.com',
  phoneNumber: '12312321',
};

export const mockAccountContactVbInfoResponse: AccountContactVbInfoResponse = {
  contacts: [
    {
      accountId: 1,
      accountName: 'Account 1',
      type: 'UK ETS Installation',
      userId: mockVerifiersRouteData.verifiers.authorities[1].userId,
    },
    { accountId: 2, accountName: 'Account 2', type: 'UK ETS Installation' },
    { accountId: 3, accountName: 'Account 3', type: 'UK ETS Installation' },
  ],
  editable: true,
  totalItems: 3,
};
