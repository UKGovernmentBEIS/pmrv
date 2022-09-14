import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, combineLatest, map, tap } from 'rxjs';

import { PERMIT_TASK_FORM } from '../../../../shared/permit-task-form.token';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { categoryAppliedTier, statusKeyToSubtaskNameMapper } from '../category-tier';
import { analysisMethodListProvider } from './analysis-method-list.provider';

@Component({
  selector: 'app-analysis-method-list',
  templateUrl: './analysis-method-list.component.html',
  styleUrls: ['./analysis-method-list.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [analysisMethodListProvider],
})
export class AnalysisMethodListComponent {
  displayAnalysisMethodNotExistError$ = new BehaviorSubject(false);
  displaySamplingJustificationNotExistError$ = new BehaviorSubject(true);

  isSummaryDisplayed$ = combineLatest([
    this.displayAnalysisMethodNotExistError$,
    this.displaySamplingJustificationNotExistError$,
  ]).pipe(
    tap(() => this.form.updateValueAndValidity()),
    map(([displayAnalysisMethodNotExistError, displaySamplingJustificationNotExistError]) => {
      return (
        (displayAnalysisMethodNotExistError && this.form.errors?.analysisMethodNotExist) ||
        (displaySamplingJustificationNotExistError && this.form.errors?.samplingJustificationNotExist)
      );
    }),
  );

  readonly index$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('index'))));

  private readonly taskKey = this.route.snapshot.data.taskKey;
  readonly statusKey = this.route.snapshot.data.statusKey;

  private readonly categoryAppliedTiers$ = this.store.findTask<categoryAppliedTier[]>(this.taskKey);

  readonly subtaskName = statusKeyToSubtaskNameMapper[this.statusKey];

  constructor(
    @Inject(PERMIT_TASK_FORM) readonly form: FormGroup,
    readonly store: PermitApplicationStore,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onContinue(): void {
    if (!this.form.valid) {
      this.displayAnalysisMethodNotExistError$.next(true);
      this.displaySamplingJustificationNotExistError$.next(true);
    } else {
      this.navigateNext();
    }
  }
  private navigateNext() {
    this.router.navigate([`../answers`], { relativeTo: this.route });
  }
}
