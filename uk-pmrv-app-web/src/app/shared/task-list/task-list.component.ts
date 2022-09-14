import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';

import { TaskSection } from './task-list.interface';

@Component({
  selector: 'app-task-list',
  templateUrl: './task-list.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TaskListComponent {
  @Input() sections: TaskSection<any>[];
  @Input() heading: string;
  @Input() isSubmitDisabled: boolean;
  @Input() hideSubmit: boolean;
  @Input() hideCancel: boolean;
  @Input() submitLabel = 'Submit';
  @Input() submitButtonLabel = 'Check & submit';
  @Input() cancelLinkLabel = 'Cancel';
  @Output() readonly submitApplication = new EventEmitter();
  @Output() readonly cancelApplication = new EventEmitter();
}
