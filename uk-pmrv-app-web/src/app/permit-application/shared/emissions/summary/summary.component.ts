import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, map, pluck, switchMap, tap } from 'rxjs';

import { MeasSourceStreamCategoryAppliedTier, N2OSourceStreamCategoryAppliedTier } from 'pmrv-api';

import { PermitApplicationStore } from '../../../store/permit-application.store';
import { PERMIT_TASK_FORM } from '../../permit-task-form.token';
import { measurementDevicesFormProvider } from '../measurement-devices-form.provider';

@Component({
  selector: 'app-summary',
  templateUrl: './summary.component.html',
  styleUrls: ['./summary.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [measurementDevicesFormProvider],
})
export class SummaryComponent {
  index$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('index'))));
  taskKey$ = this.route.data.pipe(pluck('taskKey'));
  task$ = this.route.data.pipe(
    switchMap((data) =>
      this.store.findTask<N2OSourceStreamCategoryAppliedTier[] | MeasSourceStreamCategoryAppliedTier[]>(
        `monitoringApproaches.${data.taskKey}.sourceStreamCategoryAppliedTiers`,
      ),
    ),
  );

  notification = this.router.getCurrentNavigation()?.extras.state?.notification;

  files$ = combineLatest([this.index$, this.task$]).pipe(
    map(([index, tiers]) => [...(tiers?.[index].measuredEmissions?.noHighestRequiredTierJustification?.files ?? [])]),
  );

  isSummaryDisplayed$ = this.store.pipe(
    tap(() => this.form.updateValueAndValidity()),
    map(() => this.form.errors?.validMeasurementDevicesOrMethods),
  );

  constructor(
    @Inject(PERMIT_TASK_FORM) readonly form: FormGroup,
    readonly store: PermitApplicationStore,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  changeClick(wizardStep?: string): void {
    this.router.navigate(['../' + wizardStep], { relativeTo: this.route, state: { changing: true } });
  }
}
