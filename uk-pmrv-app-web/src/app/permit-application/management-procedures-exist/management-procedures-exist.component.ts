import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { combineLatest, first, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';

import { Permit } from 'pmrv-api';

import { PERMIT_TASK_FORM } from '../shared/permit-task-form.token';
import { PermitApplicationStore } from '../store/permit-application.store';
import { getManagementProceduresTasks } from './management-procedures-exist';
import { managementProceduresExistFormProvider } from './management-procedures-exist-form.provider';

@Component({
  selector: 'app-management-procedures-exist',
  templateUrl: './management-procedures-exist.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [managementProceduresExistFormProvider],
})
export class ManagementProceduresExistComponent implements PendingRequest {
  constructor(
    @Inject(PERMIT_TASK_FORM) readonly form: FormGroup,
    readonly store: PermitApplicationStore,
    readonly pendingRequest: PendingRequestService,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit(): void {
    const exists = this.form.get('exists').value;
    if (exists) {
      this.store
        .postTask('managementProceduresExist', true, true)
        .pipe(this.pendingRequest.trackRequest())
        .subscribe(() => this.store.navigate(this.route, 'summary', 'management-procedures'));
    } else {
      combineLatest([this.store, this.route.data])
        .pipe(
          first(),
          switchMap(([state, data]) =>
            this.store.postCategoryTask(data.permitTask, {
              ...state,
              permit: {
                ...Object.keys(state.permit)
                  .filter((key) => !getManagementProceduresTasks().includes(key))
                  .reduce((res, key) => ({ ...res, [key]: state.permit[key] }), {}),
                managementProceduresExist: false,
              } as Permit,
              permitSectionsCompleted: {
                ...Object.keys(state.permitSectionsCompleted)
                  .filter((key) => !getManagementProceduresTasks().includes(key))
                  .reduce((res, key) => ({ ...res, [key]: state.permitSectionsCompleted[key] }), {}),
                managementProceduresExist: [true],
              },
            }),
          ),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.store.navigate(this.route, 'summary', 'management-procedures'));
    }
  }
}
