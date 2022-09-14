import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, switchMap } from 'rxjs';

import { MeasurementDeviceOrMethod } from 'pmrv-api';

import { PERMIT_TASK_FORM } from '../../shared/permit-task-form.token';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { measurementDevicesAddFormFactory, typeOptions } from './measurement-device-details-form.provider';

@Component({
  selector: 'app-measurement-device-details',
  templateUrl: './measurement-device-details.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [measurementDevicesAddFormFactory],
})
export class MeasurementDeviceDetailsComponent {
  isEditing$ = this.store
    .getTask('measurementDevicesOrMethods')
    .pipe(
      map((measurementDevicesOrMethods) =>
        measurementDevicesOrMethods.some((item) => item.id === this.form.get('id').value),
      ),
    );
  typeOptions = typeOptions;

  constructor(
    @Inject(PERMIT_TASK_FORM) readonly form: FormGroup,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly store: PermitApplicationStore,
  ) {}

  get type(): string {
    return this.form.get('type').value;
  }

  onSubmit(): void {
    this.store
      .findTask<MeasurementDeviceOrMethod[]>('measurementDevicesOrMethods')
      .pipe(
        first(),
        switchMap((devices) =>
          this.store.postTask(
            'measurementDevicesOrMethods',
            devices.some((item) => item.id === this.form.value.id)
              ? devices.map((item) => (item.id === this.form.value.id ? this.form.value : item))
              : [...devices, this.form.value],
            false,
          ),
        ),
      )
      .subscribe(() => this.router.navigate(['..'], { relativeTo: this.route }));
  }
}
