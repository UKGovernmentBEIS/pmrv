import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { lastValueFrom } from 'rxjs';

import { mockAerApplyPayload, mockState } from '@tasks/aer/submit/testing/mock-permit-apply-action';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteSnapshotStub } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { SourceStreamDetailsGuard } from './source-stream-details.guard';

describe('SourceStreamDetailsGuard', () => {
  let guard: SourceStreamDetailsGuard;
  let router: Router;
  let store: CommonTasksStore;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientTestingModule],
      providers: [KeycloakService],
    });

    guard = TestBed.inject(SourceStreamDetailsGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(CommonTasksStore);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should redirect to task list if no data available', async () => {
    store.setState(mockState);

    await expect(lastValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: 23 })))).resolves.toEqual(
      router.parseUrl('/tasks/23/aer/submit/source-streams'),
    );

    await expect(
      lastValueFrom(
        guard.canActivate(
          new ActivatedRouteSnapshotStub({
            taskId: 23,
            streamId: 'non-exisitng',
          }),
        ),
      ),
    ).resolves.toEqual(router.parseUrl('/tasks/23/aer/submit/source-streams'));
  });

  it('should activate if data exist', async () => {
    store.setState(mockState);

    await expect(
      lastValueFrom(
        guard.canActivate(
          new ActivatedRouteSnapshotStub({
            taskId: 23,
            streamId: mockAerApplyPayload.aer.sourceStreams[0].id,
          }),
        ),
      ),
    ).resolves.toBeTruthy();

    await expect(
      lastValueFrom(
        guard.canActivate(
          new ActivatedRouteSnapshotStub({
            taskId: 23,
            streamId: 'non-existing',
          }),
        ),
      ),
    ).resolves.toEqual(router.parseUrl('/tasks/23/aer/submit/source-streams'));
  });
});
