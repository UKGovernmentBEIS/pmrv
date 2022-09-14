import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';

import { BehaviorSubject, Observable } from 'rxjs';

import { BackLinkService } from '@shared/back-link/back-link.service';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { RequestTaskItemDTO } from 'pmrv-api';

@Component({
  selector: 'app-change-assignee',
  template: `
    <ng-container *ngIf="isTaskAssigned$ | async; else mainView">
      <app-assignment-confirmation [user]="taskAssignedTo$ | async"></app-assignment-confirmation>
    </ng-container>
    <ng-template #mainView>
      <app-assignment [noBorder]="true" [info]="info$ | async" (submitted)="assignedTask($event)"></app-assignment>
    </ng-template>
  `,
  providers: [BackLinkService],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ChangeAssigneeComponent implements OnInit {
  readonly taskAssignedTo$ = new BehaviorSubject<string>(null);
  readonly isTaskAssigned$ = new BehaviorSubject<boolean>(false);
  readonly info$: Observable<RequestTaskItemDTO> = this.commonTasksStore.requestTaskItem$;

  constructor(
    private readonly commonTasksStore: CommonTasksStore,
    private readonly backLinkService: BackLinkService,
  ) {}

  ngOnInit(): void {
    this.backLinkService.show();
  }

  assignedTask(name: string): void {
    this.taskAssignedTo$.next(name);
    this.isTaskAssigned$.next(true);
    this.backLinkService.hide();
  }
}