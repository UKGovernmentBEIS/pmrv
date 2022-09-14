import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, switchMap, withLatestFrom } from 'rxjs';

import { PermitApplicationStore } from '../../store/permit-application.store';

@Component({
  selector: 'app-emission-point-delete',
  template: `
    <app-emission-point-delete-template (delete)="onDelete()" [emissionPoint]="emissionPoint$ | async">
    </app-emission-point-delete-template>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  styles: [
    `
      .nowrap {
        white-space: nowrap;
      }
    `,
  ],
})
export class EmissionPointDeleteComponent {
  emissionPoint$ = combineLatest([this.store.getTask('emissionPoints'), this.route.paramMap]).pipe(
    map(([emissionPoints, paramMap]) =>
      emissionPoints.find((emissionPoint) => emissionPoint.id === paramMap.get('emissionPointId')),
    ),
  );

  constructor(
    private readonly store: PermitApplicationStore,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
  ) {}

  onDelete(): void {
    this.route.paramMap
      .pipe(
        first(),
        map((paramMap) => paramMap.get('emissionPointId')),
        withLatestFrom(this.store, this.route.data),
        switchMap(([pointId, state, data]) =>
          this.store.postCategoryTask(data.permitTask, {
            ...state,
            permit: {
              ...state.permit,
              emissionPoints: state.permit.emissionPoints.filter((item) => item.id !== pointId),
              emissionSummaries: state.permit.emissionSummaries.map((summary) => ({
                ...summary,
                emissionPoints:
                  summary.emissionPoints.length > 1
                    ? summary.emissionPoints.filter((summaryPointId) => summaryPointId !== pointId)
                    : summary.emissionPoints,
              })),
            },
            permitSectionsCompleted: {
              ...state.permitSectionsCompleted,
              emissionPoints: [false],
            },
          }),
        ),
      )
      .subscribe(() => this.router.navigate(['../..'], { relativeTo: this.route }));
  }
}
