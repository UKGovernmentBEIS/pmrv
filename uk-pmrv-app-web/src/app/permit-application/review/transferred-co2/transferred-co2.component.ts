import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { pluck } from 'rxjs';

@Component({
  selector: 'app-transferred-co2',
  templateUrl: './transferred-co2.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TransferredCO2Component {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  groupKey$ = this.route.data.pipe(pluck('groupKey'));

  constructor(private readonly router: Router, private readonly route: ActivatedRoute) {}
}
