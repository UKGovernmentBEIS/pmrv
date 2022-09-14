import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'govuk-footer-meta-info',
  templateUrl: './meta-info.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MetaInfoComponent {}
