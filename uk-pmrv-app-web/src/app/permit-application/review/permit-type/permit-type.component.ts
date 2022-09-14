import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { map, pluck } from 'rxjs';

import { PermitApplicationStore } from '../../store/permit-application.store';

@Component({
  selector: 'app-permit-type',
  templateUrl: './permit-type.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PermitTypeComponent {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  groupKey$ = this.route.data.pipe(pluck('groupKey'));
  permitType$ = this.store.pipe(pluck('permitType'));

  isPermitTypeEditable$ = this.store.pipe(map((state) => !state.isVariation));

  constructor(
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly store: PermitApplicationStore,
  ) {}
}
