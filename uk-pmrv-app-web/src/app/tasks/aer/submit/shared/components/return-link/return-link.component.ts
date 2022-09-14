import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { map } from 'rxjs';

@Component({
  selector: 'app-return-link',
  template: '<a govukLink [routerLink]="link$ | async">Return to: {{ title }}</a>',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReturnLinkComponent {
  title: string;

  link$ = this.route.url.pipe(
    map((url) => {
      const isIncludedInUrl = url.some((segment) => segment.path.includes('summary'));
      this.title = 'Emissions report';

      return isIncludedInUrl ? '../..' : '..';
    }),
  );

  constructor(private readonly route: ActivatedRoute) {}
}
