import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { pluck } from 'rxjs';

@Component({
  selector: 'app-uncertainty-analysis',
  templateUrl: './uncertainty-analysis.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UncertaintyAnalysisComponent {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  groupKey$ = this.route.data.pipe(pluck('groupKey'));

  constructor(private readonly router: Router, private readonly route: ActivatedRoute) {}
}
