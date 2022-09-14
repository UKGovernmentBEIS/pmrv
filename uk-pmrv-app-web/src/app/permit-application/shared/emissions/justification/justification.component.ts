import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, pluck, startWith, switchMap, switchMapTo, tap } from 'rxjs';

import { MeasSourceStreamCategoryAppliedTier, N2OSourceStreamCategoryAppliedTier } from 'pmrv-api';

import { PendingRequestService } from '../../../../core/guards/pending-request.service';
import { DestroySubject } from '../../../../core/services/destroy-subject.service';
import { PermitApplicationStore } from '../../../store/permit-application.store';
import { StatusKey, TaskKey } from '../../types/permit-task.type';
import { JUSTIFICATION_FORM, justificationFormProvider } from './justification-form.provider';

@Component({
  selector: 'app-justification',
  templateUrl: './justification.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [justificationFormProvider, DestroySubject],
})
export class JustificationComponent {
  index$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('index'))));

  taskKey$ = this.route.data.pipe(pluck('taskKey'));
  task$ = this.route.data.pipe(
    switchMap((data) =>
      this.store.findTask<N2OSourceStreamCategoryAppliedTier[] | MeasSourceStreamCategoryAppliedTier[]>(
        `monitoringApproaches.${data.taskKey}.sourceStreamCategoryAppliedTiers`,
      ),
    ),
  );

  isFileUploaded$ = this.form.get('files').valueChanges.pipe(
    startWith(this.form.get('files').value),
    map((value) => value?.length > 0),
  );

  constructor(
    @Inject(JUSTIFICATION_FORM) readonly form: FormGroup,
    readonly store: PermitApplicationStore,
    readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onContinue(): void {
    if (!this.form.dirty) {
      this.router.navigate(['../answers'], { relativeTo: this.route });
    } else {
      combineLatest([this.index$, this.task$, this.taskKey$, this.store])
        .pipe(
          first(),
          switchMap(([index, tiers, taskKey, state]) =>
            this.store.postTask(
              `monitoringApproaches.${taskKey}.sourceStreamCategoryAppliedTiers` as TaskKey,
              tiers.map((item, idx) =>
                idx === index
                  ? {
                      ...item,
                      measuredEmissions: {
                        ...item.measuredEmissions,
                        noHighestRequiredTierJustification: {
                          isCostUnreasonable: this.form.value.justification.includes('isCostUnreasonable'),
                          isTechnicallyInfeasible: this.form.value.justification.includes('isTechnicallyInfeasible'),
                          technicalInfeasibilityExplanation: this.form.value.technicalInfeasibilityExplanation,
                          files: this.form.value.files?.map((file) => file.uuid),
                        },
                      },
                    }
                  : item,
              ),
              state.permitSectionsCompleted[`${taskKey}_Measured_Emissions`].map((item, idx) =>
                index === idx ? false : item,
              ),
              `${taskKey}_Measured_Emissions` as StatusKey,
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
        .subscribe(() => {
          this.router.navigate(['../answers'], { relativeTo: this.route });
        });
    }
  }

  getDownloadUrl() {
    return this.store.createBaseFileDownloadUrl();
  }
}
