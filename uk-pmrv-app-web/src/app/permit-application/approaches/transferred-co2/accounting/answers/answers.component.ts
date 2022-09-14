import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, switchMap, switchMapTo, takeUntil, tap } from 'rxjs';

import { PendingRequestService } from '../../../../../core/guards/pending-request.service';
import { PendingRequest } from '../../../../../core/interfaces/pending-request.interface';
import { DestroySubject } from '../../../../../core/services/destroy-subject.service';
import { BackLinkService } from '../../../../../shared/back-link/back-link.service';
import { PERMIT_TASK_FORM } from '../../../../shared/permit-task-form.token';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { isReviewUrl } from '../../../approaches';
import { measurementDevicesFormProvider } from '../measurement-devices-form.provider';

@Component({
  selector: 'app-answers',
  templateUrl: './answers.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [measurementDevicesFormProvider, BackLinkService, DestroySubject],
})
export class AnswersComponent implements OnInit, PendingRequest {
  isSummaryDisplayed$ = this.store.pipe(
    tap(() => this.form.updateValueAndValidity()),
    map(() => this.form.errors?.measurementDeviceNotExist),
  );

  private readonly taskId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId'))));

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
    combineLatest([this.taskId$, this.store])
      .pipe(takeUntil(this.destroy$))
      .subscribe(([taskId, state]) => {
        const url = isReviewUrl(state.requestTaskType)
          ? `/permit-application/${taskId}/review/transferred-co2`
          : `/permit-application/${taskId}/transferred-co2`;
        this.backLinkService.show(url);
      });
  }

  confirm(): void {
    if (this.form.valid) {
      this.route.data
        .pipe(
          first(),
          switchMap((data) => this.store.postStatus(data.statusKey, true)),
          this.pendingRequest.trackRequest(),
          switchMapTo(this.store),
          first(),
        )
        .subscribe((state) =>
          this.router.navigate(
            [
              state.requestTaskType === 'PERMIT_ISSUANCE_APPLICATION_REVIEW'
                ? '../../../review/transferred-co2'
                : '../summary',
            ],
            { relativeTo: this.route, state: { notification: true } },
          ),
        );
    }
  }
}
