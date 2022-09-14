import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, switchMap, switchMapTo } from 'rxjs';

import { PendingRequestService } from '../../../../../core/guards/pending-request.service';
import { PendingRequest } from '../../../../../core/interfaces/pending-request.interface';
import { PERMIT_TASK_FORM } from '../../../../shared/permit-task-form.token';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { categoryAppliedTier, statusKeyToSubtaskNameMapper } from '../category-tier';
import { formTierOptionsMap } from './tier.map';
import { tierProvider } from './tier.provider';

@Component({
  selector: 'app-tier',
  templateUrl: './tier.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [tierProvider],
})
export class TierComponent implements PendingRequest {
  readonly index$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('index'))));

  private readonly taskKey = this.route.snapshot.data.taskKey;
  readonly statusKey = this.route.snapshot.data.statusKey;

  private readonly categoryAppliedTiers$ = this.store.findTask<categoryAppliedTier[]>(this.taskKey);

  private readonly subtaskName = statusKeyToSubtaskNameMapper[this.statusKey];
  readonly tierOptions = formTierOptionsMap[this.statusKey].options;

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
          switchMapTo(combineLatest([this.index$, this.categoryAppliedTiers$])),
          first(),
        )
        .subscribe(() => this.navigateNext());
    }
  }

  private buildData(categoryAppliedTiers: categoryAppliedTier[], index: number): categoryAppliedTier[] {
    const intermediateTierSeletected = this.form.value.tier !== this.tierOptions[0].value;
    const isHighestRequiredTier = this.form.value[`isHighestRequiredTier_${this.form.value.tier}`];

    const shouldOneThirdRuleDataBeAdded =
      ['emissionFactor', 'carbonContent'].includes(this.subtaskName) && !intermediateTierSeletected;

    const shouldTierJustificationDataBeAdded = intermediateTierSeletected && isHighestRequiredTier === false;

    return categoryAppliedTiers.map((item, idx) =>
      idx === index
        ? {
            ...item,
            [this.subtaskName]: {
              exist: item?.[this.subtaskName].exist,
              tier: this.form.value.tier,
              ...(intermediateTierSeletected ? { isHighestRequiredTier: isHighestRequiredTier } : null),
              ...(shouldOneThirdRuleDataBeAdded && item?.[this.subtaskName]?.oneThirdRule
                ? { oneThirdRule: item?.[this.subtaskName].oneThirdRule }
                : null),
              ...(shouldOneThirdRuleDataBeAdded && item?.[this.subtaskName]?.oneThirdRuleFiles
                ? { oneThirdRuleFiles: item?.[this.subtaskName].oneThirdRuleFiles }
                : null),
              ...(shouldTierJustificationDataBeAdded && item?.[this.subtaskName]?.noHighestRequiredTierJustification
                ? { noHighestRequiredTierJustification: item?.[this.subtaskName].noHighestRequiredTierJustification }
                : null),
              ...(item?.[this.subtaskName]?.defaultValueApplied !== undefined
                ? { defaultValueApplied: item?.[this.subtaskName].defaultValueApplied }
                : null),
              ...(item?.[this.subtaskName]?.standardReferenceSource
                ? { standardReferenceSource: item?.[this.subtaskName].standardReferenceSource }
                : null),
              ...(item?.[this.subtaskName]?.analysisMethodUsed !== undefined
                ? { analysisMethodUsed: item?.[this.subtaskName].analysisMethodUsed }
                : null),
              ...(item?.[this.subtaskName]?.analysisMethodUsed && item?.[this.subtaskName]?.analysisMethods
                ? { analysisMethods: item?.[this.subtaskName].analysisMethods }
                : null),
            },
          }
        : item,
    );
  }

  private navigateNext(): void {
    const selectedTierOptionParameters = this.tierOptions.find((option) => option.value === this.form.value.tier);

    const isHighestRequiredTier = this.form.value[`isHighestRequiredTier_${this.form.value.tier}`];

    const nextStep = selectedTierOptionParameters.hasConditionalContent
      ? isHighestRequiredTier
        ? selectedTierOptionParameters.nextStep.isHighestRequiredTier
        : selectedTierOptionParameters.nextStep.isNotHighestRequiredTier
      : selectedTierOptionParameters.nextStep;

    this.router.navigate([`../${nextStep}`], { relativeTo: this.route });
  }
}
