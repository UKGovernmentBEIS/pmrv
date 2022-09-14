import { Component, Input } from '@angular/core';

import { MeasSourceStreamCategory } from 'pmrv-api';

// eslint-disable-next-line @angular-eslint/prefer-on-push-component-change-detection
@Component({
  selector: 'app-category-summary-overview',
  templateUrl: './category-summary-overview.component.html',
})
export class CategorySummaryOverviewComponent {
  @Input() sourceStreamCategory: MeasSourceStreamCategory;
  @Input() cssClass: string;
}
