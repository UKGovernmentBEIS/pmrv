import { fakeAsync, TestBed, tick } from '@angular/core/testing';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlSegment } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { AccountsService } from 'pmrv-api';

import { AccountsServiceStub } from '../../../testing/accounts.service.stub';
import { INSTALLATION_FORM, installationFormFactory } from '../factories/installation-form.factory';
import { OnshoreDetails } from '../installation-type/installation';
import { GasEmissionsDetailsGuard } from './gas-emissions-details.guard';

const value: OnshoreDetails = {
  name: 'Name',
  siteName: 'Site name',
  gridReference: 'ab 123 456 23',
  address: {
    line1: 'Line',
    line2: null,
    city: 'City',
    country: 'Country',
    postcode: '123',
  },
};

describe('GasEmissionsDetailsGuard', () => {
  let guard: GasEmissionsDetailsGuard;
  let router: Router;
  let form: FormGroup;

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.url = [new UrlSegment('emissions', null)];
  const routerStateSnapshot = {
    url: '/installation-account/application/installation/emissions',
  } as RouterStateSnapshot;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [
        GasEmissionsDetailsGuard,
        installationFormFactory,
        FormBuilder,
        { provide: AccountsService, useClass: AccountsServiceStub },
      ],
    });
    router = TestBed.inject(Router);
    guard = TestBed.inject(GasEmissionsDetailsGuard);
    form = TestBed.inject(INSTALLATION_FORM);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should redirect to type if no data available', fakeAsync(() => {
    jest.spyOn(router, 'parseUrl').mockImplementation();

    expect(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot)).toEqual(
      router.parseUrl('installation-account/installation'),
    );
  }));

  it('should activate if data exist', fakeAsync(() => {
    form.get('onshoreGroup').patchValue(value);
    form.get('installationTypeGroup').get('type').patchValue('ONSHORE');
    jest.spyOn(router, 'parseUrl').mockImplementation();

    tick(500);
    expect(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot)).toBeTruthy();
  }));
});
