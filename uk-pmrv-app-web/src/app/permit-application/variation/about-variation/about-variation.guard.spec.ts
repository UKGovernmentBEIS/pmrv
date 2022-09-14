import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockVariationSubmitState } from '../../testing/mock-state';
import { AboutVariationGuard } from './about-variation.guard';

describe('AboutVariationGuard', () => {
  let store: PermitApplicationStore;
  let guard: AboutVariationGuard;
  let router: Router;

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.params = { taskId: 276 };

  beforeEach(() => {
    TestBed.configureTestingModule({ imports: [RouterTestingModule, HttpClientTestingModule] });

    guard = TestBed.inject(AboutVariationGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(PermitApplicationStore);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should redirect to summary if status is completed', async () => {
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: false } } } as any);
    store.setState({
      ...mockVariationSubmitState,
      permitVariationDetails: null,
      permitVariationDetailsCompleted: true,
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl('/permit-application/276/about/summary'));
  });

  it('should redirect to answers if wizard is completed', async () => {
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: false } } } as any);
    store.setState({
      ...mockVariationSubmitState,
      permitVariationDetails: {
        reason: 'Reason',
        modifications: [
          {
            type: 'INSTALLATION_NAME',
          },
          {
            type: 'NEW_SOURCE_STREAMS',
          },
          {
            type: 'DEFAULT_VALUE_OR_ESTIMATION_METHOD',
          },
        ],
      },
      permitVariationDetailsCompleted: false,
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl('/permit-application/276/about/answers'));
  });

  it('should activate if not started', async () => {
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: false } } } as any);
    store.setState({
      ...mockVariationSubmitState,
      permitVariationDetails: null,
      permitVariationDetailsCompleted: false,
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);
  });
});
