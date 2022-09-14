import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlSegment, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { MockType } from '../../../../testing';
import { PermitSurrenderStore } from '../../store/permit-surrender.store';
import { JustificationGuard } from './justification.guard';

describe('JustificationGuard', () => {
  let router: Router;
  let guard: JustificationGuard;
  let store: PermitSurrenderStore;

  const tasksService: MockType<TasksService> = {};

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.url = [new UrlSegment('justification', null)];
  activatedRouteSnapshot.params = { taskId: 1 };
  activatedRouteSnapshot.data = { statusKey: 'SURRENDER_APPLY' };

  const routerStateSnapshot = {
    url: '/permit-surrender/1/apply/justification',
  } as RouterStateSnapshot;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [{ provide: TasksService, useValue: tasksService }],
    });
    guard = TestBed.inject(JustificationGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(PermitSurrenderStore);
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({} as any);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should redirect to summary when section status is completed', async () => {
    store.setState({
      ...store.getState(),
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
    ).resolves.toEqual(router.parseUrl(`/permit-surrender/1/apply/summary`));
  });

  it('should redirect to answers when surrender data are filled and status is false', async () => {
    store.setState({
      ...store.getState(),
      permitSurrender: {
        stopDate: '2012-12-12',
        justification: 'justify',
        documentsExist: false,
      },
      sectionsCompleted: {
        SURRENDER_APPLY: false,
      },
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl(`/permit-surrender/1/apply/answers`));
  });

  it('should redirect to base page when stop date data is missing', async () => {
    store.setState({
      ...store.getState(),
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

  it('should return true when landed in changing state', async () => {
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({
      extras: {
        state: {
          changing: true,
        },
      },
    } as any);

    store.setState({
      ...store.getState(),
      permitSurrender: {
        stopDate: '2012-12-12',
        justification: 'justify',
        documentsExist: false,
      },
      sectionsCompleted: {
        SURRENDER_APPLY: true,
      },
    });

    await expect(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot) as Observable<true | UrlTree>).toEqual(
      true,
    );
  });
});
