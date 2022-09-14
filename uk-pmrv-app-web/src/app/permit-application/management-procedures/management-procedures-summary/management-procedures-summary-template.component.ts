import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { PermitApplicationStore } from '../../store/permit-application.store';
import { ManagementProceduresDefinitionData } from '../management-procedures.interface';

@Component({
  selector: 'app-management-procedures-summary-template',
  templateUrl: './management-procedures-summary-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ManagementProceduresSummaryTemplateComponent {
  @Input() hasBottomBorder = true;
  @Input() taskKey: ManagementProceduresDefinitionData['permitTask'];

  constructor(readonly store: PermitApplicationStore) {}
}
