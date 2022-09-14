import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { lastValueFrom } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteSnapshotStub } from '../../../../../../testing';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { mockState } from '../../../../testing/mock-state';
import { DetailsGuard } from './details.guard';

describe('InstallationDetailsGuard', () => {
  let guard: DetailsGuard;
  let router: Router;
  let store: PermitApplicationStore;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [{ provide: TasksService, useValue: {} }],
    });

    guard = TestBed.inject(DetailsGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(PermitApplicationStore);
    store.setState(mockState);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should redirect to installation list if installation index is missing', async () => {
    await expect(lastValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: 279 })))).resolves.toEqual(
      router.parseUrl('/permit-application/279/transferred-co2/installations/summary'),
    );
  });

  it('should redirect to installation list if installation index is not valid', async () => {
    await expect(
      lastValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: 279, index: 'unknown' }))),
    ).resolves.toEqual(router.parseUrl('/permit-application/279/transferred-co2/installations/summary'));
  });

  it('should activate if installation index exist', async () => {
    await expect(
      lastValueFrom(
        guard.canActivate(
          new ActivatedRouteSnapshotStub({
            taskId: 279,
            index: 0,
          }),
        ),
      ),
    ).resolves.toBeTruthy();
  });
});
