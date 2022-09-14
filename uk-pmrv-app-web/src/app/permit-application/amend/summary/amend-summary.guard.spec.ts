import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteSnapshotStub, mockClass } from '../../../../testing';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockState } from '../../testing/mock-state';
import { AmendSummaryGuard } from './amend-summary.guard';

describe('AmendSummaryGuard', () => {
  let guard: AmendSummaryGuard;
  let store: PermitApplicationStore;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [{ provide: TasksService, useValue: mockClass(TasksService) }],
    });
    store = TestBed.inject(PermitApplicationStore);
    store.setState({
      ...mockState,
      permitSectionsCompleted: {
        ...mockState.permitSectionsCompleted,
        AMEND_fuels: [true],
      },
    });
    guard = TestBed.inject(AmendSummaryGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should not activate if status is not complete', async () => {
    await expect(
      firstValueFrom(
        guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: '1', section: 'details' })),
      ),
    ).resolves.toEqual(TestBed.inject(Router).parseUrl('/permit-application/1/amend/details'));
  });

  it('should activate if status is complete', async () => {
    await expect(
      firstValueFrom(
        guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: '1', section: 'fuels' })),
      ),
    ).resolves.toEqual(true);
  });
});
