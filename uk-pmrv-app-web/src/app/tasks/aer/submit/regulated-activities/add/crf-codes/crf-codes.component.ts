import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { AerService } from '@tasks/aer/core/aer.service';
import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { crfCodesFormProvider } from '@tasks/aer/submit/regulated-activities/add/crf-codes/crf-codes-form.provider';

@Component({
  selector: 'app-regulated-activity-crf-code',
  templateUrl: './crf-codes.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [crfCodesFormProvider],
})
export class CrfCodesComponent implements PendingRequest {
  caption$ = combineLatest([this.aerService.getTask('regulatedActivities'), this.route.paramMap]).pipe(
    map(
      ([regulatedActivities, paramMap]) =>
        regulatedActivities.find((activity) => activity.id === paramMap.get('activityId')).type,
    ),
  );

  constructor(
    @Inject(AER_TASK_FORM) readonly form: FormGroup,
    readonly aerService: AerService,
    readonly pendingRequest: PendingRequestService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
  ) {}

  onSubmit(): void {
    combineLatest([this.aerService.getTask('regulatedActivities'), this.route.paramMap])
      .pipe(
        first(),
        map(([regulatedActivities, paramMap]) => {
          const hasEnergyCrf = this.form.get('hasEnergyCrf').value?.[0] ?? false;
          const hasIndustrialCrf = this.form.get('hasIndustrialCrf').value?.[0] ?? false;
          const nextStep = hasEnergyCrf ? 'energy-crf-code' : 'industrial-crf-code';
          const activity = regulatedActivities.find((activity) => activity.id === paramMap.get('activityId'));
          activity.hasEnergyCrf = hasEnergyCrf;
          activity.hasIndustrialCrf = hasIndustrialCrf;
          if (!hasEnergyCrf) {
            activity.energyCrf = null;
          }
          if (!hasIndustrialCrf) {
            activity.industrialCrf = null;
          }
          return this.aerService
            .postTaskSave({ regulatedActivities: regulatedActivities }, {}, false, 'regulatedActivities')
            .pipe(this.pendingRequest.trackRequest())
            .subscribe(() => this.router.navigate(['..', nextStep], { relativeTo: this.route }));
        }),
      )
      .subscribe();
  }
}
