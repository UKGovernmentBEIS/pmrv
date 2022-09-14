import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, first, of, pluck, switchMap, tap } from 'rxjs';

import { GovukSelectOption } from 'govuk-components';

import { AccountUpdateService, CoordinatesDTO, LocationOffShoreDTO, LocationOnShoreDTO } from 'pmrv-api';

import {
  coordinateValidatorFn,
  getLatitudeControls,
  gridReferenceControl,
  maxSecondsValidatorFn,
} from '../../../installation-account-application/factories/installation-form.factory';
import { AddressInputComponent } from '../../../shared/address-input/address-input.component';
import { BackLinkService } from '../../../shared/back-link/back-link.service';

@Component({
  selector: 'app-address',
  templateUrl: './address.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [BackLinkService],
})
export class AddressComponent implements OnInit {
  isSummaryDisplayed = new BehaviorSubject<boolean>(false);
  account$ = this.route.data.pipe(pluck('account'));

  latitudeDirections: GovukSelectOption<CoordinatesDTO['cardinalDirection']>[] = [
    { text: 'North', value: 'NORTH' },
    { text: 'South', value: 'SOUTH' },
  ];
  longitudeDirections: GovukSelectOption<CoordinatesDTO['cardinalDirection']>[] = [
    { text: 'East', value: 'EAST' },
    { text: 'West', value: 'WEST' },
  ];

  form: FormGroup;

  constructor(
    private readonly fb: FormBuilder,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly accountUpdateService: AccountUpdateService,
    private readonly backLinkService: BackLinkService,
  ) {}

  ngOnInit(): void {
    this.account$
      .pipe(
        first(),
        tap(
          (account) =>
            (this.form =
              account?.location?.type === 'ONSHORE'
                ? this.fb.group({
                    ...gridReferenceControl(account.location.gridReference),
                    address: this.fb.group(AddressInputComponent.controlsFactory(account.location.address)),
                  })
                : this.fb.group({
                    latitude: this.fb.group(getLatitudeControls(account.location.latitude), {
                      validators: [coordinateValidatorFn('Latitude', 90), maxSecondsValidatorFn('Latitude')],
                    }),
                    longitude: this.fb.group(getLatitudeControls(account.location.longitude), {
                      validators: [coordinateValidatorFn('Longitude', 180), maxSecondsValidatorFn('Longitude')],
                    }),
                  })),
        ),
      )
      .subscribe();

    this.backLinkService.show();
  }

  onSubmit(): void {
    if (this.form.valid) {
      this.account$
        .pipe(
          first(),
          switchMap((account) => {
            if (this.form.dirty) {
              const location =
                account.location.type === 'ONSHORE'
                  ? ({
                      type: account.location.type,
                      gridReference: this.form.get('gridReference').value,
                      address: {
                        city: this.form.get('address.city').value,
                        line1: this.form.get('address.line1').value,
                        line2: this.form.get('address.line2').value,
                        postcode: this.form.get('address.postcode').value,
                        country: this.form.get('address.country').value,
                      },
                    } as LocationOnShoreDTO)
                  : ({
                      type: account.location.type,
                      latitude: {
                        degree: this.form.get('latitude.degree').value,
                        minute: this.form.get('latitude.minute').value,
                        second: this.form.get('latitude.second').value,
                        cardinalDirection: this.form.get('latitude.cardinalDirection').value,
                      },
                      longitude: {
                        degree: this.form.get('longitude.degree').value,
                        minute: this.form.get('longitude.minute').value,
                        second: this.form.get('longitude.second').value,
                        cardinalDirection: this.form.get('longitude.cardinalDirection').value,
                      },
                    } as LocationOffShoreDTO);

              return this.accountUpdateService
                .updateAccountAddressUsingPOST(account.id, location)
                .pipe(tap((this.route.snapshot.data.account.location = location as any)));
            } else {
              return of(null);
            }
          }),
        )
        .subscribe(() => this.router.navigate(['../..'], { relativeTo: this.route }));
    } else {
      this.isSummaryDisplayed.next(true);
    }
  }
}
