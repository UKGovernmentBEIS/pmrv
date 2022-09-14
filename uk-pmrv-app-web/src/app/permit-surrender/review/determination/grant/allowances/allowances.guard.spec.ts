import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, UrlSegment, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { PermitSurrenderReviewDeterminationGrant, TasksService } from 'pmrv-api';

import { MockType } from '../../../../../../testing';
import { PermitSurrenderStore } from '../../../../store/permit-surrender.store';
import { mockTaskState } from '../../../../testing/mock-state';
import { AllowancesGuard } from './allowances.guard';

describe('AllowancesGuard', () => {
  let router: Router;
  let guard: AllowancesGuard;
  let store: PermitSurrenderStore;

  const tasksService: MockType<TasksService> = {};

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.url = [new UrlSegment('allowances', null)];
  activatedRouteSnapshot.params = { taskId: mockTaskState.requestTaskId };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [{ provide: TasksService, useValue: tasksService }],
    });
    guard = TestBed.inject(AllowancesGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(PermitSurrenderStore);
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({} as any);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should redirect to answers when status is not completed and wizard completed', async () => {
    store.setState({
      ...mockTaskState,
      reviewDeterminationCompleted: false,
      reviewDetermination: {
        type: 'GRANTED',
        reason: 'reason',
        stopDate: '2012-12-13',
        noticeDate: '2012-12-13',
        reportRequired: true,
        reportDate: '2012-12-13',
        allowancesSurrenderRequired: false,
      } as PermitSurrenderReviewDeterminationGrant,
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<boolean | UrlTree>),
    ).resolves.toEqual(
      router.parseUrl(`/permit-surrender/${mockTaskState.requestTaskId}/review/determination/grant/answers`),
    );
  });

  it('should return true when wizard and status are not completed and previous steps are filled', async () => {
    store.setState({
      ...mockTaskState,
      reviewDeterminationCompleted: false,
      reviewDetermination: {
        type: 'GRANTED',
        reason: 'reason',
        stopDate: '2012-12-13',
        noticeDate: '2012-12-13',
        reportRequired: false,
      } as any,
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);
  });
});
