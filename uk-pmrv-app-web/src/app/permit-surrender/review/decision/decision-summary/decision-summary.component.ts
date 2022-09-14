import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';

import { Observable } from 'rxjs';

import { PermitSurrenderReviewDecision } from 'pmrv-api';

@Component({
  selector: 'app-permit-surrender-decision-summary',
  templateUrl: './decision-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DecisionSummaryComponent {
  @Input() reviewDecision$: Observable<PermitSurrenderReviewDecision>;
  @Input() isEditable: boolean;
  @Output() readonly editClicked = new EventEmitter<Event>();
}
