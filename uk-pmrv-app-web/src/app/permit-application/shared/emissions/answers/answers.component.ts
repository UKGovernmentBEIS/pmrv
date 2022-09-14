import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, pluck, switchMap, takeUntil, tap } from 'rxjs';

import { MeasSourceStreamCategoryAppliedTier, N2OSourceStreamCategoryAppliedTier } from 'pmrv-api';

import { PendingRequestService } from '../../../../core/guards/pending-request.service';
import { PendingRequest } from '../../../../core/interfaces/pending-request.interface';
import { DestroySubject } from '../../../../core/services/destroy-subject.service';
import { BackLinkService } from '../../../../shared/back-link/back-link.service';
import { PermitApplicationStore } from '../../../store/permit-application.store';
import { PERMIT_TASK_FORM } from '../../permit-task-form.token';
import { StatusKey } from '../../types/permit-task.type';
import { measurementDevicesFormProvider } from '../measurement-devices-form.provider';

@Component({
  selector: 'app-answers',
  templateUrl: './answers.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [measurementDevicesFormProvider, BackLinkService, DestroySubject],
  styleUrls: ['./answers.component.scss'],
})
export class AnswersComponent implements OnInit, PendingRequest {
  index$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('index'))));

  taskKey$ = this.route.data.pipe(pluck('taskKey'));
  task$ = this.route.data.pipe(
    switchMap((data) =>
      this.store.findTask<N2OSourceStreamCategoryAppliedTier[] | MeasSourceStreamCategoryAppliedTier[]>(
        `monitoringApproaches.${data.taskKey}.sourceStreamCategoryAppliedTiers`,
      ),
    ),
  );

  isSummaryDisplayed$ = this.store.pipe(
    tap(() => this.form.updateValueAndValidity()),
    map(() => this.form.errors?.validMeasurementDevicesOrMethods),
  );

  files$ = combineLatest([this.index$, this.task$]).pipe(
    map(([index, tiers]) => [...(tiers?.[index].measuredEmissions?.noHighestRequiredTierJustification?.files ?? [])]),
  );

  constructor(
    @Inject(PERMIT_TASK_FORM) readonly form: FormGroup,
    readonly store: PermitApplicationStore,
    readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly backLinkService: BackLinkService,
    private readonly destroy$: DestroySubject,
  ) {}

  ngOnInit(): void {
    combineLatest([this.route.paramMap, this.taskKey$])
      .pipe(takeUntil(this.destroy$))
      .subscribe(([paramMap, taskKey]) =>
        this.backLinkService.show(
          `/permit-application/${paramMap.get('taskId')}/${
            taskKey === 'MEASUREMENT' ? 'measurement' : 'nitrous-oxide'
          }/category-tier/${paramMap.get('index')}`,
        ),
      );
  }

  confirm(): void {
    if (this.form.valid) {
      combineLatest([this.index$, this.taskKey$, this.store])
        .pipe(
          first(),
          switchMap(([index, taskKey, state]) =>
            this.store
              .postStatus(
                `${taskKey}_Measured_Emissions` as StatusKey,
                state.permitSectionsCompleted[`${taskKey}_Measured_Emissions`].map((item, idx) =>
                  index === idx ? true : item,
                ),
              )
              .pipe(this.pendingRequest.trackRequest()),
          ),
        )
        .subscribe(() =>
          this.router.navigate(['../summary'], { relativeTo: this.route, state: { notification: true } }),
        );
    }
  }

  changeClick(wizardStep?: string): void {
    this.router.navigate(['../' + wizardStep], { relativeTo: this.route, state: { changing: true } });
  }
}
