import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';

import { BehaviorSubject } from 'rxjs';

import { BACK_LINK, BACK_LINK_TARGET } from './back-link.factory';

@Component({
  selector: 'app-back-link',
  template: `<govuk-back-link
    *ngIf="backLink$ | async"
    [link]="(backLinkTarget$ | async)?.link"
    [fragment]="(backLinkTarget$ | async)?.fragment"
  ></govuk-back-link>`,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BackLinkComponent {
  constructor(
    @Inject(BACK_LINK) readonly backLink$: BehaviorSubject<boolean>,
    @Inject(BACK_LINK_TARGET) readonly backLinkTarget$: BehaviorSubject<{ link: string; fragment: string }>,
  ) {}
}
