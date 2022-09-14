import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, Observable, startWith, switchMap, switchMapTo, tap } from 'rxjs';

import { PendingRequestService } from '../../../../../core/guards/pending-request.service';
import { PendingRequest } from '../../../../../core/interfaces/pending-request.interface';
import { PERMIT_TASK_FORM } from '../../../../shared/permit-task-form.token';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { categoryAppliedTier, statusKeyToSubtaskNameMapper } from '../category-tier';
import { oneThirdProvider } from './one-third.provider';

@Component({
  selector: 'app-one-third',
  templateUrl: './one-third.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [oneThirdProvider],
})
export class OneThirdComponent implements PendingRequest {
  readonly index$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('index'))));

  isFileUploaded$: Observable<boolean> = this.form.get('oneThirdRuleFiles').valueChanges.pipe(
    startWith(this.form.get('oneThirdRuleFiles').value),
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
          switchMapTo(this.store),
          first(),
          tap((state) =>
            this.store.setState({
              ...state,
              permitAttachments: {
                ...state.permitAttachments,
                ...this.form.value.oneThirdRuleFiles?.reduce(
                  (result, item) => ({ ...result, [item.uuid]: item.file.name }),
                  {},
                ),
              },
            }),
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
              oneThirdRule: this.form.value.oneThirdRule,
              oneThirdRuleFiles: this.form.value.oneThirdRuleFiles?.map((file) => file.uuid),
            },
          }
        : item,
    );
  }

  getDownloadUrl() {
    return this.store.createBaseFileDownloadUrl();
  }

  private navigateNext(): void {
    this.router.navigate(['../default-value'], { relativeTo: this.route });
  }
}
