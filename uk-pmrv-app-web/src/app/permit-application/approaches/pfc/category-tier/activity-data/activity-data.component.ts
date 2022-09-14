import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, switchMap, takeUntil } from 'rxjs';

import { PFCSourceStreamCategoryAppliedTier } from 'pmrv-api';

import { PendingRequestService } from '../../../../../core/guards/pending-request.service';
import { DestroySubject } from '../../../../../core/services/destroy-subject.service';
import { PERMIT_TASK_FORM } from '../../../../shared/permit-task-form.token';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { categoryTierSubtaskStatus } from '../../pfc-status';
import { activityDataFormProvider } from './activity-data-form.provider';

@Component({
  selector: 'app-activity-data',
  templateUrl: './activity-data.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [activityDataFormProvider, DestroySubject],
})
export class ActivityDataComponent implements OnInit {
  index$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('index'))));
  cannotStart$ = combineLatest([this.route.data, this.index$, this.store]).pipe(
    map(([data, index, state]) => categoryTierSubtaskStatus(state, data.statusKey, index) === 'cannot start yet'),
  );
  task$ = this.store.findTask<PFCSourceStreamCategoryAppliedTier[]>(
    'monitoringApproaches.PFC.sourceStreamCategoryAppliedTiers',
  );

  constructor(
    @Inject(PERMIT_TASK_FORM) readonly form: FormGroup,
    readonly store: PermitApplicationStore,
    readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly destroy$: DestroySubject,
  ) {}

  ngOnInit(): void {
    this.form
      .get('massBalanceApproachUsed')
      .valueChanges.pipe(takeUntil(this.destroy$))
      .subscribe(() =>
        this.form.patchValue({
          tier: null,
          isHighestRequiredTier_TIER_1: null,
          isHighestRequiredTier_TIER_2: null,
          isHighestRequiredTier_TIER_3: null,
        }),
      );
  }

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
              tiers.map((item, idx) =>
                idx === index
                  ? {
                      ...item,
                      activityData: {
                        massBalanceApproachUsed: this.form.value.massBalanceApproachUsed,
                        tier: this.form.value.tier,
                        isHighestRequiredTier: this.form.value[`isHighestRequiredTier_${this.form.value.tier}`],
                        ...(this.form.value[`isHighestRequiredTier_${this.form.value.tier}`] === false
                          ? {
                              noHighestRequiredTierJustification: {
                                isCostUnreasonable:
                                  item.activityData?.noHighestRequiredTierJustification?.isCostUnreasonable,
                                isTechnicallyInfeasible:
                                  item.activityData?.noHighestRequiredTierJustification?.isTechnicallyInfeasible,
                                technicalInfeasibilityExplanation:
                                  item.activityData?.noHighestRequiredTierJustification
                                    ?.technicalInfeasibilityExplanation,
                                files: item.activityData?.noHighestRequiredTierJustification?.files,
                              },
                            }
                          : null),
                      },
                    }
                  : item,
              ),
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
