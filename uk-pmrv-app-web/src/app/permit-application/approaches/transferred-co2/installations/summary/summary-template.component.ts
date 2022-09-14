import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { PermitApplicationStore } from '../../../../store/permit-application.store';

@Component({
  selector: 'app-installations-summary-template',
  templateUrl: './summary-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryTemplateComponent {
  @Input() hasBottomBorder = true;
  @Input() preview = true;

  constructor(readonly store: PermitApplicationStore) {}
}
