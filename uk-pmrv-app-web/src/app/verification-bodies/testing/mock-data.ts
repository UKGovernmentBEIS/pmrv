import {
  UserAuthorityInfoDTO,
  UsersAuthoritiesInfoDTO,
  VerificationBodyCreationDTO,
  VerificationBodyDTO,
  VerificationBodyInfoResponseDTO,
  VerificationBodyUpdateDTO,
} from 'pmrv-api';

export const mockVerificationBodies: VerificationBodyInfoResponseDTO = {
  verificationBodies: [
    { id: 3, name: 'B Verifier', status: 'ACTIVE' },
    { id: 4, name: 'A Verifier', status: 'DISABLED' },
    { id: 5, name: 'C Verifier', status: 'PENDING' },
  ],
  editable: true,
};

export const verificationBody: Omit<Required<VerificationBodyDTO>, 'id' | 'status'> = {
  name: 'Body test name',
  accreditationReferenceNumber: 'Accreditation ref num',
  address: {
    city: 'Body city',
    country: 'GR',
    line1: 'Body street',
    line2: null,
    postcode: 'Body post',
  },
  emissionTradingSchemes: ['UK_ETS_INSTALLATIONS'],
};

export const validVerificationBodyCreation: VerificationBodyCreationDTO = {
  ...verificationBody,
  adminVerifierUserInvitation: {
    email: 'body@admin.com',
    firstName: 'Body',
    lastName: 'Admin',
    mobileNumber: null,
    phoneNumber: '1111',
  },
};

export const validVerificationBodyUpdate: VerificationBodyUpdateDTO = {
  ...verificationBody,
  ...{ id: 123 },
};

export const verificationBodyContacts: UsersAuthoritiesInfoDTO = {
  authorities: [
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
      userId: 'verifier2',
      firstName: 'Jim',
      lastName: 'Cash',
      authorityStatus: 'ACTIVE',
      locked: false,
      authorityCreationDate: '2020-01-03T12:38:12.846716Z',
    },
  ] as UserAuthorityInfoDTO[],
  editable: true,
};
