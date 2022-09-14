import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { ProcedureOptionalForm } from 'pmrv-api';

import { TaskKey } from '../../../../shared/types/permit-task.type';

@Component({
  selector: 'app-measurement-optional-summary-template',
  templateUrl: './optional-summary-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OptionalSummaryTemplateComponent {
  @Input() procedureOptionalForm: ProcedureOptionalForm;
  @Input() taskKey: TaskKey;
  @Input() hasBottomBorder = true;
}
