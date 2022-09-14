import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, combineLatest, filter, first, map, of, pluck, shareReplay, switchMap, tap } from 'rxjs';

import { GovukValidators } from 'govuk-components';

import { AccountUpdateService } from 'pmrv-api';

import { BackLinkService } from '../../../shared/back-link/back-link.service';

@Component({
  selector: 'app-site-name',
  template: `
    <app-page-heading size="l">Edit site name</app-page-heading>

    <form (ngSubmit)="onSubmit()" *ngIf="form$ | async as form" [formGroup]="form">
      <govuk-error-summary *ngIf="isSummaryDisplayed | async" [form]="form"></govuk-error-summary>
      <div class="govuk-grid-row">
        <div class="govuk-grid-column-one-half">
          <div formControlName="siteName" govuk-text-input></div>
        </div>
      </div>
      <button appPendingButton govukButton type="submit">Confirm and complete</button>
    </form>

    <a govukLink routerLink="../..">Return to: Installation details</a>
  `,

  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [BackLinkService],
})
export class SiteNameComponent implements OnInit {
  account$ = this.route.data.pipe(pluck('account'));
  isSummaryDisplayed = new BehaviorSubject<boolean>(false);

  form$ = this.account$.pipe(
    map((account) =>
      this.fb.group({
        siteName: [
          account?.siteName,
          [
            GovukValidators.required('Enter site name'),
            GovukValidators.maxLength(255, 'The site name should not be more than 255 characters'),
          ],
        ],
      }),
    ),
    shareReplay({ bufferSize: 1, refCount: true }),
  );

  constructor(
    private readonly fb: FormBuilder,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly accountUpdateService: AccountUpdateService,
    private readonly backLinkService: BackLinkService,
  ) {}

  ngOnInit() {
    this.backLinkService.show();
  }

  onSubmit(): void {
    combineLatest([this.form$, this.account$])
      .pipe(
        first(),
        tap(([form]) => {
          if (!form.valid) {
            this.isSummaryDisplayed.next(true);
          }
        }),
        filter(([form]) => form.valid),
        switchMap(([form, account]) =>
          form.dirty
            ? this.accountUpdateService
                .updateAccountSiteNameUsingPOST(account.id, {
                  siteName: form.get('siteName').value,
                })
                .pipe(tap((this.route.snapshot.data.account.siteName = form.get('siteName').value)))
            : of(null),
        ),
      )
      .subscribe(() => this.router.navigate(['../..'], { relativeTo: this.route }));
  }
}
