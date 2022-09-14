import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, switchMap, withLatestFrom } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';

import { FallbackMonitoringApproach } from 'pmrv-api';

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
                FALLBACK: {
                  ...state.permit.monitoringApproaches.FALLBACK,
                  sourceStreamCategoryAppliedTiers:
                    (state.permit.monitoringApproaches.FALLBACK as FallbackMonitoringApproach)
                      .sourceStreamCategoryAppliedTiers.length > 1
                      ? (
                          state.permit.monitoringApproaches.FALLBACK as FallbackMonitoringApproach
                        ).sourceStreamCategoryAppliedTiers.filter((_, i) => i !== index)
                      : null,
                } as FallbackMonitoringApproach,
              },
            },
            permitSectionsCompleted: {
              ...state.permitSectionsCompleted,
              FALLBACK_Category: state.permitSectionsCompleted.FALLBACK_Category?.filter((_, i) => i !== index),
            },
          }),
        ),
        switchMap(() => this.store),
        first(),
        this.pendingRequest.trackRequest(),
      )
      .subscribe((state) => this.router.navigate([deleteReturnUrl(state, 'fall-back')], { relativeTo: this.route }));
  }
}
