import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { filter, first, map, switchMap } from 'rxjs';

import { AerService } from '@tasks/aer/core/aer.service';
import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { emissionPointDetailsFormProvider } from '@tasks/aer/submit/emission-points/emission-point-details/emission-point-details-form.provider';

@Component({
  selector: 'app-emission-point-details',
  template: `
    <app-aer-task>
      <app-emission-point-details-template (formSubmit)="onSubmit()" [form]="form" [isEditing]="isEditing$ | async">
      </app-emission-point-details-template>
    </app-aer-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [emissionPointDetailsFormProvider],
})
export class EmissionPointDetailsComponent {
  emissionPointId = this.form.get('id').value;
  isEditing$ = this.aerService
    .getTask('emissionPoints')
    .pipe(
      filter((emissionPoints) => !!emissionPoints),
      map((emissionPoints) => emissionPoints.some((ep) => ep.id === this.emissionPointId)),
    );

  constructor(
    @Inject(AER_TASK_FORM) readonly form: FormGroup,
    private readonly aerService: AerService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
  ) {}

  onSubmit(): void {
    this.aerService
      .getTask('emissionPoints')
      .pipe(
        first(),
        switchMap((emissionPoints) =>
          this.aerService.postTaskSave(
            {
              emissionPoints: emissionPoints?.some((ep) => ep.id === this.emissionPointId)
                ? emissionPoints.map((ep) => (ep.id === this.emissionPointId ? this.form.value : ep))
                : [...(emissionPoints ?? []), this.form.value],
            },
            undefined,
            false,
            'emissionPoints',
          ),
        ),
      )
      .subscribe(() => this.router.navigate(['..'], { relativeTo: this.route }));
  }
}
