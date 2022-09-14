import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-approaches-help',
  template: `
    <app-aer-task>
      <app-approaches-help-template></app-approaches-help-template>
    </app-aer-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ApproachesHelpComponent {}
