import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map } from 'rxjs';

import { GovukTableColumn } from 'govuk-components';

import { MeasSourceStreamCategoryAppliedTier } from 'pmrv-api';

import { statusMap } from '../../../shared/task-list/task-item/status.map';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { areCategoryTierPrerequisitesMet } from './measurement-status';

@Component({
  selector: 'app-measurement',
  templateUrl: './measurement.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MeasurementComponent {
  sourceStreamCategoryAppliedTiers$ = this.store.findTask<MeasSourceStreamCategoryAppliedTier[]>(
    'monitoringApproaches.MEASUREMENT.sourceStreamCategoryAppliedTiers',
  );
  sumOfAnnualEmitted$ = this.sourceStreamCategoryAppliedTiers$.pipe(
    map((appliedTiers) =>
      appliedTiers.reduce((total, tier) => total + (tier?.sourceStreamCategory?.annualEmittedCO2Tonnes ?? 0), 0),
    ),
  );
  columns: GovukTableColumn<any>[] = [
    { field: 'category', header: 'Source stream categories', widthClass: 'govuk-!-width-two-quarter' },
    { field: 'emissions', header: 'Emissions', widthClass: 'govuk-!-width-one-quarter' },
    { field: 'status', header: '', widthClass: 'govuk-!-width-one-quarter' },
  ];
  sourceStreamCategoriesStatus$ = this.store.pipe(
    map((state) => (!areCategoryTierPrerequisitesMet(state) ? 'cannot start yet' : 'not started')),
  );
  statusMap = statusMap;

  constructor(readonly store: PermitApplicationStore) {}
}
