import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-approaches-help',
  template: `
    <app-permit-task>
      <app-approaches-help-template></app-approaches-help-template>
    </app-permit-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ApproachesHelpComponent {}
