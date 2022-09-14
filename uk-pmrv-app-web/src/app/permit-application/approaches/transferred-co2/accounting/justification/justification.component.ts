import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, Observable, startWith, switchMap, switchMapTo, tap } from 'rxjs';

import { TransferredCO2MonitoringApproach } from 'pmrv-api';

import { PendingRequestService } from '../../../../../core/guards/pending-request.service';
import { PendingRequest } from '../../../../../core/interfaces/pending-request.interface';
import { PERMIT_TASK_FORM } from '../../../../shared/permit-task-form.token';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { justificationFormProvider } from './justification-form.provider';

@Component({
  selector: 'app-justification',
  templateUrl: './justification.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [justificationFormProvider],
})
export class JustificationComponent implements PendingRequest {
  isFileUploaded$: Observable<boolean> = this.form.get('files').valueChanges.pipe(
    startWith(this.form.get('files').value),
    map((value) => value?.length > 0),
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
      combineLatest([this.route.data, this.store.getTask('monitoringApproaches')])
        .pipe(
          first(),
          switchMap(([data, monitoringApproaches]) =>
            this.store.patchTask(
              data.taskKey,
              {
                chemicallyBound: false,
                accountingEmissionsDetails: {
                  ...(monitoringApproaches.TRANSFERRED_CO2 as TransferredCO2MonitoringApproach).accountingEmissions
                    .accountingEmissionsDetails,
                  noHighestRequiredTierJustification: {
                    isCostUnreasonable: this.form.value.justification.includes('isCostUnreasonable'),
                    isTechnicallyInfeasible: this.form.value.justification.includes('isTechnicallyInfeasible'),
                    technicalInfeasibilityExplanation: this.form.value.technicalInfeasibilityExplanation,
                    files: this.form.value.files?.map((file) => file.uuid),
                  },
                },
              },
              false,
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
        .subscribe(() => this.router.navigate(['../answers'], { relativeTo: this.route }));
    }
  }

  getDownloadUrl() {
    return this.store.createBaseFileDownloadUrl();
  }
}
