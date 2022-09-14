import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, UrlSegment, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import {
  PermitSurrenderReviewDeterminationGrant,
  PermitSurrenderReviewDeterminationReject,
  TasksService,
} from 'pmrv-api';

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

  it('should return true when status is not completed and wizard completed', async () => {
    store.setState({
      ...mockTaskState,
      reviewDeterminationCompleted: false,
      reviewDetermination: {
        type: 'REJECTED',
        reason: 'reason',
        officialRefusalLetter: 'official refusal letter',
        shouldFeeBeRefundedToOperator: true,
      } as PermitSurrenderReviewDeterminationReject,
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);
  });

  it('should return to determination when wizard and status are not completed and previous steps are filled', async () => {
    store.setState({
      ...mockTaskState,
      reviewDeterminationCompleted: false,
      reviewDetermination: {
        type: 'REJECTED',
        reason: 'reason',
      } as PermitSurrenderReviewDeterminationReject,
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<boolean | UrlTree>),
    ).resolves.toEqual(router.parseUrl(`/permit-surrender/${mockTaskState.requestTaskId}/review/determination`));
  });

  it('should return to determination when determination is not rejected', async () => {
    store.setState({
      ...mockTaskState,
      reviewDeterminationCompleted: false,
      reviewDetermination: {
        type: 'GRANTED',
        reason: 'reason',
        stopDate: '2012-12-13',
        noticeDate: '2012-12-13',
        reportRequired: false,
        allowancesSurrenderRequired: false,
      } as PermitSurrenderReviewDeterminationGrant,
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<boolean | UrlTree>),
    ).resolves.toEqual(router.parseUrl(`/permit-surrender/${mockTaskState.requestTaskId}/review/determination`));
  });

  it('should redirect to summary when status is completed and wizard completed', async () => {
    store.setState({
      ...mockTaskState,
      reviewDeterminationCompleted: true,
      reviewDetermination: {
        type: 'REJECTED',
        reason: 'reason',
        officialRefusalLetter: 'official refusal letter',
        shouldFeeBeRefundedToOperator: true,
      } as PermitSurrenderReviewDeterminationReject,
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<boolean | UrlTree>),
    ).resolves.toEqual(
      router.parseUrl(`/permit-surrender/${mockTaskState.requestTaskId}/review/determination/reject/summary`),
    );
  });
});
