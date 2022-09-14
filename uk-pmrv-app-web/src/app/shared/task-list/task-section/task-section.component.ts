import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { TaskItem } from '../task-list.interface';

@Component({
  selector: 'li[app-task-section]',
  template: `
    <h2 *ngIf="title" class="app-task-list__section">{{ title }}</h2>
    <ul *ngIf="tasks" app-task-item-list [taskItems]="tasks"></ul>
    <ng-content></ng-content>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TaskSectionComponent {
  @Input() title: string;
  @Input() tasks: TaskItem<any>[];
}
