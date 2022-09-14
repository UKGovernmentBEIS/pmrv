import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, switchMap, withLatestFrom } from 'rxjs';

import { AerService } from '@tasks/aer/core/aer.service';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { AerApplicationSubmitRequestTaskPayload, PermitMonitoringApproachSection } from 'pmrv-api';

@Component({
  selector: 'app-approaches-delete',
  template: `
    <app-approaches-delete-template (delete)="delete()" [monitoringApproach]="monitoringApproach$ | async">
    </app-approaches-delete-template>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ApproachesDeleteComponent {
  monitoringApproach$ = combineLatest([this.aerService.getTask('monitoringApproachTypes'), this.route.paramMap]).pipe(
    map(
      ([monitoringApproaches, paramMap]) =>
        monitoringApproaches.find(
          (approach) => approach === paramMap.get('monitoringApproach'),
        ) as PermitMonitoringApproachSection['type'],
    ),
  );

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly aerService: AerService,
    private readonly store: CommonTasksStore,
  ) {}

  delete(): void {
    this.route.paramMap
      .pipe(
        first(),
        map((paramMap) => paramMap.get('monitoringApproach')),
        withLatestFrom(this.store),
        switchMap(([deletedMonitoringApproach, state]) => {
          const measurementAndN2OApproaches = ['MEASUREMENT', 'N2O'];
          const payload = state.requestTaskItem.requestTask.payload as AerApplicationSubmitRequestTaskPayload;
          const aer = payload.aer;
          const remainingMonitoringApproachTypes = aer.monitoringApproachTypes.filter((approach) => approach !== deletedMonitoringApproach);
          const areMeasurementAndN2OApproachesDeleted = measurementAndN2OApproaches.includes(deletedMonitoringApproach) && !remainingMonitoringApproachTypes.some(item => measurementAndN2OApproaches.includes(item));
  
          const aerUpdatedWithRemainingMonitoringApproachTypes = { ...aer, monitoringApproachTypes: remainingMonitoringApproachTypes};
          const aerSectionsCompletedUpdatedWithMonitoringApproachTypesStatus = { ...payload.aerSectionsCompleted, "monitoringApproachTypes": [false]};

          return this.aerService.postAer(
            {
              ...state,
              requestTaskItem: {
                ...state.requestTaskItem,
                requestTask: {
                  ...state.requestTaskItem.requestTask,
                  payload: {
                    ...payload,
                    ...(
                      areMeasurementAndN2OApproachesDeleted
                      ? { 
                          aer: {
                            ...aerUpdatedWithRemainingMonitoringApproachTypes,
                            emissionPoints: [],
                          },
                          aerSectionsCompleted: {
                            ...Object.keys(aerSectionsCompletedUpdatedWithMonitoringApproachTypesStatus)
                            .filter((key) => key !== 'emissionPoints')
                            .reduce((res, key) => ({ ...res, [key]: aerSectionsCompletedUpdatedWithMonitoringApproachTypesStatus[key] }), {}),
                          },
                        }
                      : {
                          aer: aerUpdatedWithRemainingMonitoringApproachTypes,
                          aerSectionsCompleted: aerSectionsCompletedUpdatedWithMonitoringApproachTypesStatus,
                      }
                    )
                  } as AerApplicationSubmitRequestTaskPayload,
                },
              },
            },
            'AER_SAVE_APPLICATION'
          );
        }),
      )
      .subscribe(() => this.router.navigate(['../..'], { relativeTo: this.route }));
  }
}
