import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, switchMap } from 'rxjs';

import { PendingRequestService } from '../../../../../core/guards/pending-request.service';
import { PendingRequest } from '../../../../../core/interfaces/pending-request.interface';
import { originalOrder } from '../../../../../shared/keyvalue-order';
import { PERMIT_TASK_FORM } from '../../../../shared/permit-task-form.token';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { categoryAppliedTier, statusKeyToSubtaskNameMapper } from '../category-tier';
import { referenceMap } from './reference.map';
import { referenceProvider } from './reference.provider';

@Component({
  selector: 'app-reference',
  templateUrl: './reference.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [referenceProvider],
})
export class ReferenceComponent implements PendingRequest {
  readonly index$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('index'))));

  private readonly taskKey = this.route.snapshot.data.taskKey;
  readonly statusKey = this.route.snapshot.data.statusKey;

  private readonly categoryAppliedTiers$ = this.store.findTask<categoryAppliedTier[]>(this.taskKey);

  private readonly subtaskName = statusKeyToSubtaskNameMapper[this.statusKey];

  readonly originalOrder = originalOrder;
  readonly referenceMap = Object.keys(referenceMap[this.statusKey])
    .filter((key) => key !== 'OTHER')
    .reduce((result, key) => ({ ...result, [key]: referenceMap[this.statusKey][key] }), {});

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
              ...item[this.subtaskName],
              standardReferenceSource: this.form.value,
            },
          }
        : item,
    );
  }

  private navigateNext(): void {
    this.router.navigate([`../analysis-method-used`], { relativeTo: this.route });
  }
}
