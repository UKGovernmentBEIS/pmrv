import { ChangeDetectionStrategy, Component } from '@angular/core';

import { first, map } from 'rxjs';

import { CommonTasksStore } from '../../store/common-tasks.store';

@Component({
  selector: 'app-peer-review-wait',
  templateUrl: './peer-review-wait.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PeerReviewWaitComponent {
  readonly daysRemaining$ = this.store.pipe(
    first(),
    map((state) => state.requestTaskItem.requestTask.daysRemaining),
  );

  constructor(readonly store: CommonTasksStore) {}
}
