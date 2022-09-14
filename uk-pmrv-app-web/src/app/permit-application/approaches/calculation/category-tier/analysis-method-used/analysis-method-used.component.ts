import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, switchMap } from 'rxjs';

import { PendingRequestService } from '../../../../../core/guards/pending-request.service';
import { PendingRequest } from '../../../../../core/interfaces/pending-request.interface';
import { PERMIT_TASK_FORM } from '../../../../shared/permit-task-form.token';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { categoryAppliedTier, statusKeyToSubtaskNameMapper } from '../category-tier';
import { analysisΜethodUsedProvider } from './analysis-method-used.provider';

@Component({
  selector: 'app-analysis-method-used',
  templateUrl: './analysis-method-used.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [analysisΜethodUsedProvider],
})
export class AnalysisMethodUsedComponent implements PendingRequest {
  readonly index$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('index'))));

  private readonly taskKey = this.route.snapshot.data.taskKey;
  readonly statusKey = this.route.snapshot.data.statusKey;

  private readonly categoryAppliedTiers$ = this.store.findTask<categoryAppliedTier[]>(this.taskKey);

  private readonly subtaskName = statusKeyToSubtaskNameMapper[this.statusKey];

  constructor(
    @Inject(PERMIT_TASK_FORM) readonly form: FormGroup,
    readonly store: PermitApplicationStore,
    readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onContinue(): void {
    if (!this.form.dirty) {
      this.navigateNext();
    } else {
      combineLatest([this.index$, this.categoryAppliedTiers$, this.store])
        .pipe(
          first(),
          switchMap(([index, categoryAppliedTiers, state]) =>
            this.store.postTask(
              this.taskKey,
              this.buildData(categoryAppliedTiers, index),
              state.permitSectionsCompleted[this.statusKey].map((item, idx) => (index === idx ? false : item)),
              this.statusKey,
            ),
          ),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.navigateNext());
    }
  }

  private buildData(categoryAppliedTiers: categoryAppliedTier[], index: number): categoryAppliedTier[] {
    return categoryAppliedTiers.map((item, idx) =>
      idx === index
        ? {
            ...item,
            [this.subtaskName]: {
              exist: item?.[this.subtaskName].exist,
              tier: item?.[this.subtaskName]?.tier,
              ...(item?.[this.subtaskName]?.isHighestRequiredTier !== undefined
                ? { isHighestRequiredTier: item?.[this.subtaskName].isHighestRequiredTier }
                : null),
              ...(item?.[this.subtaskName]?.oneThirdRule !== undefined
                ? { oneThirdRule: item?.[this.subtaskName].oneThirdRule }
                : null),
              ...(item?.[this.subtaskName]?.oneThirdRuleFiles
                ? { oneThirdRuleFiles: item?.[this.subtaskName].oneThirdRuleFiles }
                : null),
              ...(item?.[this.subtaskName]?.noHighestRequiredTierJustification
                ? { noHighestRequiredTierJustification: item?.[this.subtaskName].noHighestRequiredTierJustification }
                : null),
              defaultValueApplied: item?.[this.subtaskName]?.defaultValueApplied,
              ...(item?.[this.subtaskName]?.defaultValueApplied && item?.[this.subtaskName]?.standardReferenceSource
                ? { standardReferenceSource: item?.[this.subtaskName].standardReferenceSource }
                : null),
              analysisMethodUsed: this.form.value.analysisMethodUsed,
              ...(this.form.value.analysisMethodUsed && item?.[this.subtaskName]?.analysisMethods
                ? { analysisMethods: item?.[this.subtaskName].analysisMethods }
                : null),
            },
          }
        : item,
    );
  }

  private navigateNext(): void {
    combineLatest([this.index$, this.categoryAppliedTiers$])
      .pipe(first())
      .subscribe(([index, categoryAppliedTiers]) => {
        const analysisMethods = categoryAppliedTiers[index]?.[this.subtaskName]?.analysisMethods;

        const nextStep = this.form.value.analysisMethodUsed
          ? analysisMethods?.length
            ? 'analysis-method-list'
            : 'analysis-method/0'
          : 'answers';

        this.router.navigate([`../${nextStep}`], { relativeTo: this.route });
      });
  }
}
