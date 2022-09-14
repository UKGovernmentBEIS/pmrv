import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, switchMap } from 'rxjs';

import { PendingRequestService } from '../../../../core/guards/pending-request.service';
import { PendingRequest } from '../../../../core/interfaces/pending-request.interface';
import { BackLinkService } from '../../../../shared/back-link/back-link.service';
import { PERMIT_TASK_FORM } from '../../../shared/permit-task-form.token';
import { PermitApplicationStore } from '../../../store/permit-application.store';
import { officialNoticeFormProvider } from './official-notice-form.provider';

@Component({
  selector: 'app-official-notice',
  template: `
    <app-permit-task>
      <app-wizard-step
        (formSubmit)="onContinue()"
        [formGroup]="form"
        submitText="Continue"
        [hideSubmit]="(store.isEditable$ | async) === false"
      >
        <span class="govuk-caption-l">Reject</span>

        <app-page-heading>Provide a text to be included in the official refusal letter</app-page-heading>
        <div class="govuk-hint">The operator will see this in the official notice letter</div>

        <div formControlName="officialNotice" govuk-textarea [maxLength]="10000"></div>
      </app-wizard-step>

      <a govukLink routerLink="../..">Return to: Permit determination</a>
    </app-permit-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [officialNoticeFormProvider, BackLinkService],
})
export class OfficialNoticeComponent implements PendingRequest, OnInit {
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
      this.router.navigate(['../answers'], { relativeTo: this.route });
    } else {
      const officialNotice = this.form.value.officialNotice;
      this.store
        .pipe(
          first(),
          switchMap((state) =>
            this.store.postDetermination(
              {
                ...state.determination,
                officialNotice,
              },
              false,
            ),
          ),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.router.navigate(['../answers'], { relativeTo: this.route }));
    }
  }
}
