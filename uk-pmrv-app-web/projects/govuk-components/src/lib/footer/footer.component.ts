import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'govuk-footer',
  templateUrl: './footer.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FooterComponent {}
