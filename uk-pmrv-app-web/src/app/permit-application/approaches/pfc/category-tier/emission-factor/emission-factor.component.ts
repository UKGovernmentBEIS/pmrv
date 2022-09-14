import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, switchMap } from 'rxjs';
import { combineLatest } from 'rxjs';

import { PFCSourceStreamCategoryAppliedTier } from 'pmrv-api';

import { PendingRequestService } from '../../../../../core/guards/pending-request.service';
import { PERMIT_TASK_FORM } from '../../../../shared/permit-task-form.token';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { categoryTierSubtaskStatus } from '../../pfc-status';
import { emissionFactorFormProvider } from './emission-factor-form.provider';

@Component({
  selector: 'app-category-tier-emission-factor',
  templateUrl: './emission-factor.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [emissionFactorFormProvider],
})
export class EmissionFactorComponent {
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
              tiers.map((item, idx) =>
                idx === index
                  ? {
                      ...item,
                      emissionFactor: {
                        tier: this.form.value.tier,
                        isHighestRequiredTier: this.form.value.isHighestRequiredTierT1,
                        ...(this.form.value.isHighestRequiredTierT1 === false
                          ? {
                              noHighestRequiredTierJustification: {
                                isCostUnreasonable:
                                  item.emissionFactor?.noHighestRequiredTierJustification?.isCostUnreasonable,
                                isTechnicallyInfeasible:
                                  item.emissionFactor?.noHighestRequiredTierJustification?.isTechnicallyInfeasible,
                                files: item.emissionFactor?.noHighestRequiredTierJustification?.files,
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
