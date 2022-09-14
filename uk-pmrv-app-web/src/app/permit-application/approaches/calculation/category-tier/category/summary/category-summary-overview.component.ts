import { Component, Input } from '@angular/core';

import { CalculationSourceStreamCategory } from 'pmrv-api';

// eslint-disable-next-line @angular-eslint/prefer-on-push-component-change-detection
@Component({
  selector: 'app-category-summary-overview',
  templateUrl: './category-summary-overview.component.html',
})
export class CategorySummaryOverviewComponent {
  @Input() sourceStreamCategory: CalculationSourceStreamCategory;
  @Input() cssClass: string;
}
