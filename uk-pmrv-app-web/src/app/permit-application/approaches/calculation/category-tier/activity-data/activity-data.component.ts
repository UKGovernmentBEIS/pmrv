import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, switchMap } from 'rxjs';

import { CalculationActivityData, CalculationSourceStreamCategoryAppliedTier } from 'pmrv-api';

import { PendingRequestService } from '../../../../../core/guards/pending-request.service';
import { DestroySubject } from '../../../../../core/services/destroy-subject.service';
import { PERMIT_TASK_FORM } from '../../../../shared/permit-task-form.token';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { categoryTierSubtaskStatus } from '../../calculation-status';
import { activityDataFormProvider } from './activity-data-form.provider';

@Component({
  selector: 'app-activity-data',
  templateUrl: './activity-data.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [activityDataFormProvider, DestroySubject],
})
export class ActivityDataComponent {
  index$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('index'))));
  cannotStart$ = combineLatest([this.route.data, this.index$, this.store]).pipe(
    map(([data, index, state]) => categoryTierSubtaskStatus(state, data.statusKey, index) === 'cannot start yet'),
  );
  task$ = this.store.findTask<CalculationSourceStreamCategoryAppliedTier[]>(
    'monitoringApproaches.CALCULATION.sourceStreamCategoryAppliedTiers',
  );
  uncertaintyOptions: CalculationActivityData['uncertainty'][] = [
    'LESS_OR_EQUAL_1_5',
    'LESS_OR_EQUAL_2_5',
    'LESS_OR_EQUAL_5_0',
    'LESS_OR_EQUAL_7_5',
    'LESS_OR_EQUAL_10_0',
    'LESS_OR_EQUAL_12_5',
    'LESS_OR_EQUAL_15_0',
    'LESS_OR_EQUAL_17_5',
    'N_A',
  ];

  constructor(
    @Inject(PERMIT_TASK_FORM) readonly form: FormGroup,
    readonly store: PermitApplicationStore,
    readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onContinue() {
    if (!this.form.dirty) {
      this.router.navigate(['justification'], { relativeTo: this.route });
    } else {
      combineLatest([this.index$, this.task$, this.route.data, this.store])
        .pipe(
          first(),
          switchMap(([index, tiers, data, state]) =>
            this.store.postTask(
              data.taskKey,
              tiers.map((item, idx) => {
                const isHighestRequiredTier =
                  this.form.value.isHighestRequiredTierT0 ??
                  this.form.value.isHighestRequiredTierT1 ??
                  this.form.value.isHighestRequiredTierT2 ??
                  this.form.value.isHighestRequiredTierT3;

                return idx === index
                  ? {
                      ...item,
                      activityData: {
                        ...item.activityData,
                        measurementDevicesOrMethods: this.form.value.measurementDevicesOrMethods,
                        uncertainty: this.form.value.uncertainty,
                        tier: this.form.value.tier,
                        isHighestRequiredTier,
                        ...(isHighestRequiredTier !== false
                          ? {
                              noHighestRequiredTierJustification: null,
                            }
                          : null),
                      },
                    }
                  : item;
              }),
              state.permitSectionsCompleted[data.statusKey].map((item, idx) => (index === idx ? false : item)),
              data.statusKey,
            ),
          ),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.router.navigate(['justification'], { relativeTo: this.route }));
    }
  }
}
