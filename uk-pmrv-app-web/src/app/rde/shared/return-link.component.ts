import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { first, map, Observable } from 'rxjs';

import { RdeStore } from '../store/rde.store';

@Component({
  selector: 'app-return-link',
  template: `
    <a govukLink [routerLink]="'/' + (returnLink$ | async) + '/' + (taskId$ | async) + '/review'">
      Return to: {{ returnText$ | async }}
    </a>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReturnLinkComponent {
  constructor(private readonly route: ActivatedRoute, private readonly store: RdeStore) {}

  readonly taskId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId'))));
  readonly returnLink$ = this.returnToLink();
  readonly returnText$ = this.returnToText();

  returnToLink(): Observable<string> {
    return this.store.pipe(
      first(),
      map((state) => {
        switch (state.requestType) {
          case 'PERMIT_ISSUANCE':
          case 'PERMIT_VARIATION':
            return 'permit-application';
          case 'PERMIT_SURRENDER':
            return 'permit-surrender';
          default:
            return '';
        }
      }),
    );
  }

  returnToText(): Observable<string> {
    return this.store.pipe(
      first(),
      map((state) => {
        switch (state.requestType) {
          case 'PERMIT_ISSUANCE':
            return 'Permit Determination';
          case 'PERMIT_VARIATION':
            return 'Permit variation review';
          case 'PERMIT_SURRENDER':
            return 'Permit surrender determination';
          default:
            return '';
        }
      }),
    );
  }
}
