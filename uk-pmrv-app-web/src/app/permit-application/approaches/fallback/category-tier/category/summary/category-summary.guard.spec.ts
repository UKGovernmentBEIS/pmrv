import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteSnapshotStub, mockClass } from '../../../../../../../testing';
import { PermitApplicationStore } from '../../../../../store/permit-application.store';
import { mockState, mockStateBuild } from '../../../../../testing/mock-state';
import { CategorySummaryGuard } from './category-summary.guard';

describe('CategorySummaryGuard', () => {
  let guard: CategorySummaryGuard;
  let store: PermitApplicationStore;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [{ provide: TasksService, useValue: mockClass(TasksService) }],
    });
    store = TestBed.inject(PermitApplicationStore);
    guard = TestBed.inject(CategorySummaryGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should activate source stream category summary', async () => {
    store.setState(
      mockStateBuild(mockState, {
        FALLBACK_Category: [true],
        measurementDevicesOrMethods: [true],
        emissionSources: [true],
        sourceStreams: [true],
      }),
    );

    await expect(
      firstValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: '237', index: '0' }))),
    ).resolves.toEqual(true);
  });

  it('should activate source stream category summary', async () => {
    store.setState(
      mockStateBuild(mockState, {
        FALLBACK_Category: [true],
        measurementDevicesOrMethods: [true],
        emissionSources: [false],
        sourceStreams: [true],
      }),
    );

    await expect(
      firstValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: '237', index: '0' }))),
    ).resolves.toEqual(true);
  });

  it('should activate source stream category summary', async () => {
    store.setState(
      mockStateBuild(mockState, {
        FALLBACK_Category: [true],
        measurementDevicesOrMethods: [false],
        emissionSources: [true],
        sourceStreams: [true],
      }),
    );

    await expect(
      firstValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: '237', index: '0' }))),
    ).resolves.toEqual(true);
  });

  it('should activate source stream category summary', async () => {
    store.setState(
      mockStateBuild(mockState, {
        FALLBACK_Category: [true],
        measurementDevicesOrMethods: [true],
        emissionSources: [true],
        sourceStreams: [false],
      }),
    );

    await expect(
      firstValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: '237', index: '0' }))),
    ).resolves.toEqual(true);
  });

  it('should redirect to fallback source stream category view', async () => {
    store.setState(mockStateBuild(mockState));

    await expect(
      firstValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: '237', index: '0' }))),
    ).resolves.toEqual(TestBed.inject(Router).parseUrl('/permit-application/237/fall-back/category-tier/0/category'));
  });
});
