import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Router } from '@angular/router';

import { PermitApplicationStore } from '../../store/permit-application.store';

@Component({
  selector: 'app-management-procedures-exist-summary',
  templateUrl: './management-procedures-exist-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ManagementProceduresExistSummaryComponent {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;

  constructor(readonly store: PermitApplicationStore, private readonly router: Router) {}
}
