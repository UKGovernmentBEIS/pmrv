import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { headingMap } from '../../../core/payment.map';
import { PaymentState } from '../../../store/payment.state';

@Component({
  selector: 'app-return-link',
  template: `
    <a govukLink [routerLink]="returnLink">
      Return to: {{ state.requestTaskItem.requestTask.type | i18nSelect: headingMap }}
    </a>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReturnLinkComponent {
  @Input() state: PaymentState;
  @Input() returnLink: string;
  readonly headingMap = headingMap;
}
