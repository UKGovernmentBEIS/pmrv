import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { map, tap } from 'rxjs';

import { PERMIT_TASK_FORM } from '../../../../../shared/permit-task-form.token';
import { PermitApplicationStore } from '../../../../../store/permit-application.store';
import { measurementDevicesFormProvider } from '../measurement-devices-form.provider';

@Component({
  selector: 'app-summary',
  templateUrl: './summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [measurementDevicesFormProvider],
})
export class SummaryComponent {
  index$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('index'))));
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;

  isSummaryDisplayed$ = this.store.pipe(
    tap(() => this.form.updateValueAndValidity()),
    map(() => this.form.errors?.validMeasurementDevicesOrMethods),
  );

  constructor(
    @Inject(PERMIT_TASK_FORM) readonly form: FormGroup,
    readonly store: PermitApplicationStore,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
  ) {}
}
