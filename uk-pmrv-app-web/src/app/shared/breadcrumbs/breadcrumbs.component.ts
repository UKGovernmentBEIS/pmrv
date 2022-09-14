import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';

import { BehaviorSubject } from 'rxjs';

import { BREADCRUMB_ITEMS } from './breadcrumb.factory';
import { BreadcrumbItem } from './breadcrumb.interface';

@Component({
  selector: 'app-breadcrumbs',
  template: `
    <govuk-breadcrumbs *ngIf="breadcrumbItem$ | async as items">
      <ng-container *ngFor="let item of items">
        <a
          *ngIf="item.link; else bareText"
          govukLink="breadcrumb"
          [routerLink]="item.link"
          [queryParams]="item.queryParams"
          >{{ item.text }}</a
        >
        <ng-template #bareText>
          <li class="govuk-breadcrumbs__list-item" govukLink="breadcrumb">{{ item.text }}</li>
        </ng-template>
      </ng-container>
    </govuk-breadcrumbs>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BreadcrumbsComponent {
  constructor(@Inject(BREADCRUMB_ITEMS) readonly breadcrumbItem$: BehaviorSubject<BreadcrumbItem[]>) {}
}
