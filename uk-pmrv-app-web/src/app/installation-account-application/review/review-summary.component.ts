import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { legalEntityTypeMap } from '../../shared/interfaces/legal-entity';

@Component({
  selector: 'app-review-summary',
  templateUrl: './review-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReviewSummaryComponent {
  @Input() item: any;
  @Input() taskId: number;

  legalEntityTypeMap = legalEntityTypeMap;
}
