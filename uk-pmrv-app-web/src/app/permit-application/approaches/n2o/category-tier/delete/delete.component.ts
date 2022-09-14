import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, switchMap, withLatestFrom } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';

import { N2OMonitoringApproach } from 'pmrv-api';

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
                N2O: {
                  ...state.permit.monitoringApproaches.N2O,
                  sourceStreamCategoryAppliedTiers:
                    (state.permit.monitoringApproaches.N2O as N2OMonitoringApproach).sourceStreamCategoryAppliedTiers
                      .length > 1
                      ? (
                          state.permit.monitoringApproaches.N2O as N2OMonitoringApproach
                        ).sourceStreamCategoryAppliedTiers.filter((_, i) => i !== index)
                      : null,
                } as N2OMonitoringApproach,
              },
            },
            permitSectionsCompleted: {
              ...state.permitSectionsCompleted,
              N2O_Category: state.permitSectionsCompleted.N2O_Category?.filter((_, i) => i !== index),
              N2O_Measured_Emissions: state.permitSectionsCompleted.N2O_Measured_Emissions?.filter(
                (_, i) => i !== index,
              ),
              N2O_Applied_Standard: state.permitSectionsCompleted.N2O_Applied_Standard?.filter((_, i) => i !== index),
            },
          }),
        ),
        switchMap(() => this.store),
        first(),
        this.pendingRequest.trackRequest(),
      )
      .subscribe((state) =>
        this.router.navigate([deleteReturnUrl(state, 'nitrous-oxide')], { relativeTo: this.route }),
      );
  }
}
