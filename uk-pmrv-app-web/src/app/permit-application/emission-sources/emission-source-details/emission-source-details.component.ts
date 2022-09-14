import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, switchMap } from 'rxjs';

import { EmissionSource } from 'pmrv-api';

import { DestroySubject } from '../../../core/services/destroy-subject.service';
import { PERMIT_TASK_FORM } from '../../shared/permit-task-form.token';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { emisionSourcesAddFormFactory } from './emission-source-details-form.provider';

@Component({
  selector: 'app-emission-source-details',
  template: `
    <app-permit-task>
      <app-emission-source-details-template
        (formSubmit)="onSubmit()"
        [form]="form"
        [isEditing]="isEditing$ | async"
        [caption]="'Fuels and equipment inventory'"
      >
      </app-emission-source-details-template>
    </app-permit-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [emisionSourcesAddFormFactory, DestroySubject],
})
export class EmissionSourceDetailsComponent {
  isEditing$ = this.store
    .getTask('emissionSources')
    .pipe(map((emissionSources) => emissionSources.some((item) => item.id === this.form.get('id').value)));

  constructor(
    @Inject(PERMIT_TASK_FORM) readonly form: FormGroup,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly store: PermitApplicationStore,
  ) {}

  onSubmit(): void {
    this.store
      .findTask<EmissionSource[]>('emissionSources')
      .pipe(
        first(),
        switchMap((sources) =>
          this.store.postTask(
            'emissionSources',
            sources.some((item) => item.id === this.form.value.id)
              ? sources.map((item) => (item.id === this.form.value.id ? this.form.value : item))
              : [...sources, this.form.value],
            false,
          ),
        ),
      )
      .subscribe(() => this.router.navigate(['..'], { relativeTo: this.route }));
  }
}
