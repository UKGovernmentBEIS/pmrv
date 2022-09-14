import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { PermitApplicationStore } from '../../../store/permit-application.store';
import { mockState, mockStateBuild } from '../../../testing/mock-state';
import { EmissionFactorGuard } from './emission-factor.guard';

describe('EmissionFactorGuard', () => {
  let guard: EmissionFactorGuard;
  let router: Router;
  let store: PermitApplicationStore;

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.params = { taskId: 276 };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientTestingModule],
    });
    guard = TestBed.inject(EmissionFactorGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(PermitApplicationStore);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should redirect to summary if task is completed', async () => {
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: false } } } as any);

    store.setState(mockStateBuild(undefined, { PFC_Tier2EmissionFactor: [true] }));

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl(`/permit-application/276/pfc/emission-factor/summary`));
  });

  it('should activate if task is changing', async () => {
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: true } } } as any);
    await expect(guard.canActivate(activatedRouteSnapshot)).toEqual(true);
  });

  it('should activate if wizard not completed', async () => {
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: false } } } as any);

    store.setState(
      mockStateBuild({
        measurementDevicesOrMethods: [],
        monitoringApproaches: {
          PFC: {
            type: 'PFC',
          },
        },
      }),
    );

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);

    store.setState(
      mockStateBuild({
        measurementDevicesOrMethods: [],
        monitoringApproaches: {
          PFC: {
            type: 'PFC',
            tier2EmissionFactor: {
              exist: true,
            },
          },
        },
      }),
    );

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);

    store.setState(
      mockStateBuild({
        measurementDevicesOrMethods: [],
        monitoringApproaches: {
          PFC: {
            type: 'PFC',
            tier2EmissionFactor: {
              exist: true,
              determinationInstallation: {
                procedureDescription: 'procedureDescription',
                procedureDocumentName: 'procedureDocumentName',
                procedureReference: 'procedureReference',
                diagramReference: 'diagramReference',
                responsibleDepartmentOrRole: 'responsibleDepartmentOrRole',
                locationOfRecords: 'locationOfRecords',
                itSystemUsed: 'itSystemUsed',
                appliedStandards: 'appliedStandards',
              },
            },
          },
        },
      }),
    );

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);

    store.setState(
      mockStateBuild({
        measurementDevicesOrMethods: [],
        monitoringApproaches: {
          PFC: {
            type: 'PFC',
            tier2EmissionFactor: {
              exist: true,
              scheduleMeasurements: {
                procedureDescription: 'procedureDescription',
                procedureDocumentName: 'procedureDocumentName',
                procedureReference: 'procedureReference',
                diagramReference: 'diagramReference',
                responsibleDepartmentOrRole: 'responsibleDepartmentOrRole',
                locationOfRecords: 'locationOfRecords',
                itSystemUsed: 'itSystemUsed',
                appliedStandards: 'appliedStandards',
              },
            },
          },
        },
      }),
    );

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);
  });

  it('should redirect to answers if wizard is completed', async () => {
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: false } } } as any);

    store.setState(mockState);

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl(`/permit-application/276/pfc/emission-factor/answers`));

    store.setState(
      mockStateBuild({
        measurementDevicesOrMethods: [],
        monitoringApproaches: {
          PFC: {
            type: 'PFC',
            tier2EmissionFactor: {
              exist: false,
            },
          },
        },
      }),
    );

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl(`/permit-application/276/pfc/emission-factor/answers`));
  });
});
