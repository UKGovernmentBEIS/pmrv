import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, switchMap } from 'rxjs';

import { DestroySubject } from '../../../core/services/destroy-subject.service';
import { PERMIT_TASK_FORM } from '../../shared/permit-task-form.token';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { emissionPointFormProvider } from './emission-point-details-form.provider';

@Component({
  selector: 'app-emission-point-details',
  template: `
    <app-permit-task>
      <app-emission-point-details-template (formSubmit)="onSubmit()" [form]="form" [isEditing]="isEditing$ | async">
      </app-emission-point-details-template>
    </app-permit-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject, emissionPointFormProvider],
})
export class EmissionPointDetailsComponent {
  isEditing$ = this.store
    .getTask('emissionPoints')
    .pipe(map((emissionPoints) => emissionPoints.some((item) => item.id === this.form.get('id').value)));

  constructor(
    @Inject(PERMIT_TASK_FORM) readonly form: FormGroup,
    private readonly store: PermitApplicationStore,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
  ) {}

  onSubmit(): void {
    this.store
      .pipe(
        first(),
        switchMap((state) =>
          this.store.postTask(
            'emissionPoints',
            state.permit.emissionPoints.some((point) => point.id === this.form.value.id)
              ? state.permit.emissionPoints.map((point) => (point.id === this.form.value.id ? this.form.value : point))
              : [...state.permit.emissionPoints, this.form.value],
            false,
          ),
        ),
      )
      .subscribe(() => this.router.navigate(['..'], { relativeTo: this.route }));
  }
}
