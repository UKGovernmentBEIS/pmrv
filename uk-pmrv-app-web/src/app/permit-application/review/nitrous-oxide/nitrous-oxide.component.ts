import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { map, pluck } from 'rxjs';

import { GovukTableColumn } from 'govuk-components';

import { N2OSourceStreamCategoryAppliedTier } from 'pmrv-api';

import { statusMap } from '../../../shared/task-list/task-item/status.map';
import { areCategoryTierPrerequisitesMet } from '../../approaches/n2o/n2o-status';
import { PermitApplicationStore } from '../../store/permit-application.store';

@Component({
  selector: 'app-nitrous-oxide',
  templateUrl: './nitrous-oxide.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NitrousOxideComponent {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  groupKey$ = this.route.data.pipe(pluck('groupKey'));

  sourceStreamCategoryAppliedTiers$ = this.store.findTask<N2OSourceStreamCategoryAppliedTier[]>(
    'monitoringApproaches.N2O.sourceStreamCategoryAppliedTiers',
  );

  columns: GovukTableColumn<any>[] = [
    { field: 'category', header: 'Source stream categories', widthClass: 'govuk-!-width-two-quarter' },
    { field: 'emissions', header: 'Emissions', widthClass: 'govuk-!-width-one-quarter' },
    { field: 'measuredEmissions', header: 'Measured emissions', widthClass: 'govuk-!-width-one-quarter' },
    { field: 'status', header: '', widthClass: 'app-column-width-15-per' },
  ];

  statusMap = statusMap;

  sourceStreamCategoriesStatus$ = this.store.pipe(
    map((state) => (!areCategoryTierPrerequisitesMet(state) ? 'cannot start yet' : 'not started')),
  );
  constructor(
    readonly store: PermitApplicationStore,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}
}
