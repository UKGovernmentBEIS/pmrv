import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { pluck } from 'rxjs';

import { PermitApplicationStore } from '../../store/permit-application.store';

@Component({
  selector: 'app-management-procedures',
  templateUrl: './management-procedures.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ManagementProceduresComponent {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  groupKey$ = this.route.data.pipe(pluck('groupKey'));
  managementProceduresExist$ = this.store.pipe(pluck('permit', 'managementProceduresExist'));

  constructor(
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly store: PermitApplicationStore,
  ) {}
}
