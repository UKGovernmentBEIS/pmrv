import { TestBed } from '@angular/core/testing';

import { UserContactPipe } from './user-contact.pipe';
import { UserInfoResolverPipe } from './user-info-resolver.pipe';
import { UserRolePipe } from './user-role.pipe';

describe('UserInfoResolverPipe', () => {
  let pipe: UserInfoResolverPipe;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [UserInfoResolverPipe],
    });
  });

  beforeEach(() => (pipe = new UserInfoResolverPipe(new UserContactPipe(), new UserRolePipe())));

  it('should create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should display an operator', () => {
    expect(
      pipe.transform('operator', {
        operator: { name: 'operator name', roleCode: 'operator_admin', contactTypes: ['FINANCIAL', 'PRIMARY'] },
      }),
    ).toEqual('operator name, Operator admin - Financial contact, Primary contact');
  });

  it('should display a regulator', () => {
    expect(
      pipe.transform('regulator', {
        regulator: { name: 'regulator name' },
      }),
    ).toEqual('regulator name');
  });
});
