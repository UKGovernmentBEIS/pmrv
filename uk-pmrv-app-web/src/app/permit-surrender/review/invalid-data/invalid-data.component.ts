import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';

import { map, tap } from 'rxjs';

import { PERMIT_SURRENDER_TASK_FORM } from '../../core/permit-surrender-task-form.token';
import { PermitSurrenderStore } from '../../store/permit-surrender.store';
import { noticeDateFormProvider } from '../determination/grant/notice-date/notice-date-form.provider';

@Component({
  selector: 'app-invalid-data',
  template: `
    <govuk-error-summary *ngIf="invalidNoticeDate$ | async" [form]="form"></govuk-error-summary>
    <a govukLink routerLink=".."> Return to surrender permit determination </a>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [noticeDateFormProvider],
})
export class InvalidDataComponent {
  invalidNoticeDate$ = this.store.pipe(
    tap(() => {
      this.form.controls.noticeDate.enable();
      this.form.controls.noticeDate.updateValueAndValidity();
    }),
    map(() => this.form.controls.noticeDate.errors?.invalidNoticeDate),
  );

  constructor(
    @Inject(PERMIT_SURRENDER_TASK_FORM) readonly form: FormGroup,
    private readonly store: PermitSurrenderStore,
  ) {}
}
