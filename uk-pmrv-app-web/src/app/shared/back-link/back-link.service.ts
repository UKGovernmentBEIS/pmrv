import { Inject, Injectable, OnDestroy } from '@angular/core';

import { BehaviorSubject } from 'rxjs';

import { BACK_LINK, BACK_LINK_TARGET } from './back-link.factory';

@Injectable()
export class BackLinkService implements OnDestroy {
  constructor(
    @Inject(BACK_LINK) private readonly backLink$: BehaviorSubject<boolean>,
    @Inject(BACK_LINK_TARGET) private readonly backLinkTarget$: BehaviorSubject<{ link: string; fragment: string }>,
  ) {}

  show(link?: string, fragment?: string): void {
    this.backLink$.next(true);
    if (link) {
      this.backLinkTarget$.next({
        link: link,
        ...(fragment ? { fragment: fragment } : null),
      });
    }
  }

  hide(): void {
    this.backLink$.next(false);
    this.backLinkTarget$.next(null);
  }

  ngOnDestroy(): void {
    this.hide();
  }
}
