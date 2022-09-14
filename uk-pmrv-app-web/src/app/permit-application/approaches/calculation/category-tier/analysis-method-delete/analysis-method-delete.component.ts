import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, switchMap } from 'rxjs';

import { PendingRequestService } from '../../../../../core/guards/pending-request.service';
import { PendingRequest } from '../../../../../core/interfaces/pending-request.interface';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { categoryAppliedTier, statusKeyToSubtaskNameMapper } from '../category-tier';

@Component({
  selector: 'app-analysis-method-delete',
  templateUrl: './analysis-method-delete.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AnalysisMethodDeleteComponent implements PendingRequest {
  readonly index$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('index'))));
  readonly methodIndex$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('methodIndex'))));

  private readonly taskKey = this.route.snapshot.data.taskKey;
  readonly statusKey = this.route.snapshot.data.statusKey;

  private readonly categoryAppliedTiers$ = this.store.findTask<categoryAppliedTier[]>(this.taskKey);

  private readonly subtaskName = statusKeyToSubtaskNameMapper[this.statusKey];

  constructor(
    readonly store: PermitApplicationStore,
    readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  delete() {
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
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => this.navigateNext());
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
              analysisMethods: analysisMethods
                .map((method, methodIdx) => {
                  return methodIdx === methodIndex ? null : method;
                })
                .filter((method) => !!method),
            },
          }
        : item;
    });
  }

  private navigateNext() {
    const nextStep = '../../../analysis-method-list';

    this.router.navigate([`${nextStep}`], { relativeTo: this.route });
  }
}
