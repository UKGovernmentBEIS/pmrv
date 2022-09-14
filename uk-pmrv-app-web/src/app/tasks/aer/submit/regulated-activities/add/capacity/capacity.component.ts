import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, takeUntil } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { BackLinkService } from '@shared/back-link/back-link.service';
import { unitOptions } from '@shared/components/regulated-activities/regulated-activities-form-options';
import { AerService } from '@tasks/aer/core/aer.service';
import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { capacityFormProvider } from '@tasks/aer/submit/regulated-activities/add/capacity/capacity-form.provider';

@Component({
  selector: 'app-regulated-activity-capacity',
  templateUrl: './capacity.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [capacityFormProvider, BackLinkService],
})
export class CapacityComponent implements PendingRequest, OnInit {
  caption$ = combineLatest([this.aerService.getTask('regulatedActivities'), this.route.paramMap]).pipe(
    map(
      ([regulatedActivities, paramMap]) =>
        regulatedActivities.find((activity) => activity.id === paramMap.get('activityId')).type,
    ),
  );
  unitOptions = unitOptions;
  changing = this.router.getCurrentNavigation()?.extras?.state?.changing;

  constructor(
    @Inject(AER_TASK_FORM) readonly form: FormGroup,
    readonly aerService: AerService,
    readonly pendingRequest: PendingRequestService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly backLinkService: BackLinkService,
    private readonly destroy$: DestroySubject,
  ) {}

  ngOnInit(): void {
    this.route.paramMap
      .pipe(takeUntil(this.destroy$))
      .subscribe((paramMap) =>
        this.backLinkService.show(
          this.changing
            ? `/tasks/${paramMap.get('taskId')}/aer/submit/regulated-activities/`
            : `/tasks/${paramMap.get('taskId')}/aer/submit/regulated-activities/${paramMap.get('activityId')}`,
        ),
      );
  }

  onSubmit(): void {
    combineLatest([this.aerService.getTask('regulatedActivities'), this.route.paramMap])
      .pipe(
        first(),
        map(([regulatedActivities, paramMap]) => {
          const activity = regulatedActivities.find((activity) => activity.id === paramMap.get('activityId'));
          activity.capacity = this.form.get('activityCapacity').value;
          activity.capacityUnit = this.form.get('activityCapacityUnit').value;
          return this.aerService
            .postTaskSave({ regulatedActivities: regulatedActivities }, {}, false, 'regulatedActivities')
            .pipe(this.pendingRequest.trackRequest())
            .subscribe(() =>
              this.router.navigate([this.changing ? '../..' : '../crf-codes'], { relativeTo: this.route }),
            );
        }),
      )
      .subscribe();
  }
}
