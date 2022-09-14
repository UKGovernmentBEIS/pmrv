import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, switchMap, takeUntil, tap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import {
  effectiveDateMinValidator,
  feeDateMinValidator,
  PERMIT_REVOCATION_TASK_FORM,
  permitRevocationFormProvider,
} from '@permit-revocation/factory/permit-revocation-form-provider';
import { PermitRevocationStore } from '@permit-revocation/store/permit-revocation-store';
import { BackLinkService } from '@shared/back-link/back-link.service';

@Component({
  selector: 'app-confirm-answers',
  templateUrl: './confirm-answers.component.html',
  providers: [BackLinkService, permitRevocationFormProvider, DestroySubject],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ConfirmAnswersComponent implements OnInit {
  constructor(
    @Inject(PERMIT_REVOCATION_TASK_FORM) readonly form: FormGroup,
    readonly store: PermitRevocationStore,
    readonly route: ActivatedRoute,
    readonly router: Router,
    private readonly pendingRequest: PendingRequestService,
    private readonly backLinkService: BackLinkService,
    private readonly destroy$: DestroySubject,
  ) {}

  ngOnInit(): void {
    this.route.paramMap
      .pipe(takeUntil(this.destroy$))
      .subscribe((paramMap) => this.backLinkService.show(`/permit-revocation/${paramMap.get('taskId')}`));
    this.store
      .pipe(
        first(),
        tap((state) => {
          this.form.addValidators(effectiveDateMinValidator());
          if (state.permitRevocation?.feeDate) {
            this.form.addValidators(feeDateMinValidator());
          }
        }),
      )
      .subscribe();
  }

  confirm() {
    this.form.updateValueAndValidity();
    if (this.form.valid) {
      combineLatest([this.route.data, this.store])
        .pipe(
          first(),
          switchMap(([data, state]) =>
            this.store.postApplyPermitRevocation({
              ...state,
              sectionsCompleted: {
                ...state.sectionsCompleted,
                [data.statusKey]: true,
              },
            }),
          ),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() =>
          this.router.navigate(['..', 'summary'], { relativeTo: this.route, state: { notification: true } }),
        );
    }
  }
}
