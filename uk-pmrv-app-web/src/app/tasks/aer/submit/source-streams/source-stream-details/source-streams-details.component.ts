import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, switchMap } from 'rxjs';

import { AerService } from '@tasks/aer/core/aer.service';
import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';

import { sourceStreamFormProvider } from './source-stream-form.provider';

@Component({
  selector: 'app-source-streams-details',
  template: `
    <app-aer-task>
      <app-source-streams-details-template (formSubmit)="onSubmit()" [form]="form" [isEditing]="isEditing$ | async">
      </app-source-streams-details-template>
    </app-aer-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [sourceStreamFormProvider],
})
export class SourceStreamDetailsComponent {
  isEditing$ = this.aerService
    .getTask('sourceStreams')
    .pipe(map((sourceStreams) => sourceStreams?.some((item) => item.id === this.form.get('id').value)));

  constructor(
    @Inject(AER_TASK_FORM) readonly form: FormGroup,
    private readonly aerService: AerService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
  ) {}

  onSubmit(): void {
    this.aerService
      .getTask('sourceStreams')
      .pipe(
        first(),
        switchMap((sourceStreams) =>
          this.aerService.postTaskSave(
            {
              sourceStreams: sourceStreams?.some((item) => item.id === this.form.value.id)
                ? sourceStreams.map((item) => (item.id === this.form.value.id ? this.form.value : item))
                : [...(sourceStreams ?? []), this.form.value],
            },
            undefined,
            false,
            'sourceStreams',
          ),
        ),
      )
      .subscribe(() => this.router.navigate(['..'], { relativeTo: this.route }));
  }
}
