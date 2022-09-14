import { Component, Input } from '@angular/core';

import { ProcedureForm } from 'pmrv-api';

// eslint-disable-next-line @angular-eslint/prefer-on-push-component-change-detection
@Component({
  selector: 'app-procedure-form-summary',
  templateUrl: './procedure-form-summary.component.html',
})
export class ProcedureFormSummaryComponent {
  @Input() details: ProcedureForm;
  @Input() cssClass: string;
  @Input() hasBottomBorder = true;
}
