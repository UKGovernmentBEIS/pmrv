import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, pluck, switchMap, takeUntil } from 'rxjs';

import {
  MeasMonitoringApproach,
  N2OMeasuredEmissions,
  N2OMonitoringApproach,
  N2OSourceStreamCategoryAppliedTier,
} from 'pmrv-api';
import { MeasSourceStreamCategoryAppliedTier } from 'pmrv-api';

import { PendingRequestService } from '../../../core/guards/pending-request.service';
import { DestroySubject } from '../../../core/services/destroy-subject.service';
import { areMeasMeasuredEmissionsPrerequisitesMet } from '../../approaches/measurement/measurement-status';
import { areN2OMeasuredEmissionsPrerequisitesMet } from '../../approaches/n2o/n2o-status';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { StatusKey, TaskKey } from '../types/permit-task.type';
import { EMISSIONS_FORM, emissionsFormProvider } from './emissions-form.provider';

@Component({
  selector: 'app-emissions',
  templateUrl: './emissions.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [emissionsFormProvider, DestroySubject],
})
export class EmissionsComponent implements OnInit {
  index$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('index'))));

  taskKey$ = this.route.data.pipe(pluck('taskKey'));
  task$ = this.route.data.pipe(
    switchMap((data) =>
      this.store.findTask<N2OSourceStreamCategoryAppliedTier[] | MeasSourceStreamCategoryAppliedTier[]>(
        `monitoringApproaches.${data.taskKey}.sourceStreamCategoryAppliedTiers`,
      ),
    ),
  );

  measuredEmissionsNotApplicable$ = combineLatest([this.index$, this.taskKey$, this.store]).pipe(
    map(([index, taskKey, state]) =>
      taskKey === 'MEASUREMENT'
        ? !areMeasMeasuredEmissionsPrerequisitesMet(state, index)
        : !areN2OMeasuredEmissionsPrerequisitesMet(state, index),
    ),
  );

  typeOptions: { value: N2OMeasuredEmissions['samplingFrequency']; label: string }[] = [
    { value: 'CONTINUOUS', label: 'Continuous' },
    { value: 'DAILY', label: 'Daily' },
    { value: 'WEEKLY', label: 'Weekly' },
    { value: 'MONTHLY', label: 'Monthly' },
    { value: 'BI_ANNUALLY', label: 'Bi annually' },
    { value: 'ANNUALLY', label: 'Annually' },
  ];

  tierCategory$ = combineLatest([this.index$, this.taskKey$, this.store]).pipe(
    map(
      ([index, taskKey, state]) =>
        (state.permit.monitoringApproaches[taskKey] as N2OMonitoringApproach | MeasMonitoringApproach)
          ?.sourceStreamCategoryAppliedTiers?.[index]?.sourceStreamCategory,
    ),
  );

  tierCategorySourceStream$ = combineLatest([this.store.getTask('sourceStreams'), this.tierCategory$]).pipe(
    map(([sourceStreams, tierCategory]) =>
      sourceStreams.find((sourceStream) => sourceStream.id === tierCategory?.sourceStream),
    ),
  );

  estimatedAnnualEmissions$ = this.store.findTask('estimatedAnnualEmissions').pipe(pluck('quantity'));
  constructor(
    @Inject(EMISSIONS_FORM) readonly form: FormGroup,
    readonly store: PermitApplicationStore,
    readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly destroy$: DestroySubject,
  ) {}

  ngOnInit(): void {
    this.form
      .get('tier')
      .valueChanges.pipe(takeUntil(this.destroy$))
      .subscribe((value) => {
        if (value === 'TIER_3') {
          this.form.patchValue({ isHighestRequiredTier: null });
        }
      });
  }

  onContinue(): void {
    if (!this.form.dirty) {
      this.router.navigate(['justification'], { relativeTo: this.route });
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
                        ...(this.form.value.isHighestRequiredTierT1 === false ||
                        this.form.value.isHighestRequiredTierT2 === false ||
                        this.form.value.isHighestRequiredTierT3 === false
                          ? item.measuredEmissions
                          : null),
                        measurementDevicesOrMethods: this.form.value.measurementDevicesOrMethods,
                        samplingFrequency: this.form.value.samplingFrequency,
                        tier: this.form.value.tier,
                        otherSamplingFrequency: this.form.value.otherSamplingFrequency,
                        ...{
                          isHighestRequiredTier:
                            this.form.value.isHighestRequiredTierT1 ??
                            this.form.value.isHighestRequiredTierT2 ??
                            this.form.value.isHighestRequiredTierT3,
                        },
                        noTierJustification: this.form.value.noTierJustification,
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
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.router.navigate(['justification'], { relativeTo: this.route }));
    }
  }
}
