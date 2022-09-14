import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-source-stream-help',
  template: `
    <app-aer-task>
      <app-source-stream-help-template></app-source-stream-help-template>
    </app-aer-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SourceStreamHelpComponent {}
