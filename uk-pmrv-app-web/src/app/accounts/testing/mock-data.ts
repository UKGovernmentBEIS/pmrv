import { address } from '@testing';

import {
  AccountDetailsDTO,
  AccountOperatorsUsersAuthoritiesInfoDTO,
  AccountSearchResults,
  LocationOnShoreDTO,
  OperatorUserDTO,
  RequestDetailsSearchResults,
  RoleDTO,
  UserAuthorityInfoDTO,
  UserStatusDTO,
} from 'pmrv-api';

export const mockOperatorListRouteData: { users: AccountOperatorsUsersAuthoritiesInfoDTO } = {
  users: {
    authorities: [
      {
        authorityStatus: 'ACTIVE',
        firstName: 'First',
        lastName: 'User',
        roleCode: 'operator_admin',
        roleName: 'Operator admin',
        userId: 'userTest1',
        authorityCreationDate: '2019-12-21T13:42:43.050682Z',
        locked: true,
      },
      {
        authorityStatus: 'ACTIVE',
        firstName: 'John',
        lastName: 'Doe',
        roleCode: 'operator',
        roleName: 'Operator',
        userId: 'userTest2',
        authorityCreationDate: '2020-12-21T13:42:43.050682Z',
        locked: false,
      },
      {
        authorityStatus: 'DISABLED',
        firstName: 'Darth',
        lastName: 'Vader',
        roleCode: 'operator',
        roleName: 'Operator',
        userId: 'userTest3',
        authorityCreationDate: '2020-10-13T13:42:43.050682Z',
        locked: false,
      },
      {
        authorityStatus: 'ACTIVE',
        firstName: 'anakin',
        lastName: 'skywalker',
        roleCode: 'operator',
        roleName: 'Operator',
        userId: 'userTest4',
        authorityCreationDate: '2021-01-13T13:42:43.050682Z',
        locked: false,
      },
    ] as UserAuthorityInfoDTO[],
    contactTypes: {
      PRIMARY: 'userTest1',
      SECONDARY: 'userTest3',
      SERVICE: 'userTest2',
      FINANCIAL: 'userTest4',
    },
    editable: true,
  },
};

export const mockOperatorRoleCodes: RoleDTO[] = [
  {
    name: 'Operator admin',
    code: 'operator_admin',
  },
  {
    name: 'Operator',
    code: 'operator',
  },
  {
    name: 'Consultant / Agent',
    code: 'consultant_agent',
  },
  {
    name: 'Emitter Contact',
    code: 'emitter_contact',
  },
];

export const operator: OperatorUserDTO = {
  address,
  email: 'test@host.com',
  firstName: 'Mary',
  lastName: 'Za',
  mobileNumber: { countryCode: '30', number: '1234567890' },
  phoneNumber: { countryCode: '30', number: '123456780' },
};

export const operatorUserRole: UserStatusDTO = {
  loginStatus: 'ENABLED',
  roleType: 'OPERATOR',
  userId: 'asdf4',
};

export const regulatorUserRole: UserStatusDTO = {
  loginStatus: 'ENABLED',
  roleType: 'REGULATOR',
  userId: 'asdf4',
};

export const mockAccountResults: AccountSearchResults = {
  accounts: [
    { id: 1, name: 'account1', emitterId: 'EM00001', status: 'LIVE', legalEntityName: 'le1' },
    { id: 1, name: 'account2', emitterId: 'EM00002', status: 'LIVE', legalEntityName: 'le2' },
    { id: 1, name: 'account3', emitterId: 'EM00003', status: 'LIVE', legalEntityName: 'le3' },
  ],
  total: 3,
};

export const mockedAccount: AccountDetailsDTO = {
  id: 1,
  name: 'accountName',
  accountType: 'INSTALLATION',
  status: 'LIVE',
  siteName: 'siteName',
  location: {
    type: 'ONSHORE',
    gridReference: 'NN166712',
    address: {
      line1: 'line',
      line2: null,
      city: 'town',
      country: 'GR',
      postcode: '1231',
    },
  } as LocationOnShoreDTO,
  sopId: 111,
  registryId: 222,
  permitId: 'permitId',
  installationCategory: 'A_LOW_EMITTER',
  emitterType: 'GHGE',
  subsistenceCategory: 'A',
  applicationType: 'NEW_PERMIT',
  legalEntityName: 'leName',
  legalEntityType: 'LIMITED_COMPANY',
  legalEntityAddress: {
    line1: 'line',
    line2: null,
    city: 'town',
    country: 'GR',
    postcode: '1231',
  },
  companyReferenceNumber: '11111',
};

export const mockedOffshoreAccount = {
  id: 38,
  name: 'Operator 11 Instaccount',
  accountType: 'INSTALLATION',
  status: 'APPROVED',
  siteName: 'siteName',
  location: {
    type: 'OFFSHORE',
    latitude: {
      degree: 12,
      minute: 12,
      second: 4,
      cardinalDirection: 'NORTH',
    },
    longitude: {
      degree: 3,
      minute: 3,
      second: 4,
      cardinalDirection: 'EAST',
    },
  },
  emitterId: 'EM00038',
  legalEntityName: 'Instaccount',
  companyReferenceNumber: '09546038',
};

export const mockWorkflowResults: RequestDetailsSearchResults = {
  requestDetails: [
    {
      id: '2',
      requestType: 'PERMIT_ISSUANCE',
      requestStatus: 'IN_PROGRESS',
      creationDate: new Date('2022-12-12').toISOString(),
    },
    {
      id: '1',
      requestType: 'INSTALLATION_ACCOUNT_OPENING',
      requestStatus: 'APPROVED',
      creationDate: new Date('2022-12-11').toISOString(),
    },
  ],
  total: 2,
};
