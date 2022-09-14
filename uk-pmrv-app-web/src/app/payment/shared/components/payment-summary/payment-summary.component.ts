import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { PaymentDetails } from '../../../core/payment.map';

@Component({
  selector: 'app-payment-summary',
  templateUrl: './payment-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PaymentSummaryComponent {
  @Input() details: PaymentDetails;
  @Input() isActionView?: boolean = false;
}
