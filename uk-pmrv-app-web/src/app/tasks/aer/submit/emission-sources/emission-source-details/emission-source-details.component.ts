import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, switchMap } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { AerService } from '@tasks/aer/core/aer.service';
import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';

import { emissionSourcesAddFormFactory } from './emission-source-details-form.provider';

@Component({
  selector: 'app-emission-source-details',
  template: `
    <app-aer-task>
      <app-emission-source-details-template (formSubmit)="onSubmit()" [form]="form" [isEditing]="isEditing$ | async">
      </app-emission-source-details-template>
    </app-aer-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [emissionSourcesAddFormFactory, DestroySubject],
})
export class EmissionSourceDetailsComponent {
  isEditing$ = this.aerService
    .getTask('emissionSources')
    .pipe(map((emissionSources) => emissionSources?.some((item) => item.id === this.form.get('id').value)));

  constructor(
    @Inject(AER_TASK_FORM) readonly form: FormGroup,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly aerService: AerService,
  ) {}

  onSubmit(): void {
    this.aerService
      .getTask('emissionSources')
      .pipe(
        first(),
        switchMap((emissionSources) => {
          return this.aerService.postTaskSave(
            {
              emissionSources: emissionSources?.some((item) => item.id === this.form.value.id)
                ? emissionSources.map((item) => (item.id === this.form.value.id ? this.form.value : item))
                : [...(emissionSources ?? []), this.form.value],
            },
            undefined,
            false,
            'emissionSources',
          );
        }),
      )
      .subscribe(() => this.router.navigate(['..'], { relativeTo: this.route }));
  }
}
