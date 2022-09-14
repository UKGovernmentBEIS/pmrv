import { Component, Input } from '@angular/core';

import { AppliedStandard } from 'pmrv-api';

// eslint-disable-next-line @angular-eslint/prefer-on-push-component-change-detection
@Component({
  selector: 'app-applied-standard-form-summary',
  templateUrl: './applied-standard-form-summary.component.html',
})
export class AppliedStandardFormSummaryComponent {
  @Input() appliedStandard: AppliedStandard;
  @Input() cssClass: string;
}
