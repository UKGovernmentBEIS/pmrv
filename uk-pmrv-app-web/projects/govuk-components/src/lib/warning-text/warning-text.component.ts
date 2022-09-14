import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

@Component({
  selector: 'govuk-warning-text',
  templateUrl: './warning-text.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class WarningTextComponent {
  @Input() assistiveText = 'Warning';
}
