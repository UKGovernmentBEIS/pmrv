import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { pluck } from 'rxjs';

import { PermitApplicationStore } from '@permit-application/store/permit-application.store';

@Component({
  selector: 'app-details',
  templateUrl: './details.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DetailsComponent {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  groupKey$ = this.route.data.pipe(pluck('groupKey'));
  installationOperatorDetails$ = this.store.select('installationOperatorDetails');

  constructor(
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly store: PermitApplicationStore,
  ) {}
}
