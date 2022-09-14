import { TestBed } from '@angular/core/testing';
import { Router, RouterStateSnapshot } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteSnapshotStub, mockClass } from '../../../testing';
import { PermitApplicationStore } from '../store/permit-application.store';
import { mockState } from '../testing/mock-state';
import { setStoreTask } from '../testing/set-store-task';
import { SummaryGuard } from './summary.guard';

describe('SummaryGuard', () => {
  let guard: SummaryGuard;
  let store: PermitApplicationStore;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [{ provide: TasksService, useValue: mockClass(TasksService) }],
    });
    store = TestBed.inject(PermitApplicationStore);
    store.setState(mockState);
    guard = TestBed.inject(SummaryGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should activate if status is complete', async () => {
    await expect(
      firstValueFrom(
        guard.canActivate(
          new ActivatedRouteSnapshotStub({ taskId: '1' }, undefined, {
            permitTask: 'sourceStreams',
          }),
          { url: '/permit-application/1/source-streams/summary' } as RouterStateSnapshot,
        ),
      ),
    ).resolves.toEqual(TestBed.inject(Router).parseUrl('/permit-application/1/source-streams'));

    setStoreTask('sourceStreams', undefined, [true]);

    await expect(
      firstValueFrom(
        guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: '1' }, undefined, { permitTask: 'sourceStreams' }), {
          url: '/permit-application/1/source-streams/summary',
        } as RouterStateSnapshot),
      ),
    ).resolves.toEqual(true);
  });

  it('should not activate if subtask status is not completed', async () => {
    setStoreTask('monitoringApproaches', undefined, [false], 'transferredCo2Installations');

    await expect(
      firstValueFrom(
        guard.canActivate(
          new ActivatedRouteSnapshotStub({ taskId: '1' }, undefined, {
            statusKey: 'transferredCo2Installations',
          }),
          {
            url: '/permit-application/1/transferred-co2/installations/summary',
          } as RouterStateSnapshot,
        ),
      ),
    ).resolves.toEqual(TestBed.inject(Router).parseUrl('/permit-application/1/transferred-co2/installations'));
  });

  it('should activate if subtask status is completed', async () => {
    setStoreTask('monitoringApproaches', undefined, [true], 'transferredCo2Installations');
    await expect(
      firstValueFrom(
        guard.canActivate(
          new ActivatedRouteSnapshotStub({ taskId: '1' }, undefined, {
            statusKey: 'transferredCo2Installations',
          }),
          {
            url: '/permit-application/1/transferred-co2/installations/summary',
          } as RouterStateSnapshot,
        ),
      ),
    ).resolves.toEqual(true);
  });

  it('should not activate if subtask status is not completed for given index', async () => {
    setStoreTask('monitoringApproaches', undefined, [false, true], 'N2O_Applied_Standard');

    await expect(
      firstValueFrom(
        guard.canActivate(
          new ActivatedRouteSnapshotStub({ taskId: '1', index: 0 }, undefined, {
            statusKey: 'N2O_Applied_Standard',
          }),
          {
            url: '/permit-application/1/nitrous-oxide/category-tier/0/applied-standard/summary',
          } as RouterStateSnapshot,
        ),
      ),
    ).resolves.toEqual(
      TestBed.inject(Router).parseUrl('/permit-application/1/nitrous-oxide/category-tier/0/applied-standard'),
    );
  });

  it('should activate if subtask status is completed for given index', async () => {
    setStoreTask('monitoringApproaches', undefined, [false, true], 'N2O_Applied_Standard');

    await expect(
      firstValueFrom(
        guard.canActivate(
          new ActivatedRouteSnapshotStub({ taskId: '1', index: 1 }, undefined, {
            statusKey: 'N2O_Applied_Standard',
          }),
          {
            url: '/permit-application/1/nitrous-oxide/category-tier/0/applied-standard/summary',
          } as RouterStateSnapshot,
        ),
      ),
    ).resolves.toEqual(true);
  });
});
