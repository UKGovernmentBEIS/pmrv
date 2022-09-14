import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, switchMap, withLatestFrom } from 'rxjs';

import { AerService } from '@tasks/aer/core/aer.service';

@Component({
  selector: 'app-regulated-activities-delete',
  template: `
    <ng-container *ngIf="regulatedActivity$ | async as activity">
      <app-page-heading size="xl">
        Are you sure you want to delete
        <span class="nowrap"> ‘{{ activity.type | regulatedActivityType }}’? </span>
      </app-page-heading>

      <p class="govuk-body">Any reference to this item will be removed from your application.</p>

      <div class="govuk-button-group">
        <button (click)="onDelete()" appPendingButton govukWarnButton>Yes, delete</button>
        <a govukLink routerLink="..">Cancel</a>
      </div>
    </ng-container>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RegulatedActivityDeleteComponent {
  regulatedActivity$ = combineLatest([this.aerService.getTask('regulatedActivities'), this.route.paramMap]).pipe(
    map(([activities, paramMap]) => activities?.find((activity) => activity.id === paramMap.get('activityId'))),
  );

  constructor(
    private readonly aerService: AerService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
  ) {}

  onDelete(): void {
    this.route.paramMap
      .pipe(
        first(),
        map((paramMap) => paramMap.get('activityId')),
        withLatestFrom(this.aerService.getTask('regulatedActivities')),
        switchMap(([activityId, regulatedActivities]) =>
          this.aerService.postTaskSave(
            { regulatedActivities: regulatedActivities?.filter((activity) => activity.id !== activityId) },
            undefined,
            false,
            'regulatedActivities',
          ),
        ),
      )
      .subscribe(() => this.router.navigate(['../..'], { relativeTo: this.route }));
  }
}
