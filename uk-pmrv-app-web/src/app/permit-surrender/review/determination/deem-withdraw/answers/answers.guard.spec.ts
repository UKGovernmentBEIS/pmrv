import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, UrlSegment, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { MockType } from '../../../../../../testing';
import { PermitSurrenderStore } from '../../../../store/permit-surrender.store';
import { mockTaskState } from '../../../../testing/mock-state';
import { AnswersGuard } from './answers.guard';

describe('AnswersGuard', () => {
  let router: Router;
  let guard: AnswersGuard;
  let store: PermitSurrenderStore;

  const tasksService: MockType<TasksService> = {};

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.url = [new UrlSegment('answers', null)];
  activatedRouteSnapshot.params = { taskId: mockTaskState.requestTaskId };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [{ provide: TasksService, useValue: tasksService }],
    });
    guard = TestBed.inject(AnswersGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(PermitSurrenderStore);
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({} as any);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should redirect to summary when status is completed', async () => {
    store.setState({
      ...mockTaskState,
      reviewDetermination: {
        type: 'DEEMED_WITHDRAWN',
        reason: 'reason',
      },
      reviewDeterminationCompleted: true,
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<boolean | UrlTree>),
    ).resolves.toEqual(
      router.parseUrl(`/permit-surrender/${mockTaskState.requestTaskId}/review/determination/deem-withdraw/summary`),
    );
  });

  it('should redirect to first step when type is missing', async () => {
    store.setState({
      ...mockTaskState,
      reviewDeterminationCompleted: false,
      reviewDetermination: {
        type: undefined,
      } as any,
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<boolean | UrlTree>),
    ).resolves.toEqual(router.parseUrl(`/permit-surrender/${mockTaskState.requestTaskId}/review/determination`));
  });

  it('should return true when wizard and status are not completed and all steps are filled', async () => {
    store.setState({
      ...mockTaskState,
      reviewDeterminationCompleted: false,
      reviewDetermination: {
        type: 'DEEMED_WITHDRAWN',
        reason: 'reason',
      },
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);
  });
});
