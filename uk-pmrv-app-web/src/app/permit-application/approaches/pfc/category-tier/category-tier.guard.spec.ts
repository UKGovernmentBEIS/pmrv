import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteSnapshotStub, mockClass } from '../../../../../testing';
import { PermitApplicationStore } from '../../../store/permit-application.store';
import { mockState, mockStateBuild } from '../../../testing/mock-state';
import { CategoryTierGuard } from './category-tier.guard';

describe('CategoryTierGuard', () => {
  let guard: CategoryTierGuard;
  let store: PermitApplicationStore;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [{ provide: TasksService, useValue: mockClass(TasksService) }],
    });
    store = TestBed.inject(PermitApplicationStore);
    guard = TestBed.inject(CategoryTierGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should activate source stream category view', async () => {
    store.setState(mockState);

    await expect(
      firstValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: '237', index: '0' }))),
    ).resolves.toEqual(true);

    await expect(
      firstValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: '237', index: '1' }))),
    ).resolves.toEqual(true);

    store.setState({
      ...mockStateBuild({
        monitoringApproaches: {
          PFC: {},
        },
      }),
    });
    await expect(
      firstValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: '237', index: '0' }))),
    ).resolves.toEqual(true);
  });

  it('should redirect to PFC view', async () => {
    store.setState(mockState);

    await expect(
      firstValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: '237', index: '2' }))),
    ).resolves.toEqual(TestBed.inject(Router).parseUrl('/permit-application/237/pfc'));

    store.setState({
      ...mockStateBuild({
        monitoringApproaches: {
          PFC: {},
        },
      }),
    });
    await expect(
      firstValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: '237', index: '1' }))),
    ).resolves.toEqual(TestBed.inject(Router).parseUrl('/permit-application/237/pfc'));
  });
});
