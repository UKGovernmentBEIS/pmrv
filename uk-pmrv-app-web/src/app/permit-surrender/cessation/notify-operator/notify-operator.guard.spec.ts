import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, UrlSegment, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { MockType } from '../../../../testing';
import { PermitSurrenderStore } from '../../store/permit-surrender.store';
import { mockTaskState } from '../testing/mock-state';
import { NotifyOperatorGuard } from './notify-operator.guard';

describe('NotifyOperatorGuard', () => {
  let router: Router;
  let guard: NotifyOperatorGuard;
  let store: PermitSurrenderStore;

  const tasksService: MockType<TasksService> = {};

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.url = [new UrlSegment('allowances-date', null)];
  activatedRouteSnapshot.params = { taskId: mockTaskState.requestTaskId };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [{ provide: TasksService, useValue: tasksService }],
    });
    guard = TestBed.inject(NotifyOperatorGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(PermitSurrenderStore);
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({} as any);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should redirect to parent page when cessation is not completed', async () => {
    store.setState({
      ...mockTaskState,
      cessationCompleted: false,
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<boolean | UrlTree>),
    ).resolves.toEqual(router.parseUrl(`/permit-surrender/${mockTaskState.requestTaskId}/cessation`));
  });

  it('should redirect to parent page when allowed actions do not include the notify operator request task action', async () => {
    store.setState({
      ...mockTaskState,
      allowedRequestTaskActions: [],
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<boolean | UrlTree>),
    ).resolves.toEqual(router.parseUrl(`/permit-surrender/${mockTaskState.requestTaskId}/cessation`));
  });

  it('should return true when completed and notify action is included', async () => {
    store.setState({
      ...mockTaskState,
      cessationCompleted: true,
      allowedRequestTaskActions: ['PERMIT_SURRENDER_CESSATION_NOTIFY_OPERATOR_FOR_DECISION'],
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);
  });
});
