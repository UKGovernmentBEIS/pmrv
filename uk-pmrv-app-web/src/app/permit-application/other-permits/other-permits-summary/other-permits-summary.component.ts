import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Router } from '@angular/router';

import { PermitApplicationStore } from '../../store/permit-application.store';

@Component({
  selector: 'app-other-permits-summary',
  templateUrl: './other-permits-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OtherPermitsSummaryComponent {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;

  constructor(readonly store: PermitApplicationStore, private readonly router: Router) {}
}
