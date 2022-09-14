import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, switchMap } from 'rxjs';

import { PermitMonitoringApproachSection } from 'pmrv-api';

import { PermitApplicationStore } from '../../store/permit-application.store';

@Component({
  selector: 'app-approaches-delete',
  template: `
    <app-approaches-delete-template (delete)="delete()" [monitoringApproach]="monitoringApproach$ | async">
    </app-approaches-delete-template>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ApproachesDeleteComponent {
  monitoringApproach$ = combineLatest([this.store.getTask('monitoringApproaches'), this.route.paramMap]).pipe(
    map(
      ([monitoringApproaches, paramMap]) =>
        Object.keys(monitoringApproaches).find(
          (approach) => approach === paramMap.get('monitoringApproach'),
        ) as PermitMonitoringApproachSection['type'],
    ),
  );

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly store: PermitApplicationStore,
  ) {}

  delete(): void {
    combineLatest([this.store, this.route.paramMap, this.route.data])
      .pipe(
        first(),
        switchMap(([state, paramMap, data]) =>
          this.store.postCategoryTask(
            data.permitTask,
            {
              ...state,
              permit: {
                ...state.permit,
                monitoringApproaches: Object.keys(state.permit.monitoringApproaches)
                  .filter((key) => key !== paramMap.get('monitoringApproach'))
                  .reduce((res, key) => ({ ...res, [key]: state.permit.monitoringApproaches[key] }), {}),
              },
              permitSectionsCompleted: {
                ...Object.keys(state.permitSectionsCompleted)
                  .filter((key) => !key.startsWith(paramMap.get('monitoringApproach')))
                  .reduce((res, key) => ({ ...res, [key]: state.permitSectionsCompleted[key] }), {}),
                monitoringApproaches: [false],
              },
            },
            {
              ...Object.keys(state.reviewSectionsCompleted)
                .filter((key) => key !== paramMap.get('monitoringApproach'))
                .reduce((res, key) => ({ ...res, [key]: state.reviewSectionsCompleted[key] }), {}),
              DEFINE_MONITORING_APPROACHES: false,
              ...(state?.determination?.type !== 'DEEMED_WITHDRAWN' ? { determination: false } : null),
            },
          ),
        ),
      )
      .subscribe(() => this.router.navigate(['../..'], { relativeTo: this.route }));
  }
}
