import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, UrlSegment, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { MockType } from '../../../../../testing';
import { PermitSurrenderStore } from '../../../store/permit-surrender.store';
import { mockTaskState } from '../../testing/mock-state';
import { NotesGuard } from './notes.guard';

describe('NotesGuard', () => {
  let router: Router;
  let guard: NotesGuard;
  let store: PermitSurrenderStore;

  const tasksService: MockType<TasksService> = {};

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.url = [new UrlSegment('notes', null)];
  activatedRouteSnapshot.params = { taskId: mockTaskState.requestTaskId };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [{ provide: TasksService, useValue: tasksService }],
    });
    guard = TestBed.inject(NotesGuard);
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
      cessationCompleted: false,
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<boolean | UrlTree>),
    ).resolves.toEqual(router.parseUrl(`/permit-surrender/${mockTaskState.requestTaskId}/cessation/confirm/answers`));
  });

  it('should redirect to summary when status is completed and wizard completed', async () => {
    store.setState({
      ...mockTaskState,
      cessationCompleted: true,
      allowancesSurrenderRequired: true,
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<boolean | UrlTree>),
    ).resolves.toEqual(router.parseUrl(`/permit-surrender/${mockTaskState.requestTaskId}/cessation/confirm/summary`));
  });

  it('should return true when wizard and status are not completed', async () => {
    store.setState({
      ...mockTaskState,
      cessationCompleted: false,
      allowancesSurrenderRequired: true,
      cessation: {
        determinationOutcome: 'APPROVED',
        allowancesSurrenderDate: '2012-12-13',
        numberOfSurrenderAllowances: 100,
        annualReportableEmissions: 123.54,
        subsistenceFeeRefunded: true,
        noticeType: 'SATISFIED_WITH_REQUIREMENTS_COMPLIANCE',
      } as any,
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);
  });
});
