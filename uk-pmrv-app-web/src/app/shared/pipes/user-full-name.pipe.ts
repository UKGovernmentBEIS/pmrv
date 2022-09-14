import { Pipe, PipeTransform } from '@angular/core';

import { UserInfoDTO } from 'pmrv-api';

@Pipe({ name: 'userFullName' })
export class UserFullNamePipe implements PipeTransform {
  transform(userDto: UserInfoDTO): string {
    return userDto.firstName ? `${userDto.firstName} ${userDto.lastName}` : `${userDto.lastName}`;
  }
}
