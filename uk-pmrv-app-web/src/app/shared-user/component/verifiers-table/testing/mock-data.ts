import { UserAuthorityInfoDTO, UsersAuthoritiesInfoDTO } from 'pmrv-api';

const mockVerifierAuthorities: UserAuthorityInfoDTO[] = [
  {
    roleCode: 'verifier',
    roleName: 'Verifier',
    userId: 'verifier2',
    firstName: 'Jim',
    lastName: 'Cash',
    authorityStatus: 'DISABLED',
    locked: false,
    authorityCreationDate: '2020-01-03T12:38:12.846716Z',
  },
  {
    roleCode: 'verifier_admin',
    roleName: 'Verifier admin',
    userId: 'verifier1',
    firstName: 'John',
    lastName: 'Cash',
    authorityStatus: 'ACTIVE',
    locked: false,
    authorityCreationDate: '2020-01-01T12:38:12.846716Z',
  },
  {
    roleCode: 'verifier',
    roleName: 'Verifier',
    userId: 'verifier3',
    firstName: 'Pit',
    lastName: 'Cash',
    authorityStatus: 'PENDING',
    locked: false,
    authorityCreationDate: '2020-01-08T12:38:12.846716Z',
  },
  {
    roleCode: 'verifier',
    roleName: 'Verifier',
    userId: 'verifier4',
    firstName: 'Patty',
    lastName: 'Cash',
    authorityStatus: 'ACTIVE',
    locked: true,
    authorityCreationDate: '2020-01-06T12:38:12.846716Z',
  },
];

export const mockEditableVerifiersAuthorities: UsersAuthoritiesInfoDTO = {
  authorities: mockVerifierAuthorities,
  editable: true,
};

export const mockNonEditableVerifiersAuthorities: UsersAuthoritiesInfoDTO = {
  authorities: mockVerifierAuthorities,
  editable: false,
};
