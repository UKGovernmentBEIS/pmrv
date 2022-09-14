import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { combineLatest, takeUntil } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { BreadcrumbItem } from '@shared/breadcrumbs/breadcrumb.interface';
import { BreadcrumbService } from '@shared/breadcrumbs/breadcrumb.service';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

@Component({
  selector: 'app-aer-task',
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-full">
        <govuk-notification-banner *ngIf="notification" type="success">
          <h1 class="govuk-notification-banner__heading">Details updated</h1>
        </govuk-notification-banner>
        <ng-content></ng-content>
      </div>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [BreadcrumbService, DestroySubject],
})
export class AerTaskComponent implements OnInit {
  @Input() notification: any;
  @Input() breadcrumb: BreadcrumbItem[] | true;
  @Input() reviewGroupTitle: any;
  @Input() reviewGroupUrl: any;

  constructor(
    private readonly route: ActivatedRoute,
    private readonly destroy$: DestroySubject,
    private readonly breadcrumbService: BreadcrumbService,
    private readonly store: CommonTasksStore,
  ) {}

  ngOnInit(): void {
    if (this.breadcrumb) {
      combineLatest([this.route.paramMap, this.store.requestTaskType$])
        .pipe(takeUntil(this.destroy$))
        .subscribe(([paramMap, type]) => {
          let firstBreadcrumb;
          let reviewGroupBreadcrumb = [];
          switch (type) {
            case 'AER_APPLICATION_SUBMIT':
              firstBreadcrumb = {
                link: ['/tasks', paramMap.get('taskId'), 'aer', 'submit'],
                text: 'Emissions report',
              };
              break;
            case 'AER_APPLICATION_REVIEW':
              firstBreadcrumb = {
                link: ['/tasks', paramMap.get('taskId'), 'aer', 'review'],
                text: 'Emissions report',
              };
              reviewGroupBreadcrumb = [
                {
                  text: this.reviewGroupTitle,
                  link: [...firstBreadcrumb.link, this.reviewGroupUrl],
                },
              ];
              break;
          }

          this.breadcrumbService.show([
            firstBreadcrumb,
            ...(Array.isArray(this.breadcrumb)
              ? this.breadcrumb.reduce(
                  (result, item, index) => [
                    ...result,
                    {
                      text: item.text,
                      link: [
                        ...(index === 1 ? result[0].link.slice(0, 2) : result[index - 1]?.link ?? firstBreadcrumb.link),
                        ...item.link,
                      ],
                    },
                  ],
                  [] as any,
                )
              : reviewGroupBreadcrumb),
          ]);
        });
    }
  }
}
