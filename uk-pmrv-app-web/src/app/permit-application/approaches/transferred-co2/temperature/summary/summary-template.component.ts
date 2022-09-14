import { Component, Input } from '@angular/core';

import { TemperaturePressure } from 'pmrv-api';

// eslint-disable-next-line @angular-eslint/prefer-on-push-component-change-detection
@Component({
  selector: 'app-temperature-summary-template',
  templateUrl: './summary-template.component.html',
})
export class SummaryTemplateComponent {
  @Input() details: TemperaturePressure;
  @Input() hasBottomBorder = true;
  @Input() cssClass: string;
}
