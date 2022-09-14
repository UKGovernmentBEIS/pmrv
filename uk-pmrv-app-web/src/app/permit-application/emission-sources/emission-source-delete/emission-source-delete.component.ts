import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, switchMap, withLatestFrom } from 'rxjs';

import { PermitApplicationStore } from '../../store/permit-application.store';

@Component({
  selector: 'app-emission-source-delete',
  template: `
    <app-emission-source-delete-template
      [emissionSource]="emissionSource$ | async"
      (delete)="delete()"
    ></app-emission-source-delete-template>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EmissionSourceDeleteComponent {
  emissionSource$ = combineLatest([this.store.getTask('emissionSources'), this.route.paramMap]).pipe(
    map(([emissionSources, paramMap]) => emissionSources.find((source) => source.id === paramMap.get('sourceId'))),
  );

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly store: PermitApplicationStore,
  ) {}

  delete(): void {
    this.route.paramMap
      .pipe(
        first(),
        map((paramMap) => paramMap.get('sourceId')),
        withLatestFrom(this.store, this.route.data),
        switchMap(([sourceId, state, data]) =>
          this.store.postCategoryTask(data.permitTask, {
            ...state,
            permit: {
              ...state.permit,
              emissionSources: state.permit.emissionSources.filter((item) => item.id !== sourceId),
              emissionSummaries: state.permit.emissionSummaries.map((summary) => ({
                ...summary,
                emissionSources:
                  summary.emissionSources.length > 1
                    ? summary.emissionSources.filter((summarySourceId) => summarySourceId !== sourceId)
                    : summary.emissionSources,
              })),
            },
            permitSectionsCompleted: {
              ...state.permitSectionsCompleted,
              emissionSources: [false],
            },
          }),
        ),
      )
      .subscribe(() => this.router.navigate(['../..'], { relativeTo: this.route }));
  }
}
