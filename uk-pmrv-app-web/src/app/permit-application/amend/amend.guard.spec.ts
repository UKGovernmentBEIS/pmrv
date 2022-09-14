import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteSnapshotStub, mockClass } from '../../../testing';
import { PermitApplicationStore } from '../store/permit-application.store';
import { mockState } from '../testing/mock-state';
import { AmendGuard } from './amend.guard';

describe('AmendGuard', () => {
  let guard: AmendGuard;
  let store: PermitApplicationStore;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [{ provide: TasksService, useValue: mockClass(TasksService) }],
    });
    store = TestBed.inject(PermitApplicationStore);
    store.setState({
      ...mockState,
      reviewGroupDecisions: {
        INSTALLATION_DETAILS: {
          type: 'OPERATOR_AMENDS_NEEDED',
          changesRequired: 'Changes required for installation details',
          notes: 'notes',
        },
        ADDITIONAL_INFORMATION: {
          type: 'OPERATOR_AMENDS_NEEDED',
          changesRequired: 'Changes required for additional information',
          notes: 'notes',
        },
      },
      permitSectionsCompleted: {
        ...mockState.permitSectionsCompleted,
        AMEND_details: [true],
      },
    });
    guard = TestBed.inject(AmendGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should not activate if section is not available for amends', async () => {
    await expect(
      firstValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: '1', section: 'fuels' }))),
    ).resolves.toEqual(TestBed.inject(Router).parseUrl('/permit-application/1'));
  });

  it('should not activate if amend task has already been completed', async () => {
    await expect(
      firstValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: '1', section: 'details' }))),
    ).resolves.toEqual(TestBed.inject(Router).parseUrl('/permit-application/1'));
  });

  it('should activate if section is available for amends and task has not been completed', async () => {
    await expect(
      firstValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: '1', section: 'additional-info' }))),
    ).resolves.toEqual(true);
  });
});
