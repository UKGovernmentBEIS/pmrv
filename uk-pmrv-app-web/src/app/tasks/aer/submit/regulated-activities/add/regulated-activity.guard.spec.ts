import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { lastValueFrom } from 'rxjs';

import { RegulatedActivityGuard } from '@tasks/aer/submit/regulated-activities/add/regulated-activity.guard';
import { mockAerApplyPayload, mockState } from '@tasks/aer/submit/testing/mock-permit-apply-action';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteSnapshotStub } from '@testing';
import { KeycloakService } from 'keycloak-angular';

describe('RegulatedActivityGuard', () => {
  let guard: RegulatedActivityGuard;
  let router: Router;
  let store: CommonTasksStore;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientTestingModule],
      providers: [KeycloakService],
    });

    guard = TestBed.inject(RegulatedActivityGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(CommonTasksStore);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should redirect to task list if no data available', async () => {
    store.setState(mockState);
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: false } } } as any);

    await expect(lastValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: 23 })))).resolves.toEqual(
      router.parseUrl('/tasks/23/aer/submit/regulated-activities'),
    );

    await expect(
      lastValueFrom(
        guard.canActivate(
          new ActivatedRouteSnapshotStub({
            taskId: 23,
            activityId: 'non-exisitng',
          }),
        ),
      ),
    ).resolves.toEqual(router.parseUrl('/tasks/23/aer/submit/regulated-activities'));
  });

  it('should activate if data exist', async () => {
    store.setState(mockState);
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: false } } } as any);

    await expect(
      lastValueFrom(
        guard.canActivate(
          new ActivatedRouteSnapshotStub({
            taskId: 23,
            streamId: mockAerApplyPayload.aer.regulatedActivities[0].id,
          }),
        ),
      ),
    ).resolves.toBeTruthy();
  });
});
