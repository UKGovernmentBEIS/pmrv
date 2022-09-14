import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { map, Observable, take } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { AerService } from '@tasks/aer/core/aer.service';

import { GovukTableColumn } from 'govuk-components';

import { PollutantRegisterActivities } from 'pmrv-api';

@Component({
  selector: 'app-prtr-summary',
  templateUrl: './summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryComponent {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  activities$: Observable<PollutantRegisterActivities> = this.aerService.getTask('pollutantRegisterActivities');
  activityItems$: Observable<{ activity: string }[]> = this.aerService
    .getTask('pollutantRegisterActivities')
    .pipe(map((items) => items?.activities.map((item) => ({ activity: item })) ?? []));

  columns: GovukTableColumn[] = [
    { field: 'description', header: '', widthClass: 'govuk-input--width-20' },
    { field: 'activity', header: '', widthClass: 'govuk-input--width-20' },
    { field: 'delete', header: '', widthClass: 'govuk-input--width-20' },
  ];

  constructor(
    readonly pendingRequest: PendingRequestService,
    readonly aerService: AerService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
  ) {}

  addAnother(): void {
    this.activityItems$
      .pipe(take(1))
      .subscribe((activities) =>
        this.router.navigate([`../activity/${activities.length}`], { relativeTo: this.route }),
      );
  }

  onSubmit(): void {
    this.aerService
      .postTaskSave({}, {}, true, 'pollutantRegisterActivities')
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => this.router.navigate(['../..'], { relativeTo: this.route }));
  }
}
