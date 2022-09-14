import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, switchMap } from 'rxjs';

import { PendingRequestService } from '../../../core/guards/pending-request.service';
import { PendingRequest } from '../../../core/interfaces/pending-request.interface';
import { PERMIT_SURRENDER_TASK_FORM } from '../../core/permit-surrender-task-form.token';
import { PermitSurrenderStore } from '../../store/permit-surrender.store';
import { supportDocumentsFormProvider } from './support-documents-form.provider';

@Component({
  selector: 'app-support-documents',
  template: ` <div class="govuk-grid-row">
    <div class="govuk-grid-column-full">
      <app-wizard-step
        (formSubmit)="onContinue()"
        [formGroup]="form"
        submitText="Continue"
        [hideSubmit]="(store.isEditable$ | async) === false"
      >
        <span class="govuk-caption-l">Surrender your permit</span>
        <app-page-heading>Do you want to provide any supporting documents?</app-page-heading>
        <app-boolean-radio-group controlName="documentsExist"> </app-boolean-radio-group>
      </app-wizard-step>
      <a govukLink routerLink="../..">Return to: Permit surrender task list</a>
    </div>
  </div>`,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [supportDocumentsFormProvider],
})
export class SupportDocumentsComponent implements OnInit, PendingRequest {
  private documentsExist: boolean;

  constructor(
    @Inject(PERMIT_SURRENDER_TASK_FORM) readonly form: FormGroup,
    readonly store: PermitSurrenderStore,
    readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    this.store
      .pipe(
        first(),
        map((state) => state.permitSurrender.documentsExist),
      )
      .subscribe((documentsExist) => (this.documentsExist = documentsExist));
  }

  onContinue(): void {
    const documentsExist = this.form.value.documentsExist;
    const supportingDocumentsChanged = this.documentsExist !== documentsExist;

    if (!supportingDocumentsChanged) {
      this.router.navigate(['../upload-documents'], { relativeTo: this.route, state: { changing: documentsExist } });
    } else {
      combineLatest([this.route.data, this.store])
        .pipe(
          first(),
          switchMap(([data, state]) =>
            this.store.postApplyPermitSurrender({
              ...state,
              permitSurrender: {
                ...state.permitSurrender,
                documentsExist,
                documents: documentsExist ? state.permitSurrender?.documents : null,
              },
              sectionsCompleted: {
                ...state.sectionsCompleted,
                [data.statusKey]: false,
              },
            }),
          ),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.router.navigate(['../upload-documents'], { relativeTo: this.route }));
    }
  }
}
