import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, Observable, of, pluck, switchMap, take, withLatestFrom } from 'rxjs';

import { ItemDTO, PermitContainer, RequestActionInfoDTO } from 'pmrv-api';

import { hasRequestTaskAllowedActions } from '../../../shared/components/related-actions/request-task-allowed-actions.map';
import { TaskItemStatus } from '../../../shared/task-list/task-list.interface';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { variationDetailsStatus } from '../../variation/variation-status';
import { getAvailableSections } from '../permit-sections/available-sections';
import { TaskStatusPipe } from '../pipes/task-status.pipe';

export const permitTypeMap: Record<PermitContainer['permitType'], string> = {
  GHGE: 'Greenhouse Gas Emissions',
  HSE: 'Hospital and Small Emitters',
};

@Component({
  selector: 'app-permit-task-list',
  templateUrl: './permit-task-list.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [TaskStatusPipe],
})
export class PermitTaskListComponent {
  @Input() actions$: Observable<RequestActionInfoDTO[]>;
  @Input() relatedTasks$: Observable<ItemDTO[]>;
  @Input() isHeadingVisible = true;
  @Output() readonly submitEvent = new EventEmitter<void>();

  headerText$: Observable<string> = this.store.pipe(
    first(),
    map((state) => (state?.isVariation ? 'Make a change to your permit' : `Apply for a ${state.permitType} permit`)),
  );

  isTask$ = this.store.pipe(map((state) => state.isRequestTask));

  hasRelatedActions$ = this.store.pipe(
    map((state) => state.assignable || hasRequestTaskAllowedActions(state.allowedRequestTaskActions)),
  );

  isRelatedActionsSectionVisible$ = combineLatest([this.isTask$, this.hasRelatedActions$]).pipe(
    map(([isTask, hasRelatedActions]) => isTask && hasRelatedActions),
  );

  taskId$ = this.store.pipe(map((state) => state.requestTaskId));
  isVariation$ = this.store.pipe(map((state) => state.isVariation));

  isTaskTypeAmendsSubmit$ = this.store.pipe(
    pluck('requestTaskType'),
    map((requestTaskType) => requestTaskType === 'PERMIT_ISSUANCE_APPLICATION_AMENDS_SUBMIT'),
  );
  permitType$ = this.store.pipe(
    pluck('permitType'),
    map((permitType) => permitTypeMap[permitType]),
  );

  reviewGroupKeys$: Observable<string[]> = this.store.pipe(
    first(),
    map((state) => Object.keys(state.reviewGroupDecisions)),
  );

  managementProceduresExist$ = this.store.pipe(pluck('permit', 'managementProceduresExist'));

  aboutVariationStatus$ = this.store.pipe(
    map((state) => (state.isRequestTask ? variationDetailsStatus(state) : 'complete')),
  );

  variationSubmissionStatus$: Observable<TaskItemStatus> = this.store.pipe(
    switchMap((state) => {
      if (!state.isRequestTask) {
        return of<TaskItemStatus>('complete');
      } else {
        return this.store.pipe(
          switchMap((state) =>
            combineLatest(getAvailableSections(state).map((section: string) => this.taskStatus.transform(section))),
          ),
          withLatestFrom(this.aboutVariationStatus$),
          map(([permitStatuses, variationDetailsStatus]) =>
            permitStatuses.every((status) => status === 'complete') && variationDetailsStatus === 'complete'
              ? 'not started'
              : 'cannot start yet',
          ),
        );
      }
    }),
  );

  isVariationSubmissionStatusCannotStartedYetOrComplete$ = this.variationSubmissionStatus$.pipe(
    map((status) => status === 'cannot start yet' || status === 'complete'),
  );

  navigationState = { returnUrl: this.router.url };
  returnUrl: string;

  constructor(
    readonly store: PermitApplicationStore,
    readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly taskStatus: TaskStatusPipe,
  ) {}

  submit(): void {
    this.submitEvent.emit();
  }

  viewAmends(): void {
    this.actions$
      .pipe(
        map(
          (actions) => actions.filter((action) => action.type === 'PERMIT_ISSUANCE_APPLICATION_RETURNED_FOR_AMENDS')[0],
        ),
        map((action) => action.id),
        take(1),
      )
      .subscribe((actionId) =>
        this.router.navigate(['..', 'action', actionId, 'review', 'return-for-amends'], {
          relativeTo: this.route,
          state: this.navigationState,
        }),
      );
  }

  getSectionUrl(url: string): Observable<string> {
    return this.store.pipe(
      map((state) =>
        state?.isVariation
          ? state?.isRequestTask
            ? `/permit-application/${this.route.snapshot.paramMap.get('taskId')}/${url}`
            : `/permit-application/action/${this.route.snapshot.paramMap.get('actionId')}/${url}`
          : `.${url}`,
      ),
    );
  }

  getVariationSubmitUrl() {
    return `/permit-application/${this.route.snapshot.paramMap.get('taskId')}/variation-summary`;
  }
}
