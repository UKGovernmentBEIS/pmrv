import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { BackLinkService } from '@shared/back-link/back-link.service';

@Component({
  selector: 'app-permit-variation-tasklist',
  template: `<app-base-task-container-component
      header="Make a change to your permit"
      [customContentTemplate]="customContentTemplate"
      expectedTaskType="PERMIT_VARIATION_OPERATOR_APPLICATION_SUBMIT"
    ></app-base-task-container-component>
    <ng-template #customContentTemplate>
      <app-permit-task-list [isHeadingVisible]="false"></app-permit-task-list>
    </ng-template>`,
  providers: [DestroySubject, BackLinkService],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PermitVariationTasklistComponent {
  constructor(private readonly router: Router, private readonly route: ActivatedRoute) {}

  onCancelTask() {
    this.router.navigate(['../cancel'], { relativeTo: this.route });
  }
}
