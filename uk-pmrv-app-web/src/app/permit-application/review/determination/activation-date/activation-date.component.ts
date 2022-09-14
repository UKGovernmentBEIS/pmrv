import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, switchMap, switchMapTo, take } from 'rxjs';

import { PendingRequestService } from '../../../../core/guards/pending-request.service';
import { PendingRequest } from '../../../../core/interfaces/pending-request.interface';
import { BackLinkService } from '../../../../shared/back-link/back-link.service';
import { PERMIT_TASK_FORM } from '../../../shared/permit-task-form.token';
import { PermitApplicationStore } from '../../../store/permit-application.store';
import { activationDateFormProvider } from './activation-date-form.provider';

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
        <span class="govuk-caption-l">Grant</span>

        <app-page-heading>Set a date for the permit to become active</app-page-heading>
        <div class="govuk-hint">For example 27.3.2022</div>

        <div formControlName="activationDate" govuk-date-input></div>
      </app-wizard-step>

      <a govukLink routerLink="../..">Return to: Permit determination</a>
    </app-permit-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [activationDateFormProvider, BackLinkService],
})
export class ActivationDateComponent implements PendingRequest, OnInit {
  constructor(
    @Inject(PERMIT_TASK_FORM) readonly form: FormGroup,
    readonly store: PermitApplicationStore,
    readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly backLinkService: BackLinkService,
  ) {}

  ngOnInit(): void {
    this.backLinkService.show();
  }

  onContinue(): void {
    if (!this.form.dirty) {
      this.store
        .pipe(
          first(),
          map((store) => this.getUrl(store)),
        )
        .subscribe((url) => this.router.navigate([`../${url}`], { relativeTo: this.route }));
    } else {
      const activationDate = this.form.value.activationDate;
      this.store
        .pipe(
          first(),
          switchMap((state) =>
            this.store.postDetermination(
              {
                ...state.determination,
                activationDate,
              },
              false,
            ),
          ),
          this.pendingRequest.trackRequest(),
          switchMapTo(this.store),
          take(1),
          map((store) => this.getUrl(store)),
        )
        .subscribe((url) => this.router.navigate([`../${url}`], { relativeTo: this.route }));
    }
  }
  private getUrl(store): string {
    if (store.determination.type === 'GRANTED' && store.permitType === 'HSE') {
      return 'emissions';
    } else {
      return 'answers';
    }
  }
}
