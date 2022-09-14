import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { lastValueFrom } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteSnapshotStub } from '../../../../testing';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockPermitApplyPayload } from '../../testing/mock-permit-apply-action';
import { mockState } from '../../testing/mock-state';
import { MeasurementDeviceDetailsGuard } from './measurement-device-details.guard';

describe('MeasurementDeviceDetailsGuard', () => {
  let guard: MeasurementDeviceDetailsGuard;
  let router: Router;
  let store: PermitApplicationStore;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [{ provide: TasksService, useValue: {} }],
    });

    guard = TestBed.inject(MeasurementDeviceDetailsGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(PermitApplicationStore);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should redirect to task list if no data available', async () => {
    store.setState(mockState);

    await expect(lastValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: 1 })))).resolves.toEqual(
      router.parseUrl('/permit-application/1/measurement-devices/summary'),
    );

    await expect(
      lastValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: 1, deviceId: 'uuid1' }))),
    ).resolves.toEqual(router.parseUrl('/permit-application/1/measurement-devices/summary'));
  });

  it('should activate if data exist', async () => {
    store.setState(mockState);

    await expect(
      lastValueFrom(
        guard.canActivate(
          new ActivatedRouteSnapshotStub({
            taskId: 1,
            deviceId: mockPermitApplyPayload.permit.measurementDevicesOrMethods[0].id,
          }),
        ),
      ),
    ).resolves.toBeTruthy();

    await expect(
      lastValueFrom(
        guard.canActivate(
          new ActivatedRouteSnapshotStub({
            taskId: 1,
            deviceId: 'notexisting',
          }),
        ),
      ),
    ).resolves.toEqual(router.parseUrl('/permit-application/1/measurement-devices/summary'));
  });
});
