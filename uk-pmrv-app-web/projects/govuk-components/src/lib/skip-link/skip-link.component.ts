import { Component, Input } from '@angular/core';
import { NavigationEnd, Router } from '@angular/router';

import { filter, map } from 'rxjs';

/* eslint-disable @angular-eslint/prefer-on-push-component-change-detection */
@Component({
  selector: 'govuk-skip-link',
  template: `
    <div>
      <a class="govuk-skip-link" [routerLink]="routerLink | async" queryParamsHandling="preserve" [fragment]="anchor">
        Skip to main content
      </a>
    </div>
  `,
  styles: [
    `
      div {
        float: left;
        margin: 0.3em 0 0 0.3em;
        z-index: 1;
        position: absolute;
      }
    `,
  ],
})
export class SkipLinkComponent {
  @Input() anchor = 'main-content';

  routerLink = this.router.events.pipe(
    filter((event) => event instanceof NavigationEnd),
    map((event: NavigationEnd) => event.url.split('#')[0].split('?')[0]),
  );

  constructor(private readonly router: Router) {}
}
