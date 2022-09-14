import { ChangeDetectionStrategy, Component, OnDestroy } from '@angular/core';

import { ConcurrencyErrorService } from './concurrency-error.service';

@Component({
  selector: 'app-concurrency-error',
  template: `
    <app-error-page *ngIf="concurrencyErrorService.error$ | async as error" [heading]="error.heading">
      <p class="govuk-body">
        <a govukLink [routerLink]="error.link" [fragment]="error.fragment">{{ error.linkText }}</a>
      </p>
    </app-error-page>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ConcurrencyErrorComponent implements OnDestroy {
  constructor(readonly concurrencyErrorService: ConcurrencyErrorService) {}

  ngOnDestroy(): void {
    this.concurrencyErrorService.clear();
  }
}
