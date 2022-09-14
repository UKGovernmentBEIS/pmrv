import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { lastValueFrom } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteSnapshotStub } from '../../../../testing';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockState } from '../../testing/mock-state';
import { EmissionSummaryDetailsGuard } from './emission-summary-details.guard';

describe('EmissionSummaryDetailsGuard', () => {
  let guard: EmissionSummaryDetailsGuard;
  let router: Router;
  let store: PermitApplicationStore;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [{ provide: TasksService, useValue: {} }],
    });

    guard = TestBed.inject(EmissionSummaryDetailsGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(PermitApplicationStore);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should redirect to task list if no data available', async () => {
    store.setState(mockState);

    await expect(lastValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: 23 })))).resolves.toEqual(
      router.parseUrl('/permit-application/23/emission-summaries/summary'),
    );

    await expect(
      lastValueFrom(
        guard.canActivate(
          new ActivatedRouteSnapshotStub({
            taskId: 23,
            emissionSummaryIndex: 'unknown',
          }),
        ),
      ),
    ).resolves.toEqual(router.parseUrl('/permit-application/23/emission-summaries/summary'));
  });

  it('should activate if data exist', async () => {
    store.setState(mockState);

    await expect(
      lastValueFrom(
        guard.canActivate(
          new ActivatedRouteSnapshotStub({
            taskId: 23,
            emissionSummaryIndex: 0,
          }),
        ),
      ),
    ).resolves.toBeTruthy();
    await expect(
      lastValueFrom(
        guard.canActivate(
          new ActivatedRouteSnapshotStub({
            taskId: 23,
            emissionSummaryIndex: 15,
          }),
        ),
      ),
    ).resolves.toEqual(router.parseUrl('/permit-application/23/emission-summaries/summary'));
  });
});
