import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { combineLatest, first, pluck, takeUntil, tap } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { BreadcrumbItem } from '@shared/breadcrumbs/breadcrumb.interface';
import { BreadcrumbService } from '@shared/breadcrumbs/breadcrumb.service';

import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';

@Component({
  selector: 'app-permit-task',
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
export class PermitTaskComponent implements OnInit {
  @Input() notification: any;
  @Input() breadcrumb: BreadcrumbItem[] | true;
  @Input() reviewGroupTitle: any;
  @Input() reviewGroupUrl: any;

  relatedContentView: boolean;

  constructor(
    private readonly route: ActivatedRoute,
    private readonly destroy$: DestroySubject,
    private readonly breadcrumbService: BreadcrumbService,
    private readonly store: PermitApplicationStore,
  ) {}

  ngOnInit(): void {
    this.store
      .pipe(
        first(),
        takeUntil(this.destroy$),
        pluck('userViewRole'),
        tap((roleType) => (this.relatedContentView = roleType === 'OPERATOR')),
      )
      .subscribe();

    if (this.breadcrumb) {
      combineLatest([this.route.paramMap, this.store])
        .pipe(takeUntil(this.destroy$))
        .subscribe(([paramMap, state]) => {
          const permitApplicationTaskLink = ['/permit-application', paramMap.get('taskId')];
          const permitApplicationActionLink = ['/permit-application/action', paramMap.get('actionId')];

          let firstBreadcrumb: { link: string[]; text: string } = {
            link: [],
            text: '',
          };

          let reviewGroupBreadcrumb = [];

          let taskUrlApproach = false; //whether we follow the new task url approach (i.e. tasks/{task_id}/{flow}) or the old one (i.e. permit-application/{task_id}/)

          if (state.isRequestTask) {
            switch (state.requestTaskType) {
              case 'PERMIT_ISSUANCE_APPLICATION_REVIEW':
              case 'PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW':
              case 'PERMIT_ISSUANCE_WAIT_FOR_PEER_REVIEW':
              case 'PERMIT_ISSUANCE_WAIT_FOR_AMENDS':
              case 'PERMIT_VARIATION_APPLICATION_REVIEW':
                firstBreadcrumb = {
                  link: [...permitApplicationTaskLink, 'review'],
                  text: this.reviewTypeText(state),
                };
                reviewGroupBreadcrumb = [
                  {
                    text: this.reviewGroupTitle,
                    link: [...firstBreadcrumb.link, this.reviewGroupUrl],
                  },
                ];
                break;
              case 'PERMIT_VARIATION_OPERATOR_APPLICATION_SUBMIT':
                taskUrlApproach = true;
                firstBreadcrumb = {
                  link: ['tasks/', paramMap.get('taskId'), 'permit-variation'],
                  text: 'Permit variation',
                };
                break;
              default:
                firstBreadcrumb = {
                  link: permitApplicationTaskLink,
                  text: 'Apply for a permit',
                };
            }
          } else if (state.requestActionType) {
            switch (state.requestActionType) {
              case 'PERMIT_ISSUANCE_APPLICATION_SUBMITTED':
              case 'PERMIT_VARIATION_APPLICATION_SUBMITTED':
                firstBreadcrumb = {
                  link: ['/permit-application/action', paramMap.get('actionId')],
                  text: this.typeText(state),
                };
                break;
              case 'PERMIT_ISSUANCE_APPLICATION_GRANTED':
                firstBreadcrumb.text = 'Permit determination';
                if (state.userViewRole === 'REGULATOR') {
                  firstBreadcrumb.link = [...permitApplicationActionLink, 'review'];
                  reviewGroupBreadcrumb = [
                    {
                      text: this.reviewGroupTitle,
                      link: [...firstBreadcrumb.link, this.reviewGroupUrl],
                    },
                  ];
                } else {
                  firstBreadcrumb.link = permitApplicationActionLink;
                }
                break;
              default:
                firstBreadcrumb = {
                  link: permitApplicationTaskLink,
                  text: 'Apply for a permit',
                };
            }
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
                        ...(index === 1
                          ? result[0].link.slice(0, 2)
                          : result[index - 1]?.link ?? taskUrlApproach
                          ? permitApplicationTaskLink
                          : firstBreadcrumb.link),
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

  private typeText(state: PermitApplicationState): string {
    return state.isVariation ? 'Make a change to your permit' : 'Apply for a permit';
  }

  private reviewTypeText(state: PermitApplicationState): string {
    return state.isVariation ? 'Variation determination' : 'Permit determination';
  }
}
