import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import {
  BehaviorSubject,
  combineLatest,
  filter,
  first,
  map,
  Observable,
  of,
  pluck,
  shareReplay,
  switchMap,
  tap,
} from 'rxjs';

import { AddressInputComponent } from '@shared/address-input/address-input.component';
import { BackLinkService } from '@shared/back-link/back-link.service';

import { AccountDetailsDTO, AccountUpdateService } from 'pmrv-api';

@Component({
  selector: 'app-operator-address',
  template: `
    <app-page-heading size="l">Edit operator address </app-page-heading>
    <ng-container *ngIf="form$ | async as form">
      <form (ngSubmit)="onSubmit()" [formGroup]="form">
        <govuk-error-summary *ngIf="isSummaryDisplayed | async" [form]="form"></govuk-error-summary>
        <div class="govuk-grid-row">
          <div class="govuk-grid-column-one-half">
            <app-address-input formGroupName="address"></app-address-input>
          </div>
        </div>
        <button appPendingButton govukButton type="submit">Confirm and complete</button>
      </form>
    </ng-container>
    <a govukLink routerLink="../..">Return to: Installation details</a>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [BackLinkService],
})
export class OperatorAddressComponent implements OnInit {
  account$ = this.route.data.pipe(pluck('account'));
  isSummaryDisplayed = new BehaviorSubject<boolean>(false);

  form$: Observable<FormGroup> = this.account$.pipe(
    map((account: AccountDetailsDTO) => this.createForm(account)),
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
        switchMap(([form, account]) => {
          const addressValue = { ...form.get('address').value };
          return form.dirty
            ? this.accountUpdateService
                .updateLegalEntityAddressUsingPOST(account.id, addressValue)
                .pipe(tap((this.route.snapshot.data.account.legalEntityAddress = addressValue)))
            : of(null);
        }),
      )
      .subscribe(() => this.router.navigate(['../..'], { relativeTo: this.route }));
  }

  private createForm({ legalEntityAddress }: AccountDetailsDTO) {
    return this.fb.group({
      address: this.fb.group(AddressInputComponent.controlsFactory(legalEntityAddress)),
    });
  }
}
