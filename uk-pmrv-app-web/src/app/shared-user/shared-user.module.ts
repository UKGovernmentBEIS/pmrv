import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { PasswordStrengthMeterModule, PasswordStrengthMeterService } from 'angular-password-strength-meter';

import { SharedModule } from '../shared/shared.module';
import { VerifiersTableComponent } from './component/verifiers-table/verifiers-table.component';
import { UsersTableDirective } from './directives/users-table.directive';
import { PasswordComponent } from './password/password.component';
import { PasswordService } from './password/password.service';
import { SubmitIfEmptyPipe } from './pipes/submit-if-empty.pipe';
import { TwoFaLinkComponent } from './two-fa-link/two-fa-link.component';
import { UserLockedComponent } from './user-locked/user-locked.component';

@NgModule({
  declarations: [
    PasswordComponent,
    SubmitIfEmptyPipe,
    TwoFaLinkComponent,
    UserLockedComponent,
    UsersTableDirective,
    VerifiersTableComponent,
  ],
  imports: [PasswordStrengthMeterModule, RouterModule, SharedModule],
  providers: [PasswordService, PasswordStrengthMeterService],
  exports: [
    PasswordComponent,
    SubmitIfEmptyPipe,
    TwoFaLinkComponent,
    UserLockedComponent,
    UsersTableDirective,
    VerifiersTableComponent,
  ],
})
export class SharedUserModule {}
