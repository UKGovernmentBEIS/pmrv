import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { RequestActionInfoDTO } from 'pmrv-api';

@Component({
  selector: 'app-timeline-item',
  template: `
    <h3 class="govuk-heading-s govuk-!-margin-bottom-1">{{ action | itemActionHeader }}</h3>
    <p class="govuk-body govuk-!-margin-bottom-1">{{ action.creationDate | govukDate: 'datetime' }}</p>
    <span *ngIf="link"><a [routerLink]="link" [state]="state" govukLink>View details</a></span>
    <hr class="govuk-!-margin-top-6" />
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TimelineItemComponent {
  @Input() action: RequestActionInfoDTO;
  @Input() link: any[];
  @Input() state: any;
}
