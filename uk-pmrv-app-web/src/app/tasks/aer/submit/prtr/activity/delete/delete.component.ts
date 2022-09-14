import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { AerService } from '@tasks/aer/core/aer.service';

@Component({
  selector: 'app-prtr-activity-delete',
  template: `
    <app-page-heading size="xl">
      Are you sure you want to delete
      <span class="nowrap"> ‘{{ activity$ | async | activityItemName }}’? </span>
    </app-page-heading>

    <p class="govuk-body">Any reference to this item will be removed from your application.</p>

    <div class="govuk-button-group">
      <button (click)="onDelete()" appPendingButton govukWarnButton>Yes, delete</button>
      <a govukLink routerLink="../../../summary">Cancel</a>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DeleteComponent {
  activity$ = combineLatest([this.route.paramMap, this.aerService.getTask('pollutantRegisterActivities')]).pipe(
    map(([paramMap, activities]) => activities.activities[Number(paramMap.get('index'))]),
  );

  constructor(
    readonly pendingRequest: PendingRequestService,
    private readonly aerService: AerService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
  ) {}

  onDelete(): void {
    combineLatest([this.route.paramMap, this.aerService.getTask('pollutantRegisterActivities')])
      .pipe(
        first(),
        switchMap(([paramMap, activities]) => {
          const index = Number(paramMap.get('index'));

          return this.aerService.postTaskSave(
            {
              pollutantRegisterActivities: {
                ...activities,
                activities:
                  activities.activities.length <= 1 ? [] : activities.activities.filter((_, idx) => idx !== index),
              },
            },
            undefined,
            false,
            'pollutantRegisterActivities',
          );
        }),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => this.router.navigate(['./../../summary'], { relativeTo: this.route }));
  }
}
