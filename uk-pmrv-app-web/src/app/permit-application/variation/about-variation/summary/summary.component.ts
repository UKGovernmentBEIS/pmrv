import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Router } from '@angular/router';

import { DestroySubject } from '../../../../core/services/destroy-subject.service';
import { BreadcrumbService } from '../../../../shared/breadcrumbs/breadcrumb.service';
import { PermitApplicationStore } from '../../../store/permit-application.store';

@Component({
  selector: 'app-summary',
  templateUrl: './summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [BreadcrumbService, DestroySubject],
})
export class SummaryComponent {
  notificationBanner = this.router.getCurrentNavigation()?.extras.state?.notification;

  constructor(private readonly router: Router, readonly store: PermitApplicationStore) {}
}
