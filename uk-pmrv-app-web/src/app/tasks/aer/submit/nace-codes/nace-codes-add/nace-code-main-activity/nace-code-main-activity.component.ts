import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { originalOrder } from '@shared/keyvalue-order';
import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { mainActivities } from '@tasks/aer/submit/nace-codes/nace-code-types';
import { naceMainActivityFormProvider } from '@tasks/aer/submit/nace-codes/nace-codes-add/nace-code-main-activity/nace-code-main-activity-form.provider';

@Component({
  selector: 'app-nace-code-main-activity',
  templateUrl: './nace-code-main-activity.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [naceMainActivityFormProvider],
})
export class NaceCodeMainActivityComponent {
  readonly originalOrder = originalOrder;
  readonly mainActivities = mainActivities;

  constructor(
    @Inject(AER_TASK_FORM) readonly form: FormGroup,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
  ) {}

  onSubmit(): void {
    const mainActivity = this.form.value.mainActivity;
    this.router.navigate(['sub-category'], { relativeTo: this.route, state: { mainActivity: mainActivity } });
  }
}
