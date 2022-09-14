import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Router } from '@angular/router';

import { DestroySubject } from '../../../../core/services/destroy-subject.service';

@Component({
  selector: 'app-approaches-prepare-summary',
  templateUrl: './approaches-prepare-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class ApproachesPrepareSummaryComponent {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;

  constructor(private readonly router: Router) {}
}
