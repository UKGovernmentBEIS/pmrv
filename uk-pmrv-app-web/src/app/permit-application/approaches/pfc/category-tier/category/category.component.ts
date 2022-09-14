import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';

import { PFCMonitoringApproach, PFCSourceStreamCategory, PFCSourceStreamCategoryAppliedTier } from 'pmrv-api';

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
    { label: 'Slope method', value: 'SLOPE', hint: '' },
    { label: 'Overvoltage method', value: 'OVERVOLTAGE', hint: '' },
  ];
  categoryTypeOptions: PFCSourceStreamCategory['categoryType'][] = ['MAJOR', 'MINOR', 'DE_MINIMIS', 'MARGINAL'];

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
      this.store.findTask<PFCSourceStreamCategoryAppliedTier[]>(
        'monitoringApproaches.PFC.sourceStreamCategoryAppliedTiers',
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
                PFC: {
                  ...state.permit.monitoringApproaches.PFC,
                  sourceStreamCategoryAppliedTiers:
                    tiers && tiers[index]
                      ? tiers.map((tier, idx) =>
                          index === idx ? { ...tier, sourceStreamCategory: this.form.value } : tier,
                        )
                      : [...(tiers ?? []), { sourceStreamCategory: this.form.value }],
                } as PFCMonitoringApproach,
              },
            },
            permitSectionsCompleted: {
              ...state.permitSectionsCompleted,
              PFC_Category:
                tiers && tiers[index]
                  ? state.permitSectionsCompleted.PFC_Category.map((item, idx) => (index === idx ? true : item))
                  : [...(state.permitSectionsCompleted.PFC_Category ?? []), true],
              PFC_Activity_Data: this.applySubtaskStatus(tiers, index, state.permitSectionsCompleted.PFC_Activity_Data),
              PFC_Emission_Factor: this.applySubtaskStatus(
                tiers,
                index,
                state.permitSectionsCompleted.PFC_Emission_Factor,
              ),
            },
          }),
        ),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => this.router.navigate(['summary'], { relativeTo: this.route, state: { notification: true } }));
  }

  private applySubtaskStatus(tiers: PFCSourceStreamCategoryAppliedTier[], index: number, status: boolean[]): boolean[] {
    return tiers && tiers[index] ? status : [...(status ?? []), false];
  }
}
