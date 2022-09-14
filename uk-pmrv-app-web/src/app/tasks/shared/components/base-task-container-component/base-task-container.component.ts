import { ChangeDetectionStrategy, Component, Input, OnInit, TemplateRef } from '@angular/core';
import { Router } from '@angular/router';

import { filter, first, Observable, switchMapTo } from 'rxjs';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { ItemDTO, RequestActionInfoDTO, RequestTaskDTO, RequestTaskItemDTO } from 'pmrv-api';

@Component({
  selector: 'app-base-task-container-component[expectedTaskType][header]',
  template: `
    <ng-container *ngIf="dataLoaded">
      <app-task-layout
        [header]="header"
        [daysRemaining]="daysRemaining"
        [requestTaskItem]="requestTaskItem$ | async"
        [relatedTasks]="relatedTasks$ | async"
        [timelineActions]="timelineActions$ | async"
        [customContentTemplate]="customContentTemplate"
        [showSectionBreak]="showSectionBreak"
      >
      </app-task-layout>
    </ng-container>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BaseTaskContainerComponent implements OnInit {
  @Input() header: string;
  @Input() daysRemaining: number;
  @Input() expectedTaskType: RequestTaskDTO['type'];
  @Input() customContentTemplate: TemplateRef<any>;
  @Input() showSectionBreak = true;

  requestTaskItem$: Observable<RequestTaskItemDTO> = this.commonTaskStore.requestTaskItem$;
  relatedTasks$: Observable<ItemDTO[]> = this.commonTaskStore.relatedTasksItems$;
  timelineActions$: Observable<RequestActionInfoDTO[]> = this.commonTaskStore.timeLineActions$;
  dataLoaded = false;

  constructor(private readonly router: Router, private readonly commonTaskStore: CommonTasksStore) {}

  ngOnInit(): void {
    this.commonTaskStore.storeInitialized$
      .pipe(
        filter((storeInitialized) => !!storeInitialized),
        switchMapTo(this.commonTaskStore.requestTaskItem$),
        first(),
      )
      .subscribe((requestTaskItem) => {
        if (!requestTaskItem) {
          this.router.navigate(['/error', '404']).then(() => console.error('No Request Task Item provided.'));
        } else if (requestTaskItem.requestTask.type !== this.expectedTaskType) {
          throw Error('Invalid Request Task Item for given id.');
        } else {
          this.dataLoaded = true;
        }
      });
  }
}
