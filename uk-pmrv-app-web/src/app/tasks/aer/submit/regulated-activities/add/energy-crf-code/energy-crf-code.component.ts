import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, takeUntil } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { AerService } from '@tasks/aer/core/aer.service';
import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { energyCrfCodeFormProvider } from '@tasks/aer/submit/regulated-activities/add/energy-crf-code/energy-crf-code-form.provider';

import { activityItemNameMap } from '../crf-codes/crf-codes-item';

@Component({
  selector: 'app-regulated-activity-energy-crf-code',
  templateUrl: './energy-crf-code.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [energyCrfCodeFormProvider],
})
export class EnergyCrfCodeComponent implements PendingRequest, OnInit {
  caption$ = combineLatest([this.aerService.getTask('regulatedActivities'), this.route.paramMap]).pipe(
    map(
      ([regulatedActivities, paramMap]) =>
        regulatedActivities.find((activity) => activity.id === paramMap.get('activityId')).type,
    ),
  );
  activityItemName = activityItemNameMap;

  constructor(
    @Inject(AER_TASK_FORM) readonly form: FormGroup,
    readonly aerService: AerService,
    readonly pendingRequest: PendingRequestService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
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
          const activity = regulatedActivities.find((activity) => activity.id === paramMap.get('activityId'));
          activity.energyCrf = this.form.get('energyCrf').value;
          const nextStep = activity.hasIndustrialCrf ? '../industrial-crf-code' : '../..';
          return this.aerService
            .postTaskSave({ regulatedActivities: regulatedActivities }, {}, false, 'regulatedActivities')
            .pipe(this.pendingRequest.trackRequest())
            .subscribe(() => this.router.navigate([nextStep], { relativeTo: this.route }));
        }),
      )
      .subscribe();
  }

  private enableOptionalFields() {
    this.form
      .get('energyCrfCategory')
      .valueChanges.pipe(takeUntil(this.destroy$))
      .subscribe(() => {
        if (this.form.get('energyCrfCategory')) {
          this.form.get('energyCrf').setValue(null);
          this.form.get('energyCrf').enable();
        }
      });
  }
}
