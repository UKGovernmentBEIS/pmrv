import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, Observable, pluck, switchMap, switchMapTo, take, takeUntil } from 'rxjs';

import { GovukValidators } from 'govuk-components';

import { PendingRequestService } from '../../../../core/guards/pending-request.service';
import { PendingRequest } from '../../../../core/interfaces/pending-request.interface';
import { DestroySubject } from '../../../../core/services/destroy-subject.service';
import { BackLinkService } from '../../../../shared/back-link/back-link.service';
import { PERMIT_TASK_FORM } from '../../../shared/permit-task-form.token';
import { PermitApplicationStore } from '../../../store/permit-application.store';
import { reasonFormProvider } from './reason-form.provider';

@Component({
  selector: 'app-reason',
  template: `
    <app-permit-task>
      <app-wizard-step
        (formSubmit)="onContinue()"
        [formGroup]="form"
        submitText="Continue"
        [hideSubmit]="(store.isEditable$ | async) === false"
      >
        <span class="govuk-caption-l">{{ caption$ | async | determinationType }}</span>

        <app-page-heading>{{ header$ | async }}</app-page-heading>
        <div class="govuk-hint">This cannot be viewed by the operator</div>

        <div formControlName="reason" govuk-textarea [maxLength]="10000"></div>
      </app-wizard-step>

      <a govukLink routerLink="../..">Return to: Permit determination</a>
    </app-permit-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [reasonFormProvider, BackLinkService],
})
export class ReasonComponent implements PendingRequest, OnInit {
  header$ = this.store.pipe(
    pluck('determination', 'type'),
    map((type) =>
      type === 'GRANTED'
        ? 'Provide a reason to support your decision'
        : type === 'REJECTED'
        ? 'Provide a reason to support the rejection decision'
        : 'Provide a reason for the application withdrawal',
    ),
  );
  caption$ = this.store.pipe(pluck('determination', 'type')) as Observable<string>;

  constructor(
    @Inject(PERMIT_TASK_FORM) readonly form: FormGroup,
    readonly store: PermitApplicationStore,
    readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly backLinkService: BackLinkService,
    private readonly destroy$: DestroySubject,
  ) {}

  ngOnInit(): void {
    this.backLinkService.show();
    this.store
      .pipe(
        pluck('determination', 'type'),
        map((type) =>
          type === 'DEEMED_WITHDRAWN'
            ? 'Enter a reason for the application withdrawal.'
            : 'Enter a reason to support your decision.',
        ),
        takeUntil(this.destroy$),
      )
      .subscribe((m) => {
        this.form
          .get('reason')
          .setValidators([
            GovukValidators.required(m),
            GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
          ]);
        this.form.get('reason').updateValueAndValidity();
      });
  }

  onContinue(): void {
    if (!this.form.dirty) {
      this.store
        .pipe(
          first(),
          pluck('determination', 'type'),
          map((type) => this.getUrl(type)),
        )
        .subscribe((url) => this.router.navigate([`../${url}`], { relativeTo: this.route }));
    } else {
      const reason = this.form.value.reason;
      this.store
        .pipe(
          first(),
          switchMap((state) =>
            this.store.postDetermination(
              {
                ...state.determination,
                reason,
              },
              false,
            ),
          ),
          this.pendingRequest.trackRequest(),
          switchMapTo(this.store),
          take(1),
          pluck('determination', 'type'),
          map((type) => this.getUrl(type)),
        )
        .subscribe((url) => this.router.navigate([`../${url}`], { relativeTo: this.route }));
    }
  }

  private getUrl(type: string): string {
    return type === 'GRANTED' ? 'activation-date' : type === 'REJECTED' ? 'official-notice' : 'answers';
  }
}
