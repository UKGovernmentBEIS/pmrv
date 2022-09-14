import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { Observable, pluck, switchMap } from 'rxjs';

import { ProcedureForm } from 'pmrv-api';

import { DestroySubject } from '../../../../../core/services/destroy-subject.service';
import { TaskKey } from '../../../../shared/types/permit-task.type';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { heading } from '../heading';

@Component({
  selector: 'app-summary',
  templateUrl: './summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class SummaryComponent {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  taskKey$: Observable<TaskKey> = this.route.data.pipe(pluck('taskKey'));
  procedure$ = this.taskKey$.pipe(switchMap((taskKey) => this.store.findTask<ProcedureForm>(taskKey)));
  heading = heading;

  constructor(
    readonly store: PermitApplicationStore,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}
}
