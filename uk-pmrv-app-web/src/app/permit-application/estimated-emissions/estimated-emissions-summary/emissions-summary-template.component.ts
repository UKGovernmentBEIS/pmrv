import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { PermitApplicationStore } from '../../store/permit-application.store';

@Component({
  selector: 'app-emissions-summary-template',
  templateUrl: './emissions-summary-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EmissionsSummaryTemplateComponent {
  @Input() hasBorders = true;
  @Input() isPreview: boolean;

  constructor(readonly store: PermitApplicationStore) {}
}
