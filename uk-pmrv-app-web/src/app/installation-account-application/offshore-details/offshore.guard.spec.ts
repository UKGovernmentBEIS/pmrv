import { fakeAsync, TestBed } from '@angular/core/testing';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlSegment } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { AccountsService, LocationDTO } from 'pmrv-api';

import { AccountsServiceStub } from '../../../testing/accounts.service.stub';
import { INSTALLATION_FORM, installationFormFactory } from '../factories/installation-form.factory';
import { OffshoreGuard } from './offshore.guard';

const value: LocationDTO = {
  type: 'OFFSHORE',
};

describe('OffshoreGuard', () => {
  let guard: OffshoreGuard;
  let router: Router;
  let form: FormGroup;

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.url = [new UrlSegment('offshore', null)];
  const routerStateSnapshot = { url: '/installation-account/application/installation/offshore' } as RouterStateSnapshot;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [
        OffshoreGuard,
        installationFormFactory,
        FormBuilder,
        { provide: AccountsService, useClass: AccountsServiceStub },
      ],
    });
    router = TestBed.inject(Router);
    guard = TestBed.inject(OffshoreGuard);
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
    form.get('installationTypeGroup').patchValue(value);
    jest.spyOn(router, 'parseUrl').mockImplementation();

    expect(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot)).toBeTruthy();
  }));
});
