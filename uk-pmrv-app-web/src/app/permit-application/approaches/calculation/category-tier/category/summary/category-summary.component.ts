import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { map } from 'rxjs';

import { PermitApplicationStore } from '../../../../../store/permit-application.store';

@Component({
  selector: 'app-category-summary',
  templateUrl: './category-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CategorySummaryComponent {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  index$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('index'))));

  constructor(
    readonly store: PermitApplicationStore,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}
}
