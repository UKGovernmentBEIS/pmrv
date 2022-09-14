import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';

import {
  CalculationMonitoringApproach,
  CalculationSourceStreamCategory,
  CalculationSourceStreamCategoryAppliedTier,
} from 'pmrv-api';

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
  calculationMethodOptions = [
    {
      label: 'Standard calculation',
      value: 'STANDARD',
      hint: 'Where total CO2 is determined solely by calculating activity data (amount of a fuel or material used) alongside other relevant calculation parameters',
    },
    {
      label: 'Mass balance',
      value: 'MASS_BALANCE',
      hint: 'Where total CO2 is determined by considering the amount of a fuel or material before and after some process has occurred to release CO2',
    },
  ];
  categoryTypeOptions: CalculationSourceStreamCategory['categoryType'][] = ['MAJOR', 'MINOR', 'DE_MINIMIS', 'MARGINAL'];

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
      this.store.findTask<CalculationSourceStreamCategoryAppliedTier[]>(
        'monitoringApproaches.CALCULATION.sourceStreamCategoryAppliedTiers',
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
                CALCULATION: {
                  ...state.permit.monitoringApproaches.CALCULATION,
                  sourceStreamCategoryAppliedTiers:
                    tiers && tiers[index]
                      ? tiers.map((tier, idx) =>
                          index === idx ? { ...tier, sourceStreamCategory: this.form.value } : tier,
                        )
                      : [...(tiers ?? []), { sourceStreamCategory: this.form.value }],
                } as CalculationMonitoringApproach,
              },
            },
            permitSectionsCompleted: {
              ...state.permitSectionsCompleted,
              CALCULATION_Category:
                tiers && tiers[index]
                  ? state.permitSectionsCompleted.CALCULATION_Category.map((item, idx) => (index === idx ? true : item))
                  : [...(state.permitSectionsCompleted.CALCULATION_Category ?? []), true],
              CALCULATION_Activity_Data: this.applySubtaskStatus(
                tiers,
                index,
                state.permitSectionsCompleted.CALCULATION_Activity_Data,
              ),
              CALCULATION_Calorific: this.applySubtaskStatus(
                tiers,
                index,
                state.permitSectionsCompleted.CALCULATION_Calorific,
              ),
              CALCULATION_Emission_Factor: this.applySubtaskStatus(
                tiers,
                index,
                state.permitSectionsCompleted.CALCULATION_Emission_Factor,
              ),
              CALCULATION_Oxidation_Factor: this.applySubtaskStatus(
                tiers,
                index,
                state.permitSectionsCompleted.CALCULATION_Oxidation_Factor,
              ),
              CALCULATION_Carbon_Content: this.applySubtaskStatus(
                tiers,
                index,
                state.permitSectionsCompleted.CALCULATION_Carbon_Content,
              ),
              CALCULATION_Conversion_Factor: this.applySubtaskStatus(
                tiers,
                index,
                state.permitSectionsCompleted.CALCULATION_Conversion_Factor,
              ),
              CALCULATION_Biomass_Fraction: this.applySubtaskStatus(
                tiers,
                index,
                state.permitSectionsCompleted.CALCULATION_Biomass_Fraction,
              ),
            },
          }),
        ),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => this.router.navigate(['summary'], { relativeTo: this.route, state: { notification: true } }));
  }

  private applySubtaskStatus(
    tiers: CalculationSourceStreamCategoryAppliedTier[],
    index: number,
    status: boolean[],
  ): boolean[] {
    return tiers && tiers[index] ? status : [...(status ?? []), false];
  }
}
