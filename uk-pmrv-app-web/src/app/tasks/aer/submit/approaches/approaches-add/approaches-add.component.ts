import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, switchMap } from 'rxjs';

import { AerService } from '@tasks/aer/core/aer.service';
import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';

import { approachesAddFormProvider } from './approaches-add-form.provider';

@Component({
  selector: 'app-approaches-add',
  template: `
    <app-aer-task>
      <app-approaches-add-template
        (formSubmit)="onSubmit()"
        [monitoringApproaches]="monitoringApproaches$ | async"
        [form]="form"
      ></app-approaches-add-template>
    </app-aer-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [approachesAddFormProvider],
})
export class ApproachesAddComponent {
  monitoringApproaches$ = this.aerService.getTask('monitoringApproachTypes').pipe(
    // this transformation from array to index signature is done only to reuse the template
    map((approaches) => approaches.reduce((res, key) => ({ ...res, [key]: key }), {} as { [key: string]: any })),
  );

  constructor(
    @Inject(AER_TASK_FORM) readonly form: FormGroup,
    private readonly aerService: AerService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
  ) {}

  onSubmit(): void {
    this.aerService
      .getTask('monitoringApproachTypes')
      .pipe(
        first(),
        switchMap((monitoringApproachTypes) =>
          this.aerService.postTaskSave(
            { monitoringApproachTypes: (monitoringApproachTypes ?? []).concat(this.form.value.monitoringApproaches) },
            undefined,
            false,
            'monitoringApproachTypes',
          ),
        ),
      )
      .subscribe(() => this.router.navigate(['..'], { relativeTo: this.route }));
  }
}
