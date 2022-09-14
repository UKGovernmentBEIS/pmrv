import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { combineLatest, map, takeUntil } from 'rxjs';

import { DestroySubject } from '../../../core/services/destroy-subject.service';
import { BreadcrumbService } from '../../../shared/breadcrumbs/breadcrumb.service';
import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';

@Component({
  selector: 'app-permit-task-review',
  template: `
    <govuk-notification-banner *ngIf="notification" type="success">
      <h1 class="govuk-notification-banner__heading">Details updated</h1>
    </govuk-notification-banner>
    <app-page-heading>{{ heading }}</app-page-heading>
    <ng-content></ng-content>
    <a govukLink routerLink="..">Return to: {{ returnLinkText$ | async }}</a>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [BreadcrumbService, DestroySubject],
})
export class PermitTaskReviewComponent implements OnInit {
  @Input() notification: any;
  @Input() heading: string;
  @Input() breadcrumb: true;

  returnLinkText$ = this.store.pipe(map((state) => this.parentLinkText(state)));

  constructor(
    private readonly route: ActivatedRoute,
    private readonly destroy$: DestroySubject,
    private readonly breadcrumbService: BreadcrumbService,
    private readonly store: PermitApplicationStore,
  ) {}

  ngOnInit(): void {
    if (this.breadcrumb) {
      combineLatest([this.route.paramMap, this.store])
        .pipe(
          takeUntil(this.destroy$),
          map(([paramMap, state]) => {
            const link = paramMap.get('taskId')
              ? ['/permit-application', paramMap.get('taskId'), 'review']
              : ['/permit-application', 'action', paramMap.get('actionId'), 'review'];

            const text = this.parentLinkText(state);
            return { link, text };
          }),
        )
        .subscribe(({ link, text }) => this.breadcrumbService.show([{ text, link }]));
    }
  }

  private parentLinkText(state: PermitApplicationState) {
    return state.isVariation ? 'Variation determination' : 'Permit determination';
  }
}
