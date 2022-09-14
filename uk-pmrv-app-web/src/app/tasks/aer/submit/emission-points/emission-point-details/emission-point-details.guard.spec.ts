import { HttpClient, HttpHandler } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { lastValueFrom } from 'rxjs';

import { EmissionPointDetailsGuard } from '@tasks/aer/submit/emission-points/emission-point-details/emission-point-details.guard';
import { mockAerApplyPayload, mockState } from '@tasks/aer/submit/testing/mock-permit-apply-action';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteSnapshotStub } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

describe('EmissionPointDetailsGuard', () => {
  let guard: EmissionPointDetailsGuard;
  let router: Router;
  let store: CommonTasksStore;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [KeycloakService, HttpClient, HttpHandler, { provide: TasksService, useValue: {} }],
    });

    guard = TestBed.inject(EmissionPointDetailsGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(CommonTasksStore);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should redirect to task list if no data available', async () => {
    store.setState(mockState);
    await expect(lastValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: 23 })))).resolves.toEqual(
      router.parseUrl('/tasks/23/aer/submit/emission-points'),
    );

    await expect(
      lastValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: 23, emissionPointId: 'notexisting' }))),
    ).resolves.toEqual(router.parseUrl('/tasks/23/aer/submit/emission-points'));
  });

  it('should activate if data exist', async () => {
    store.setState(mockState);

    await expect(
      lastValueFrom(
        guard.canActivate(
          new ActivatedRouteSnapshotStub({
            taskId: 23,
            emissionPointId: mockAerApplyPayload.aer.emissionPoints[0].id,
          }),
        ),
      ),
    ).resolves.toBeTruthy();

    await expect(
      lastValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: 23, emissionPointId: 'notexisting' }))),
    ).resolves.toEqual(router.parseUrl('/tasks/23/aer/submit/emission-points'));
  });
});