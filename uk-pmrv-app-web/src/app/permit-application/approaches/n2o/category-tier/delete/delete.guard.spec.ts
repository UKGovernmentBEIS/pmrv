import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteSnapshotStub, mockClass } from '../../../../../../testing';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { mockState, mockStateBuild } from '../../../../testing/mock-state';
import { DeleteGuard } from './delete.guard';

describe('DeleteGuard', () => {
  let guard: DeleteGuard;
  let store: PermitApplicationStore;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [{ provide: TasksService, useValue: mockClass(TasksService) }],
    });
    store = TestBed.inject(PermitApplicationStore);
    guard = TestBed.inject(DeleteGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should activate delete source stream category', async () => {
    store.setState(mockState);

    await expect(
      firstValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: '237', index: '0' }))),
    ).resolves.toEqual(true);
  });

  it('should redirect to N2O view', async () => {
    store.setState(mockState);

    await expect(
      firstValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: '237', index: '1' }))),
    ).resolves.toEqual(TestBed.inject(Router).parseUrl('/permit-application/237/nitrous-oxide'));

    store.setState({
      ...mockStateBuild({
        monitoringApproaches: {
          N2O: {},
        },
      }),
    });
    await expect(
      firstValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: '237', index: '0' }))),
    ).resolves.toEqual(TestBed.inject(Router).parseUrl('/permit-application/237/nitrous-oxide'));
  });
});
