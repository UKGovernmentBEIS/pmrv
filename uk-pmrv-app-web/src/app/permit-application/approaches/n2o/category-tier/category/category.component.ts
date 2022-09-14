import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';

import { N2OMonitoringApproach, N2OSourceStreamCategory, N2OSourceStreamCategoryAppliedTier } from 'pmrv-api';

import { PERMIT_TASK_FORM } from '../../../../shared/permit-task-form.token';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { categoryFormProvider } from './category-form.provider';

@Component({
  selector: 'app-category',
  templateUrl: './category.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [categoryFormProvider],
})
export class CategoryComponent implements PendingRequest {
  index$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('index'))));
  emissionTypeOptions = [
    { label: 'Abated', value: 'ABATED', hint: '' },
    { label: 'Unabated', value: 'UNABATED', hint: '' },
  ];
  monitoringApproachTypeOptions = [
    { label: 'Calculation', value: 'CALCULATION' },
    { label: 'Measurement', value: 'MEASUREMENT' },
  ];
  categoryTypeOptions: N2OSourceStreamCategory['categoryType'][] = ['MAJOR', 'MINOR', 'DE_MINIMIS', 'MARGINAL'];

  constructor(
    @Inject(PERMIT_TASK_FORM) readonly form: FormGroup,
    readonly store: PermitApplicationStore,
    readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit(): void {
    combineLatest([
      this.index$,
      this.store.findTask<N2OSourceStreamCategoryAppliedTier[]>(
        'monitoringApproaches.N2O.sourceStreamCategoryAppliedTiers',
      ),
      this.store,
      this.route.data,
    ])
      .pipe(
        first(),
        switchMap(([index, tiers, state, data]) =>
          this.store.postCategoryTask(data.taskKey, {
            ...state,
            permit: {
              ...state.permit,
              monitoringApproaches: {
                ...state.permit.monitoringApproaches,
                N2O: {
                  ...state.permit.monitoringApproaches.N2O,
                  sourceStreamCategoryAppliedTiers:
                    tiers && tiers[index]
                      ? tiers.map((tier, idx) =>
                          index === idx ? { ...tier, sourceStreamCategory: this.form.value } : tier,
                        )
                      : [...(tiers ?? []), { sourceStreamCategory: this.form.value }],
                } as N2OMonitoringApproach,
              },
            },
            permitSectionsCompleted: {
              ...state.permitSectionsCompleted,
              N2O_Category:
                tiers && tiers[index]
                  ? state.permitSectionsCompleted.N2O_Category.map((item, idx) => (index === idx ? true : item))
                  : [...(state.permitSectionsCompleted.N2O_Category ?? []), true],
              N2O_Measured_Emissions: this.applySubtaskStatus(
                tiers,
                index,
                state.permitSectionsCompleted.N2O_Measured_Emissions,
              ),
              N2O_Applied_Standard: this.applySubtaskStatus(
                tiers,
                index,
                state.permitSectionsCompleted.N2O_Applied_Standard,
              ),
            },
          }),
        ),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => this.router.navigate(['summary'], { relativeTo: this.route, state: { notification: true } }));
  }

  private applySubtaskStatus(tiers: N2OSourceStreamCategoryAppliedTier[], index: number, status: boolean[]): boolean[] {
    return tiers && tiers[index] ? status : [...(status ?? []), false];
  }
}
