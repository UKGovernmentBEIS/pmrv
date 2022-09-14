import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { fakeAsync, TestBed, tick } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { catchError, lastValueFrom, Observable, of } from 'rxjs';

import { OperatorUsersRegistrationService } from 'pmrv-api';

import { ActivatedRouteSnapshotStub, CountryServiceStub } from '../../../testing';
import { CountryService } from '../../core/services/country.service';
import { PhoneNumberPipe } from '../../shared/pipes/phone-number.pipe';
import { UserRegistrationStore } from '../store/user-registration.store';
import { ConfirmedEmailGuard } from './confirmed-email.guard';

describe('ConfirmedEmailGuard', () => {
  let guard: ConfirmedEmailGuard;
  let store: UserRegistrationStore;
  let router: Router;
  let service: OperatorUsersRegistrationService;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientTestingModule],
      providers: [{ provide: CountryService, useClass: CountryServiceStub }, UserRegistrationStore, PhoneNumberPipe],
    });
    guard = TestBed.inject(ConfirmedEmailGuard);
    store = TestBed.inject(UserRegistrationStore);
    service = TestBed.inject(OperatorUsersRegistrationService);
    router = TestBed.inject(Router);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should not allow access if the token is neither in store nor in query param', () => {
    const navigateByUrlSpy = jest.spyOn(router, 'navigateByUrl').mockImplementation();
    jest.spyOn(service, 'verifyUserRegistrationTokenUsingPOST').mockImplementation(() => of(null));
    store.setState({ token: null });

    let response = guard.canActivate(new ActivatedRouteSnapshotStub());
    expect(response).toBeFalsy();
    expect(router.navigateByUrl).toHaveBeenCalledWith('/registration');

    navigateByUrlSpy.mockReset();
    response = guard.canActivate(new ActivatedRouteSnapshotStub(null, { token: '123' }));
    expect(response).toBeTruthy();
    expect(router.navigateByUrl).not.toHaveBeenCalled();

    navigateByUrlSpy.mockReset();
    store.setState({ token: '123' });
    response = guard.canActivate(new ActivatedRouteSnapshotStub());
    expect(response).toBeTruthy();
    expect(router.navigateByUrl).not.toHaveBeenCalled();
  });

  it('should update the state with the token and email', async () => {
    const email = 'test@test.gr';
    jest.spyOn(service, 'verifyUserRegistrationTokenUsingPOST').mockImplementation(() => of({ email }));
    await lastValueFrom(
      guard.canActivate(new ActivatedRouteSnapshotStub(null, { token: '123' })) as Observable<boolean>,
    );
    expect(store.getState()).toMatchObject({ token: '123', email, isInvited: false });
  });

  it('should redirect bad request', fakeAsync(() => {
    const navigateSpy = jest.spyOn(router, 'navigate').mockImplementation();
    (guard.canActivate(new ActivatedRouteSnapshotStub(null, { token: '123' })) as Observable<boolean>).subscribe();
    const request = httpTestingController.expectOne(
      'http://localhost:8080/api/v1.0/operator-users/registration/token-verification',
    );
    request.flush({ code: '123' }, { status: 400, statusText: '123' });
    tick();
    expect(navigateSpy).toHaveBeenCalledWith(['/registration/invalid-link'], { queryParams: { code: '123' } });
  }));

  it('should not redirect other service errors', fakeAsync(() => {
    const navigateSpy = jest.spyOn(router, 'navigate').mockImplementation();
    (guard.canActivate(new ActivatedRouteSnapshotStub(null, { token: '123' })) as Observable<boolean>)
      .pipe(catchError(() => of(null)))
      .subscribe();
    const request = httpTestingController.expectOne(
      'http://localhost:8080/api/v1.0/operator-users/registration/token-verification',
    );
    request.flush(null, { status: 123, statusText: '123' });
    tick();
    expect(navigateSpy).not.toHaveBeenCalled();
  }));
});
