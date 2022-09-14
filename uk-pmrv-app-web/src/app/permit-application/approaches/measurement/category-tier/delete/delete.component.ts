import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, switchMap, withLatestFrom } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';

import { MeasMonitoringApproach } from 'pmrv-api';

import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { deleteReturnUrl } from '../../../approaches';

@Component({
  selector: 'app-category-tier-delete',
  templateUrl: './delete.component.html',
  styles: [
    `
      .nowrap {
        white-space: nowrap;
      }
    `,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DeleteComponent implements PendingRequest {
  index$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('index'))));

  constructor(
    readonly pendingRequest: PendingRequestService,
    private readonly store: PermitApplicationStore,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
  ) {}

  delete(): void {
    this.route.paramMap
      .pipe(
        first(),
        map((paramMap) => Number(paramMap.get('index'))),
        withLatestFrom(this.store, this.route.data),
        switchMap(([index, state, data]) =>
          this.store.postCategoryTask(data.taskKey, {
            ...state,
            permit: {
              ...state.permit,
              monitoringApproaches: {
                ...state.permit.monitoringApproaches,
                MEASUREMENT: {
                  ...state.permit.monitoringApproaches.MEASUREMENT,
                  sourceStreamCategoryAppliedTiers:
                    (state.permit.monitoringApproaches.MEASUREMENT as MeasMonitoringApproach)
                      .sourceStreamCategoryAppliedTiers.length > 1
                      ? (
                          state.permit.monitoringApproaches.MEASUREMENT as MeasMonitoringApproach
                        ).sourceStreamCategoryAppliedTiers.filter((_, i) => i !== index)
                      : null,
                } as MeasMonitoringApproach,
              },
            },
            permitSectionsCompleted: {
              ...state.permitSectionsCompleted,
              MEASUREMENT_Category: state.permitSectionsCompleted.MEASUREMENT_Category?.filter((_, i) => i !== index),
              MEASUREMENT_Measured_Emissions: state.permitSectionsCompleted.MEASUREMENT_Measured_Emissions?.filter(
                (_, i) => i !== index,
              ),
              MEASUREMENT_Applied_Standard: state.permitSectionsCompleted.MEASUREMENT_Applied_Standard?.filter(
                (_, i) => i !== index,
              ),
            },
          }),
        ),
        switchMap(() => this.store),
        first(),
        this.pendingRequest.trackRequest(),
      )
      .subscribe((state) => this.router.navigate([deleteReturnUrl(state, 'measurement')], { relativeTo: this.route }));
  }
}
