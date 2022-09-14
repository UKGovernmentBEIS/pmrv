import { Component, Input } from '@angular/core';

import { N2OSourceStreamCategory } from 'pmrv-api';

// eslint-disable-next-line @angular-eslint/prefer-on-push-component-change-detection
@Component({
  selector: 'app-category-summary-overview',
  templateUrl: './category-summary-overview.component.html',
})
export class CategorySummaryOverviewComponent {
  @Input() sourceStreamCategory: N2OSourceStreamCategory;
  @Input() cssClass: string;
}
