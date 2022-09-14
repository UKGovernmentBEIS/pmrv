import { Pipe, PipeTransform } from '@angular/core';

import { RequestActionUserInfo } from 'pmrv-api';

import { UserContactPipe } from './user-contact.pipe';
import { UserRolePipe } from './user-role.pipe';

@Pipe({ name: 'userInfoResolver' })
export class UserInfoResolverPipe implements PipeTransform {
  constructor(private userContact: UserContactPipe, private userRole: UserRolePipe) {}

  transform(userId: string, info: { [key: string]: RequestActionUserInfo }): string {
    const user = info[userId];
    const roleCode: string = user.roleCode ? `, ${this.userRole.transform(user.roleCode)}` : '';
    const contacts: string =
      user.contactTypes?.length > 0
        ? ` - ${user.contactTypes.map((c) => this.userContact.transform(c)).join(', ')}`
        : '';
    return `${user.name}${roleCode}${contacts}`;
  }
}
