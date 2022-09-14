import { ChangeDetectionStrategy, Component } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Router } from '@angular/router';

import { switchMap } from 'rxjs';

import { GovukValidators } from 'govuk-components';

import { TermsDTO, UsersService } from 'pmrv-api';

import { AuthService } from '../core/services/auth.service';

@Component({
  selector: 'app-terms-and-conditions',
  templateUrl: './terms-and-conditions.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TermsAndConditionsComponent {
  terms: TermsDTO = this.authService.terms.getValue();

  form: FormGroup = this.fb.group({
    terms: [null, GovukValidators.required('You should accept terms and conditions to proceed')],
  });

  constructor(
    private readonly router: Router,
    private readonly usersService: UsersService,
    private readonly authService: AuthService,
    private readonly fb: FormBuilder,
  ) {}

  submitTerms(): void {
    if (this.form.valid) {
      this.usersService
        .editUserTermsUsingPATCH({ version: this.terms.version })
        .pipe(switchMap(() => this.authService.loadUser()))
        .subscribe(() => this.router.navigate(['']));
    }
  }
}
