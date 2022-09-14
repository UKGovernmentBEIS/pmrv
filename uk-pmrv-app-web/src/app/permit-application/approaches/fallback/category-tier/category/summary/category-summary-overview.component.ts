import { Component, Input } from '@angular/core';

import { FallbackSourceStreamCategory } from 'pmrv-api';

// eslint-disable-next-line @angular-eslint/prefer-on-push-component-change-detection
@Component({
  selector: 'app-category-summary-overview',
  templateUrl: './category-summary-overview.component.html',
  styles: [
    `
      .pre-line {
        white-space: pre-line;
      }
    `,
  ],
})
export class CategorySummaryOverviewComponent {
  @Input() sourceStreamCategory: FallbackSourceStreamCategory;
  @Input() cssClass: string;
}
