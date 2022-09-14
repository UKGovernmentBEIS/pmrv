import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { BehaviorSubject, first, map } from 'rxjs';

import {
  activityGroupMap,
  activityHintMap,
  formGroupOptions,
  unitOptions,
} from '@shared/components/regulated-activities/regulated-activities-form-options';

import { RegulatedActivity } from 'pmrv-api';

import { PendingRequestService } from '../../core/guards/pending-request.service';
import { PendingRequest } from '../../core/interfaces/pending-request.interface';
import { originalOrder } from '../../shared/keyvalue-order';
import { IdGeneratorService } from '../../shared/services/id-generator.service';
import { PERMIT_TASK_FORM } from '../shared/permit-task-form.token';
import { PermitApplicationStore } from '../store/permit-application.store';
import { regulatedActivitiesFormProvider } from './regulated-activities-form.provider';

@Component({
  selector: 'app-regulated-activities',
  templateUrl: './regulated-activities.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [regulatedActivitiesFormProvider, IdGeneratorService],
})
export class RegulatedActivitiesComponent implements PendingRequest {
  readonly originalOrder = originalOrder;
  readonly activityGroups = formGroupOptions;
  uncheckedRegulatedActivities: RegulatedActivity[];
  isDeleteConfirmationDisplayed$ = new BehaviorSubject<boolean>(false);
  activityGroupMap = activityGroupMap;
  unitOptions = unitOptions;
  activityHintMap = activityHintMap;

  private newRegulatedActivities: RegulatedActivity[];

  constructor(
    @Inject(PERMIT_TASK_FORM) readonly form: FormGroup,
    readonly store: PermitApplicationStore,
    readonly pendingRequest: PendingRequestService,
    private readonly route: ActivatedRoute,
    private readonly idGeneratorService: IdGeneratorService,
  ) {}

  onSubmit(): void {
    this.store
      .getTask('regulatedActivities')
      .pipe(
        first(),
        map((regulatedActivities) => {
          this.newRegulatedActivities = this.mapToStore(regulatedActivities);
          this.uncheckedRegulatedActivities = regulatedActivities.filter((currentRegAct) =>
            this.newRegulatedActivities.every((newRegAct) => newRegAct.type !== currentRegAct.type),
          );

          if (this.uncheckedRegulatedActivities.length === 0) {
            this.confirmSubmit();
          } else {
            this.isDeleteConfirmationDisplayed$.next(true);
          }
        }),
      )
      .subscribe();
  }

  confirmSubmit(): void {
    this.store
      .postTask('regulatedActivities', this.newRegulatedActivities, true)
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => this.store.navigate(this.route, 'summary', 'details'));
  }

  private mapToStore(regulatedActivities: RegulatedActivity[]): RegulatedActivity[] {
    return Object.entries<RegulatedActivity['type'][]>(this.form.value)
      .filter(([key, value]) => key.endsWith('_GROUP') && value?.length > 0)
      .map(([, value]) => value)
      .reduce(
        (result: RegulatedActivity[], types) => [
          ...result,
          ...types.map((type) => {
            const capacity = this.form.value[`${type}_CAPACITY`];
            const capacityUnit = this.form.value[`${type}_CAPACITY_UNIT`];
            const id =
              regulatedActivities.find((activity) => activity.type === type)?.id ??
              this.idGeneratorService.generateId();

            return {
              id,
              capacity,
              capacityUnit,
              type,
            } as RegulatedActivity;
          }),
        ],
        [],
      );
  }
}
