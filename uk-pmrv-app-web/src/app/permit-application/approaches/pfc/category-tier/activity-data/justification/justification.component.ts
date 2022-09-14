import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, Observable, startWith, switchMap, switchMapTo, tap } from 'rxjs';

import { PFCSourceStreamCategoryAppliedTier } from 'pmrv-api';

import { PendingRequestService } from '../../../../../../core/guards/pending-request.service';
import { PendingRequest } from '../../../../../../core/interfaces/pending-request.interface';
import { PERMIT_TASK_FORM } from '../../../../../shared/permit-task-form.token';
import { PermitApplicationStore } from '../../../../../store/permit-application.store';
import { justificationFormProvider } from './justification-form.provider';

@Component({
  selector: 'app-justification',
  templateUrl: './justification.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [justificationFormProvider],
})
export class JustificationComponent implements PendingRequest {
  index$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('index'))));
  isFileUploaded$: Observable<boolean> = this.form.get('files').valueChanges.pipe(
    startWith(this.form.get('files').value),
    map((value) => value?.length > 0),
  );
  task$ = this.store.findTask<PFCSourceStreamCategoryAppliedTier[]>(
    'monitoringApproaches.PFC.sourceStreamCategoryAppliedTiers',
  );

  constructor(
    @Inject(PERMIT_TASK_FORM) readonly form: FormGroup,
    readonly store: PermitApplicationStore,
    readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onContinue(): void {
    if (!this.form.dirty) {
      this.router.navigate(['../answers'], { relativeTo: this.route });
    } else {
      combineLatest([this.index$, this.task$, this.route.data, this.store])
        .pipe(
          first(),
          switchMap(([index, tiers, data, state]) =>
            this.store.postTask(
              data.taskKey,
              tiers.map((item, idx) =>
                idx === index
                  ? {
                      ...item,
                      activityData: {
                        ...item.activityData,
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
              state.permitSectionsCompleted[data.statusKey].map((item, idx) => (index === idx ? false : item)),
              data.statusKey,
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
