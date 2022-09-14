import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, startWith, switchMap, switchMapTo, tap } from 'rxjs';

import { PendingRequestService } from '../../../../../core/guards/pending-request.service';
import { PendingRequest } from '../../../../../core/interfaces/pending-request.interface';
import { PERMIT_TASK_FORM } from '../../../../shared/permit-task-form.token';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { categoryAppliedTier, statusKeyToSubtaskNameMapper } from '../category-tier';
import { samplingJustificationProvider } from './sampling-justification.provider';

@Component({
  selector: 'app-sampling-justification',
  templateUrl: './sampling-justification.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [samplingJustificationProvider],
})
export class SamplingJustificationComponent implements PendingRequest {
  readonly index$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('index'))));
  private readonly methodIndex$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('methodIndex'))));

  readonly isFileUploaded$ = this.form.get('files').valueChanges.pipe(
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
      const analysisMethods = item[this.subtaskName]?.analysisMethods;

      return idx === index
        ? {
            ...item,
            [this.subtaskName]: {
              ...item[this.subtaskName],
              analysisMethods: analysisMethods.map((method, methodIdx) => {
                return methodIdx === methodIndex
                  ? {
                      ...method,
                      reducedSamplingFrequencyJustification: {
                        files: this.form.value.files?.map((file) => file.uuid),
                        isCostUnreasonable: this.form.value.justification.includes('isCostUnreasonable'),
                        isOneThirdRuleAndSampling: this.form.value.justification.includes('isOneThirdRuleAndSampling'),
                      },
                    }
                  : method;
              }),
            },
          }
        : item;
    });
  }

  navigateNext(): void {
    this.router.navigate([`../../../analysis-method-list`], { relativeTo: this.route });
  }
}
