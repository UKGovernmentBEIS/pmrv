import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, takeUntil } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { DestroySubject } from '@core/services/destroy-subject.service';
import {
  activityGroupMap,
  activityHintMap,
  formGroupOptions,
  unitOptions,
} from '@shared/components/regulated-activities/regulated-activities-form-options';
import { originalOrder } from '@shared/keyvalue-order';
import { IdGeneratorService } from '@shared/services/id-generator.service';
import { AerService } from '@tasks/aer/core/aer.service';
import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';

import { RegulatedActivity } from 'pmrv-api';

import { regulatedActivityFormProvider } from './regulated-activity-form.provider';

@Component({
  selector: 'app-regulated-activity',
  templateUrl: './regulated-activity.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [regulatedActivityFormProvider, IdGeneratorService],
})
export class RegulatedActivityComponent implements PendingRequest, OnInit {
  readonly originalOrder = originalOrder;
  readonly activityGroups = formGroupOptions;
  activityGroupMap = activityGroupMap;
  unitOptions = unitOptions;
  activityHintMap = activityHintMap;
  changing = this.router.getCurrentNavigation()?.extras?.state?.changing;

  constructor(
    @Inject(AER_TASK_FORM) readonly form: FormGroup,
    readonly aerService: AerService,
    readonly pendingRequest: PendingRequestService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly idGeneratorService: IdGeneratorService,
    private readonly destroy$: DestroySubject,
  ) {}

  ngOnInit(): void {
    this.enableOptionalFields();
  }

  onSubmit(): void {
    combineLatest([this.aerService.getTask('regulatedActivities'), this.route.paramMap])
      .pipe(
        first(),
        map(([regulatedActivities, paramMap]) => {
          const type = (this.form.get('activity').value ??
            this.form.get('activityCategory').value) as RegulatedActivity['type'];
          const existingActivity = regulatedActivities?.find((activity) => activity.id === paramMap.get('activityId'));
          const id = existingActivity ? existingActivity.id : this.idGeneratorService.generateId();
          this.aerService
            .postTaskSave(
              {
                regulatedActivities: existingActivity
                  ? regulatedActivities.map((activity) =>
                      activity.id === existingActivity.id
                        ? {
                            ...existingActivity,
                            type: type,
                          }
                        : activity,
                    )
                  : [...(regulatedActivities ?? []), { id: id, type: type }],
              },
              {},
              false,
              'regulatedActivities',
            )
            .pipe(this.pendingRequest.trackRequest())
            .subscribe(() =>
              this.router.navigate(this.changing ? ['..'] : ['..', id, 'capacity'], { relativeTo: this.route }),
            );
        }),
      )
      .subscribe();
  }

  private enableOptionalFields() {
    this.form
      .get('activityCategory')
      .valueChanges.pipe(takeUntil(this.destroy$))
      .subscribe(() => {
        if (this.form.get('activityCategory')) {
          this.form.get('activity').setValue(null);
          this.form.get('activity').enable();
        }
      });
  }
}
