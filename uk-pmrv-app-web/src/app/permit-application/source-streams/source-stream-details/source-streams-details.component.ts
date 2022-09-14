import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, switchMap } from 'rxjs';

import { descriptionOptions, typeOptions } from '@shared/components/source-streams/source-stream-options';

import { SourceStream } from 'pmrv-api';

import { PERMIT_TASK_FORM } from '../../shared/permit-task-form.token';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { sourceStreamFormProvider } from './source-stream-form.provider';

@Component({
  selector: 'app-source-streams-details',
  template: `
    <app-permit-task>
      <app-source-streams-details-template (formSubmit)="onSubmit()" [form]="form" [isEditing]="isEditing$ | async">
      </app-source-streams-details-template>
    </app-permit-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [sourceStreamFormProvider],
})
export class SourceStreamDetailsComponent {
  isEditing$ = this.store
    .getTask('sourceStreams')
    .pipe(map((sourceStreams) => sourceStreams.some((item) => item.id === this.form.get('id').value)));
  descriptionOptions = descriptionOptions;
  typeOptions = typeOptions;

  constructor(
    @Inject(PERMIT_TASK_FORM) readonly form: FormGroup,
    private readonly store: PermitApplicationStore,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
  ) {}

  onSubmit(): void {
    this.store
      .findTask<SourceStream[]>('sourceStreams')
      .pipe(
        first(),
        switchMap((sourceStreams) =>
          this.store.postTask(
            'sourceStreams',
            sourceStreams.some((item) => item.id === this.form.value.id)
              ? sourceStreams.map((item) => (item.id === this.form.value.id ? this.form.value : item))
              : [...sourceStreams, this.form.value],
            false,
          ),
        ),
      )
      .subscribe(() => this.router.navigate(['..'], { relativeTo: this.route }));
  }
}
