import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { ItemDTO } from 'pmrv-api';

@Component({
  selector: 'app-related-tasks',
  template: `
    <div class="govuk-!-margin-bottom-9">
      <h2 class="govuk-heading-m">{{ heading }}</h2>
      <hr class="govuk-!-margin-bottom-3" *ngIf="!noBorders" />
      <div *ngFor="let item of items">
        <h3 class="govuk-heading-s govuk-!-margin-bottom-1">{{ item.taskType | itemName }}</h3>
        <div class="govuk-grid-row">
          <div class="govuk-grid-column-full">
            <p class="govuk-body" *ngIf="item.daysRemaining !== null && item.daysRemaining !== undefined">
              Days Remaining: {{ item.daysRemaining | daysRemaining }}
            </p>
            <a [routerLink]="item | itemLink" class="govuk-button govuk-button--secondary">View task</a>
          </div>
        </div>
        <hr class="govuk-!-margin-bottom-3" *ngIf="!noBorders" />
      </div>
    </div>
  `,
  styles: [
    `
      .govuk-body {
        float: left;
      }
      .govuk-button {
        margin-bottom: 0;
      }
      .govuk-button--secondary {
        float: right;
      }
    `,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RelatedTasksComponent {
  @Input() items: ItemDTO[];
  @Input() heading = 'Related tasks';
  @Input() noBorders = false;
}
