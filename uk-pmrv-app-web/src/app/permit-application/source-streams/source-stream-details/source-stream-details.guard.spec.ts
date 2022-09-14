import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { lastValueFrom } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteSnapshotStub } from '../../../../testing';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockPermitApplyPayload } from '../../testing/mock-permit-apply-action';
import { mockState } from '../../testing/mock-state';
import { SourceStreamDetailsGuard } from './source-stream-details.guard';

describe('SourceStreamDetailsGuard', () => {
  let guard: SourceStreamDetailsGuard;
  let router: Router;
  let store: PermitApplicationStore;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [{ provide: TasksService, useValue: {} }],
    });

    guard = TestBed.inject(SourceStreamDetailsGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(PermitApplicationStore);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should redirect to task list if no data available', async () => {
    store.setState(mockState);

    await expect(lastValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: 23 })))).resolves.toEqual(
      router.parseUrl('/permit-application/23/source-streams/summary'),
    );

    await expect(
      lastValueFrom(
        guard.canActivate(
          new ActivatedRouteSnapshotStub({
            taskId: 23,
            streamId: 'nonexisitng',
          }),
        ),
      ),
    ).resolves.toEqual(router.parseUrl('/permit-application/23/source-streams/summary'));
  });

  it('should activate if data exist', async () => {
    store.setState(mockState);

    await expect(
      lastValueFrom(
        guard.canActivate(
          new ActivatedRouteSnapshotStub({
            taskId: 23,
            streamId: mockPermitApplyPayload.permit.sourceStreams[0].id,
          }),
        ),
      ),
    ).resolves.toBeTruthy();

    await expect(
      lastValueFrom(
        guard.canActivate(
          new ActivatedRouteSnapshotStub({
            taskId: 23,
            streamId: 'notexisting',
          }),
        ),
      ),
    ).resolves.toEqual(router.parseUrl('/permit-application/23/source-streams/summary'));
  });
});
