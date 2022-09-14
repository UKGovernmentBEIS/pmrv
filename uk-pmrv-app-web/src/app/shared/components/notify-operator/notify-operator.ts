import { AccountOperatorsUsersAuthoritiesInfoDTO, RequestActionUserInfo, UserAuthorityInfoDTO } from 'pmrv-api';

export interface AccountOperatorUser {
  firstName?: string;
  lastName?: string;
  roleCode?: string;
  userId?: string;
  contactTypes?: Array<string>;
}

export type NotifyAccountOperatorUsersInfo = {
  [key: string]: RequestActionUserInfo;
};

export function toAccountOperatorUser(
  userAuthorityInfo: UserAuthorityInfoDTO,
  contactTypes: AccountOperatorsUsersAuthoritiesInfoDTO['contactTypes'],
): AccountOperatorUser {
  return {
    firstName: userAuthorityInfo.firstName,
    lastName: userAuthorityInfo.lastName,
    roleCode: userAuthorityInfo.roleCode,
    userId: userAuthorityInfo.userId,
    contactTypes: Object.keys(contactTypes).filter((key) => contactTypes[key] === userAuthorityInfo.userId),
  };
}

export function toNotifyAccountOperatorUsersInfo(
  result: NotifyAccountOperatorUsersInfo,
  user: AccountOperatorUser,
): NotifyAccountOperatorUsersInfo {
  return {
    ...result,
    [user.userId]: {
      contactTypes: user.contactTypes as RequestActionUserInfo['contactTypes'],
      name: user.firstName ? user.firstName + ' ' + user.lastName : user.lastName,
      roleCode: user.roleCode,
    },
  };
}
