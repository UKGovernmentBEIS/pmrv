import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, first, map, Observable, of, switchMap, takeUntil } from 'rxjs';

import { InvitedUserInfoDTO, VerifierUsersRegistrationService } from 'pmrv-api';

import { DestroySubject } from '../../core/services/destroy-subject.service';
import { catchBadRequest, ErrorCode } from '../../error/business-errors';
import { PASSWORD_FORM, passwordFormFactory } from '../../shared-user/password/password-form.factory';

@Component({
  selector: 'app-verifier-invitation',
  templateUrl: './verifier-invitation.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [passwordFormFactory, DestroySubject],
})
export class VerifierInvitationComponent implements OnInit {
  isSummaryDisplayed = new BehaviorSubject(false);

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly verifierUsersRegistrationService: VerifierUsersRegistrationService,
    private readonly destroy$: DestroySubject,
    @Inject(PASSWORD_FORM) public readonly form: FormGroup,
  ) {}

  ngOnInit(): void {
    (this.route.data as Observable<{ invitedUser: InvitedUserInfoDTO }>)
      .pipe(takeUntil(this.destroy$))
      .subscribe(({ invitedUser: { email } }) => {
        this.form.patchValue({ email });
      });
  }

  submitPassword(): void {
    if (this.form.valid) {
      this.route.queryParamMap
        .pipe(
          map((paramMap) => paramMap.get('token')),
          first(),
          switchMap((invitationToken) =>
            this.verifierUsersRegistrationService.acceptAndEnableVerifierInvitedUserUsingPUT({
              invitationToken,
              password: this.form.get('password').value,
            }),
          ),
          map(() => ({ url: 'confirmed' })),
          catchBadRequest([ErrorCode.EMAIL1001, ErrorCode.TOKEN1001, ErrorCode.USER1004], (res) =>
            of({ url: 'invalid-link', queryParams: { code: res.error.code } }),
          ),
        )
        .subscribe(({ queryParams, url }: { url: string; queryParams?: any }) =>
          this.router.navigate([url], { relativeTo: this.route, queryParams }),
        );
    } else {
      this.isSummaryDisplayed.next(true);
      this.form.markAllAsTouched();
    }
  }
}
