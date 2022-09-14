import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Router } from '@angular/router';

import { PermitApplicationStore } from '../../store/permit-application.store';

@Component({
  selector: 'app-site-diagram-summary',
  templateUrl: './site-diagram-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SiteDiagramSummaryComponent {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;

  constructor(readonly store: PermitApplicationStore, private readonly router: Router) {}
}
