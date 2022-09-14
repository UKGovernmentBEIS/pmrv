import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, Observable, of, switchMap } from 'rxjs';

import { AerService } from '@tasks/aer/core/aer.service';
import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { activityFormProvider } from '@tasks/aer/submit/prtr/activity/activity-form.provider';
import {
  activitiesChildSection,
  activitiesSection,
  activityItemNameMap,
  activityItemTypeMap,
  finalWizard,
  stepWithSubActivities,
} from '@tasks/aer/submit/prtr/activity-item';

@Component({
  selector: 'app-prtr-activity',
  templateUrl: './activity.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [activityFormProvider],
})
export class ActivityComponent {
  isEditable$ = this.aerService.isEditable$;
  activityItems$: Observable<string[]> = this.route.queryParams.pipe(
    map((params) => activitiesChildSection[params?.activityItem] ?? activitiesSection),
  );
  hasSubActivities$: Observable<boolean> = this.route.queryParams.pipe(
    map((params) => stepWithSubActivities.includes(params?.activityItem) ?? false),
  );
  isFinalWizard$: Observable<boolean> = this.route.queryParams.pipe(
    map((params) => finalWizard.includes(params?.activityItem) ?? false),
  );
  isActivitySector$: Observable<boolean> = this.route.queryParams.pipe(map((params) => !params?.activityItem));
  isA1Sector$: Observable<boolean> = this.route.queryParams.pipe(map((params) => params?.activityItem === '_1_A'));
  activityItemName = activityItemNameMap;

  constructor(
    @Inject(AER_TASK_FORM) readonly form: FormGroup,
    private readonly aerService: AerService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
  ) {
    this.router.routeReuseStrategy.shouldReuseRoute = () => false;
  }

  onSubmit(): void {
    const activityItem = [
      this.form.get('subActivityA1').value,
      this.form.get('subActivityA2').value,
      this.form.get('subActivityA3').value,
      this.form.get('subActivityA4').value,
      this.form.get('subActivityA5').value,
      this.form.get('subActivityB1').value,
      this.form.get('subActivityB2').value,
      this.form.get('activity').value,
    ].find((value) => !!value);

    combineLatest([this.route.paramMap, this.aerService.getTask('pollutantRegisterActivities')])
      .pipe(
        first(),
        switchMap(([paramMap, activities]) => {
          const index = Number(paramMap.get('index'));
          const activity = activityItemTypeMap[activityItem];

          return activity
            ? this.aerService.postTaskSave(
                {
                  pollutantRegisterActivities: {
                    exist: true,
                    activities:
                      index === (activities?.activities?.length || 0)
                        ? [...(activities?.activities ?? []), activity]
                        : activities.activities.map((item, i) => (index === i ? activity : item)),
                  },
                },
                undefined,
                false,
                'pollutantRegisterActivities',
              )
            : of({ index: Number(paramMap.get('index')) });
        }),
        first(),
      )
      .subscribe((res) =>
        res
          ? this.router.navigate(['../../activity', res.index], {
              relativeTo: this.route,
              queryParams: { activityItem: this.form.get('activity').value },
            })
          : this.router.navigate(['../../summary'], { relativeTo: this.route, state: { notification: true } }),
      );
  }
}
