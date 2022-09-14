import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, switchMap } from 'rxjs';

import { PERMIT_TASK_FORM } from '../../shared/permit-task-form.token';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { approachesAddFormProvider } from './approaches-add-form.provider';

@Component({
  selector: 'app-approaches-add',
  template: `
    <app-permit-task>
      <app-approaches-add-template
        (formSubmit)="onSubmit()"
        [monitoringApproaches]="monitoringApproaches$ | async"
        [form]="form"
      ></app-approaches-add-template>
    </app-permit-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [approachesAddFormProvider],
})
export class ApproachesAddComponent {
  monitoringApproaches$ = this.store
    .getTask('monitoringApproaches')
    .pipe(map((monitoringApproaches) => monitoringApproaches ?? {}));

  constructor(
    @Inject(PERMIT_TASK_FORM) readonly form: FormGroup,
    private readonly store: PermitApplicationStore,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
  ) {}

  onSubmit(): void {
    const monitoringApproachesAdded = this.form.value.monitoringApproaches.reduce(
      (res, approach) => ({ ...res, [approach]: { type: approach } }),
      {},
    );

    this.store
      .getTask('monitoringApproaches')
      .pipe(
        first(),
        switchMap((monitoringApproaches) =>
          this.store.postTask('monitoringApproaches', { ...monitoringApproaches, ...monitoringApproachesAdded }, false),
        ),
      )
      .subscribe(() => this.router.navigate(['..'], { relativeTo: this.route }));
  }
}
