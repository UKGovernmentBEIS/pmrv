import { ChangeDetectionStrategy, Component, Inject, Input } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, map, tap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { AerService } from '@tasks/aer/core/aer.service';
import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { regulatedActivitiesFormFactory } from '@tasks/aer/submit/regulated-activities/regulated-activities-form.provider';

import { AerRegulatedActivity } from 'pmrv-api';

@Component({
  selector: 'app-regulated-activities-summary-template',
  templateUrl: './regulated-activities.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [regulatedActivitiesFormFactory],
  styleUrls: ['./regulated-activities.component.scss'],
})
export class RegulatedActivitiesComponent {
  @Input() hasBottomBorder = true;

  displayErrorSummary$ = new BehaviorSubject(false);
  isSummaryDisplayed$ = this.displayErrorSummary$.pipe(
    tap(() => this.form.updateValueAndValidity()),
    map((displaySummary) => displaySummary || this.form.errors?.missingCrf),
  );

  constructor(
    @Inject(AER_TASK_FORM) readonly form: FormGroup,
    readonly aerService: AerService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    readonly pendingRequest: PendingRequestService,
  ) {}

  missingCrfCode(activity: AerRegulatedActivity): boolean {
    return !this.hasAtLeastOneCrfCode(activity) || (activity.hasIndustrialCrf && !activity.industrialCrf);
  }

  hasAtLeastOneCrfCode(activity: AerRegulatedActivity): boolean {
    return !!activity.energyCrf || !!activity.industrialCrf;
  }

  delete(id: string): void {
    this.router.navigate(['delete', id], { relativeTo: this.route });
  }

  onSubmit(): void {
    if (!this.form.valid) {
      this.displayErrorSummary$.next(true);
      return;
    }
    this.aerService
      .postTaskSave({}, {}, true, 'regulatedActivities')
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => this.router.navigate(['..'], { relativeTo: this.route }));
  }
}
