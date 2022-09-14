import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, pluck, switchMap, takeUntil, tap } from 'rxjs';

import { PendingRequestService } from '../../../../../../core/guards/pending-request.service';
import { PendingRequest } from '../../../../../../core/interfaces/pending-request.interface';
import { DestroySubject } from '../../../../../../core/services/destroy-subject.service';
import { BackLinkService } from '../../../../../../shared/back-link/back-link.service';
import { PERMIT_TASK_FORM } from '../../../../../shared/permit-task-form.token';
import { PermitApplicationStore } from '../../../../../store/permit-application.store';
import { measurementDevicesFormProvider } from '../measurement-devices-form.provider';

@Component({
  selector: 'app-answers',
  templateUrl: './answers.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [BackLinkService, measurementDevicesFormProvider, DestroySubject],
})
export class AnswersComponent implements OnInit, PendingRequest {
  index$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('index'))));

  isSummaryDisplayed$ = this.store.pipe(
    tap(() => this.form.updateValueAndValidity()),
    map(() => this.form.errors?.validMeasurementDevicesOrMethods),
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
    this.route.paramMap
      .pipe(takeUntil(this.destroy$))
      .subscribe((paramMap) =>
        this.backLinkService.show(
          `/permit-application/${paramMap.get('taskId')}/calculation/category-tier/${paramMap.get('index')}`,
        ),
      );
  }

  onConfirm() {
    if (this.form.valid) {
      combineLatest([this.index$, this.route.data.pipe(pluck('statusKey')), this.store])
        .pipe(
          first(),
          switchMap(([index, statusKey, state]) =>
            this.store.postStatus(
              statusKey,
              state.permitSectionsCompleted[statusKey].map((item, idx) => (index === idx ? true : item)),
            ),
          ),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() =>
          this.router.navigate(['../summary'], { relativeTo: this.route, state: { notification: true } }),
        );
    }
  }
}
