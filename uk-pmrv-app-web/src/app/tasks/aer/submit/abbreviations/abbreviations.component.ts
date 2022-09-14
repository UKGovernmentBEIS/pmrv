import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { AerService } from '@tasks/aer/core/aer.service';
import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { abbreviationsFormProvider } from '@tasks/aer/submit/abbreviations/abbreviations-form.provider';

@Component({
  selector: 'app-abbreviations',
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-full">
        <app-abbreviations-template
          (formSubmit)="onSubmit()"
          [form]="form"
          [isEditable]="aerService.isEditable$ | async"
          caption="Additional information"
        ></app-abbreviations-template>
        <app-return-link></app-return-link>
      </div>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [abbreviationsFormProvider],
})
export class AbbreviationsComponent implements PendingRequest {
  constructor(
    @Inject(AER_TASK_FORM) readonly form: FormGroup,
    readonly aerService: AerService,
    readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit(): void {
    if (!this.form.dirty) {
      this.router.navigate(['summary'], { relativeTo: this.route });
    } else {
      this.aerService
        .postTaskSave(
          {
            abbreviations: {
              ...this.form.value,
            },
          },
          undefined,
          true,
          'abbreviations',
        )
        .pipe(this.pendingRequest.trackRequest())
        .subscribe(() => this.router.navigate(['summary'], { relativeTo: this.route, state: { notification: true } }));
    }
  }
}
