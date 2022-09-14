import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map } from 'rxjs';

import { GovukTableColumn } from 'govuk-components';

import { N2OSourceStreamCategoryAppliedTier } from 'pmrv-api';

import { statusMap } from '../../../shared/task-list/task-item/status.map';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { areCategoryTierPrerequisitesMet } from './n2o-status';

@Component({
  selector: 'app-n2o',
  templateUrl: './n2o.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class N2oComponent {
  sourceStreamCategoryAppliedTiers$ = this.store.findTask<N2OSourceStreamCategoryAppliedTier[]>(
    'monitoringApproaches.N2O.sourceStreamCategoryAppliedTiers',
  );
  sumOfAnnualEmitted$ = this.sourceStreamCategoryAppliedTiers$.pipe(
    map((appliedTiers) =>
      appliedTiers.reduce((total, tier) => total + (tier?.sourceStreamCategory?.annualEmittedCO2Tonnes ?? 0), 0),
    ),
  );
  columns: GovukTableColumn<any>[] = [
    { field: 'category', header: 'Source stream categories', widthClass: 'govuk-!-width-one-third' },
    { field: 'emissions', header: 'Emissions', widthClass: 'govuk-!-width-one-third' },
    { field: 'status', header: '', widthClass: 'govuk-!-width-one-third' },
  ];
  sourceStreamCategoriesStatus$ = this.store.pipe(
    map((state) => (!areCategoryTierPrerequisitesMet(state) ? 'cannot start yet' : 'not started')),
  );
  statusMap = statusMap;

  constructor(readonly store: PermitApplicationStore) {}
}
