import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { pluck } from 'rxjs';

@Component({
  selector: 'app-inherent-co2',
  templateUrl: './inherent-co2.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class InherentCO2Component {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  groupKey$ = this.route.data.pipe(pluck('groupKey'));

  constructor(private readonly router: Router, private readonly route: ActivatedRoute) {}
}
