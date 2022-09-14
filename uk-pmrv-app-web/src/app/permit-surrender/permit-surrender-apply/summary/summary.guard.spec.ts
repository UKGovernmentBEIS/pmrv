import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlSegment, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { MockType } from '../../../../testing';
import { PermitSurrenderStore } from '../../store/permit-surrender.store';
import { SummaryGuard } from './summary.guard';

describe('SummaryGuard', () => {
  let router: Router;
  let guard: SummaryGuard;
  let store: PermitSurrenderStore;

  const tasksService: MockType<TasksService> = {};

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.url = [new UrlSegment('summary', null)];
  activatedRouteSnapshot.params = { taskId: 1 };
  activatedRouteSnapshot.data = { statusKey: 'SURRENDER_APPLY' };

  const routerStateSnapshot = {
    url: '/permit-surrender/1/apply/summary',
  } as RouterStateSnapshot;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [{ provide: TasksService, useValue: tasksService }],
    });
    guard = TestBed.inject(SummaryGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(PermitSurrenderStore);
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({} as any);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should return true when section status is completed', async () => {
    store.setState({
      ...store.getState(),
      isEditable: true,
      permitSurrender: {
        stopDate: '2012-12-12',
        justification: 'justify',
        documentsExist: false,
      },
      sectionsCompleted: {
        SURRENDER_APPLY: true,
      },
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);
  });

  it('should redirect to base page when some data are missing', async () => {
    store.setState({
      ...store.getState(),
      isEditable: true,
      permitSurrender: {
        stopDate: null,
        justification: undefined,
        documentsExist: undefined,
      },
      sectionsCompleted: {},
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl(`/permit-surrender/1/apply`));
  });

  it('should return true when in non editable mode', async () => {
    store.setState({
      ...store.getState(),
      isEditable: false,
      permitSurrender: {
        stopDate: '2012-12-12',
        justification: undefined,
        documentsExist: undefined,
      },
      sectionsCompleted: {
        SURRENDER_APPLY: false,
      },
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);
  });
});
