import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlSegment, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { mockClass } from '../../../../../testing';
import { PermitApplicationStore } from '../../../store/permit-application.store';
import { mockStateBuild } from '../../../testing/mock-state';
import { UploadFileGuard } from './upload-file.guard';

describe('UploadFileGuard', () => {
  let router: Router;
  let guard: UploadFileGuard;
  let store: PermitApplicationStore;

  const tasksService = mockClass(TasksService);
  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.url = [new UrlSegment('upload-file', null)];
  const routerStateSnapshot = {
    url: '/permit-application/276/uncertainty-analysis/upload-file',
  } as RouterStateSnapshot;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [{ provide: TasksService, useValue: tasksService }],
    });
    guard = TestBed.inject(UploadFileGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(PermitApplicationStore);
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({} as any);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should redirect to summary if status is completed', async () => {
    store.setState(
      mockStateBuild(
        {
          uncertaintyAnalysis: { exist: true, attachments: ['e227ea8a-778b-4208-9545-e108ea66c114'] },
        },
        { uncertaintyAnalysis: [true] },
      ),
    );

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl('/permit-application/276/uncertainty-analysis/summary'));
  });

  it('should redirect to uncertainty analysis if status is not started', async () => {
    store.setState(
      mockStateBuild(
        {
          uncertaintyAnalysis: {},
        },
        {},
      ),
    );

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl('/permit-application/276/uncertainty-analysis'));
  });

  it('should allow if status is in progress and no files are selected', async () => {
    store.setState(
      mockStateBuild(
        {
          uncertaintyAnalysis: { exist: true },
        },
        {},
      ),
    );

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);
  });

  it('should redirect to answers if status is in progress and files are selected', async () => {
    store.setState(
      mockStateBuild(
        {
          uncertaintyAnalysis: { exist: true, attachments: ['e227ea8a-778b-4208-9545-e108ea66c114'] },
        },
        {},
      ),
    );

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl('/permit-application/276/uncertainty-analysis/answers'));
  });

  it('should redirect to answers if status is in progress and selection is No', async () => {
    store.setState(
      mockStateBuild(
        {
          uncertaintyAnalysis: { exist: false },
        },
        {},
      ),
    );

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl('/permit-application/276/uncertainty-analysis/answers'));
  });
});
