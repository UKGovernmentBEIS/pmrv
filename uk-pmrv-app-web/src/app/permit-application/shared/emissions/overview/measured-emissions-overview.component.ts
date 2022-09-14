import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { combineLatest, map, pluck, switchMap } from 'rxjs';

import {
  MeasMeasuredEmissions,
  MeasSourceStreamCategoryAppliedTier,
  N2OMeasuredEmissions,
  N2OSourceStreamCategoryAppliedTier,
} from 'pmrv-api';

import { PermitApplicationStore } from '../../../store/permit-application.store';

@Component({
  selector: 'app-measured-emissions-overview',
  templateUrl: './measured-emissions-overview.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MeasuredEmissionsOverviewComponent {
  @Input() measuredEmissions: MeasMeasuredEmissions & N2OMeasuredEmissions;
  @Input() cssClass: string;

  index$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('index'))));
  taskKey$ = this.route.data.pipe(
    pluck('taskKey'),
    map((taskKey) => taskKey.split('.')[1]),
  );

  task$ = this.route.data.pipe(
    switchMap((data) =>
      this.store.findTask<N2OSourceStreamCategoryAppliedTier[] | MeasSourceStreamCategoryAppliedTier[]>(data.taskKey),
    ),
  );

  files$ = combineLatest([this.index$, this.task$]).pipe(
    map(([index, tiers]) => [...(tiers?.[index].measuredEmissions?.noHighestRequiredTierJustification?.files ?? [])]),
  );

  constructor(readonly store: PermitApplicationStore, private readonly route: ActivatedRoute) {}
}
