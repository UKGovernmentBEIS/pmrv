import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { map } from 'rxjs';

import { statusKeyToSubtaskNameMapper } from '../category-tier';

@Component({
  selector: 'app-summary',
  template: `
    <ng-container
      *ngIf="('CALCULATION' | monitoringApproachTask | async).sourceStreamCategoryAppliedTiers[index$ | async] as tier"
    >
      <app-permit-task
        [notification]="notification"
        [breadcrumb]="[
          { text: 'Calculation', link: ['calculation'] },
          { text: tier | sourceStreamCategoryName | async, link: ['calculation', 'category-tier', index$ | async] }
        ]"
      >
        <app-page-heading caption="Calculation, {{ tier | sourceStreamCategoryName | async }}">
          {{ statusKey | subtaskName }}
        </app-page-heading>
        <app-calculation-category-tier-subtasks-summary-template></app-calculation-category-tier-subtasks-summary-template>
        <a govukLink routerLink="../..">Return to: {{ tier | sourceStreamCategoryName | async }}</a>
      </app-permit-task>
    </ng-container>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryComponent {
  index$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('index'))));
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;

  readonly statusKey = this.route.snapshot.data.statusKey;
  readonly subtaskName = statusKeyToSubtaskNameMapper[this.statusKey];

  constructor(private readonly route: ActivatedRoute, private readonly router: Router) {}
}
