import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, Observable, startWith, switchMap, switchMapTo, tap } from 'rxjs';

import { PendingRequestService } from '../../../../../core/guards/pending-request.service';
import { PendingRequest } from '../../../../../core/interfaces/pending-request.interface';
import { originalOrder } from '../../../../../shared/keyvalue-order';
import { PERMIT_TASK_FORM } from '../../../../shared/permit-task-form.token';
import { SamplingFrequencyPipe } from '../../../../shared/pipes/sampling-frequency.pipe';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { categoryAppliedTier, statusKeyToSubtaskNameMapper } from '../category-tier';
import { analysisMethodProvider } from './analysis-method.provider';

@Component({
  selector: 'app-analysis-method',
  templateUrl: './analysis-method.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [analysisMethodProvider, SamplingFrequencyPipe],
})
export class AnalysisMethodComponent implements PendingRequest {
  readonly index$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('index'))));
  private readonly methodIndex$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('methodIndex'))));

  samplingFrequencyMap = ['CONTINUOUS', 'DAILY', 'WEEKLY', 'MONTHLY', 'QUARTERLY', 'BI_ANNUALLY', 'ANNUALLY', 'OTHER'];

  readonly originalOrder = originalOrder;
  readonly isFileUploaded$: Observable<boolean> = this.form.get('files').valueChanges.pipe(
    startWith(this.form.get('files').value),
    map((value) => value?.length > 0),
  );

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
      combineLatest([this.index$, this.methodIndex$, this.categoryAppliedTiers$, this.store])
        .pipe(
          first(),
          switchMap(([index, methodIndex, categoryAppliedTiers, state]) =>
            this.store.postTask(
              this.taskKey,
              this.buildData(categoryAppliedTiers, index, methodIndex),
              state.permitSectionsCompleted[this.statusKey].map((item, idx) => (index === idx ? false : item)),
              this.statusKey,
            ),
          ),
          switchMapTo(this.store),
          first(),
          tap((state) =>
            this.store.setState({
              ...state,
              permitAttachments: {
                ...state.permitAttachments,
                ...this.form.value.files?.reduce((result, item) => ({ ...result, [item.uuid]: item.file.name }), {}),
              },
            }),
          ),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.navigateNext());
    }
  }

  getDownloadUrl() {
    return this.store.createBaseFileDownloadUrl();
  }

  private buildData(
    categoryAppliedTiers: categoryAppliedTier[],
    index: number,
    methodIndex: number,
  ): categoryAppliedTier[] {
    return categoryAppliedTiers.map((item, idx) => {
      const analysisMethodPayload = { ...this.form.value, files: this.form.value.files?.map((file) => file.uuid) };
      const analysisMethods = item[this.subtaskName]?.analysisMethods;

      return idx === index
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
              analysisMethodUsed: true,
              analysisMethods: analysisMethods
                ? methodIndex >= analysisMethods.length
                  ? [...analysisMethods, analysisMethodPayload]
                  : analysisMethods.map((method, methodIdx) => {
                      return methodIdx === methodIndex
                        ? {
                            ...analysisMethodPayload,
                            ...(!this.form.value.frequencyMeetsMinRequirements
                              ? { reducedSamplingFrequencyJustification: method.reducedSamplingFrequencyJustification }
                              : {}),
                          }
                        : method;
                    })
                : [analysisMethodPayload],
            },
          }
        : item;
    });
  }

  private navigateNext() {
    const nextStep = this.form.value.frequencyMeetsMinRequirements
      ? '../../analysis-method-list'
      : './sampling-justification';

    this.router.navigate([`${nextStep}`], { relativeTo: this.route });
  }
}
