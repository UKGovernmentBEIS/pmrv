import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';

import { requestTaskAllowedActions } from '@shared/components/related-actions/request-task-allowed-actions.map';

import { RequestTaskActionProcessDTO } from 'pmrv-api';

@Component({
  selector: 'app-related-actions',
  template: `
    <aside class="app-related-items" role="complementary">
      <h2 class="govuk-heading-m" id="subsection-title">Related actions</h2>
      <nav role="navigation" aria-labelledby="subsection-title">
        <ul class="govuk-list govuk-!-font-size-16">
          <li *ngFor="let action of allActions">
            <a [routerLink]="action.link" govukLink>{{ action.text }}</a>
          </li>
        </ul>
      </nav>
    </aside>
  `,
  styleUrls: ['./related-actions.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RelatedActionsComponent implements OnInit {
  @Input() isAssignable: boolean;
  @Input() taskId: number;
  @Input() allowedActions: Array<RequestTaskActionProcessDTO['requestTaskActionType']>;
  allActions: { text: string; link: any[] }[] = [];

  ngOnInit(): void {
    this.allActions = requestTaskAllowedActions(this.allowedActions, this.taskId);
    if (this.isAssignable) {
      this.allActions.unshift({ text: 'Reassign task', link: ['/tasks', this.taskId, 'change-assignee'] });
    }
  }
}