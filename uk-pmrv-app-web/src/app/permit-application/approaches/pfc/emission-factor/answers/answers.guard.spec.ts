import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteSnapshotStub, mockClass } from '../../../../../../testing';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { mockState, mockStateBuild } from '../../../../testing/mock-state';
import { AnswersGuard } from './answers.guard';

describe('EmissionFactorAnswersGuard', () => {
  let guard: AnswersGuard;
  let store: PermitApplicationStore;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [{ provide: TasksService, useValue: mockClass(TasksService) }],
    });
    store = TestBed.inject(PermitApplicationStore);
    guard = TestBed.inject(AnswersGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should activate tier2 emission factor answers view', async () => {
    store.setState(
      mockStateBuild({
        monitoringApproaches: {
          PFC: {
            tier2EmissionFactor: {
              exist: false,
            },
          },
        },
      }),
    );

    await expect(firstValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: '237' })))).resolves.toEqual(
      true,
    );

    store.setState(mockState);

    await expect(firstValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: '237' })))).resolves.toEqual(
      true,
    );
  });

  it('should redirect to tier2 emission factor summary view', async () => {
    store.setState(
      mockStateBuild(mockState, {
        PFC_Tier2EmissionFactor: [true],
      }),
    );

    await expect(firstValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: '237' })))).resolves.toEqual(
      TestBed.inject(Router).parseUrl('/permit-application/237/pfc/emission-factor/summary'),
    );
  });

  it('should redirect to tier2 emission factor view', async () => {
    store.setState({
      ...mockStateBuild({
        monitoringApproaches: {
          PFC: {
            tier2EmissionFactor: {
              exist: true,
            },
          },
        },
      }),
    });
    await expect(firstValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: '237' })))).resolves.toEqual(
      TestBed.inject(Router).parseUrl('/permit-application/237/pfc/emission-factor'),
    );

    store.setState({
      ...mockStateBuild({
        monitoringApproaches: {
          PFC: {
            tier2EmissionFactor: {
              exist: true,
              determinationInstallation: {
                procedureDescription: 'procedureDescription',
                procedureDocumentName: 'procedureDocumentName',
                procedureReference: 'procedureReference',
                diagramReference: 'diagramReference',
                responsibleDepartmentOrRole: 'responsibleDepartmentOrRole',
                locationOfRecords: 'locationOfRecords',
                itSystemUsed: 'itSystemUsed',
                appliedStandards: 'appliedStandards',
              },
            },
          },
        },
      }),
    });
    await expect(firstValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: '237' })))).resolves.toEqual(
      TestBed.inject(Router).parseUrl('/permit-application/237/pfc/emission-factor'),
    );

    store.setState({
      ...mockStateBuild({
        monitoringApproaches: {
          PFC: {
            tier2EmissionFactor: {
              exist: true,
              scheduleMeasurements: {
                procedureDescription: 'procedureDescription',
                procedureDocumentName: 'procedureDocumentName',
                procedureReference: 'procedureReference',
                diagramReference: 'diagramReference',
                responsibleDepartmentOrRole: 'responsibleDepartmentOrRole',
                locationOfRecords: 'locationOfRecords',
                itSystemUsed: 'itSystemUsed',
                appliedStandards: 'appliedStandards',
              },
            },
          },
        },
      }),
    });
    await expect(firstValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: '237' })))).resolves.toEqual(
      TestBed.inject(Router).parseUrl('/permit-application/237/pfc/emission-factor'),
    );

    store.setState({
      ...mockStateBuild({
        monitoringApproaches: {
          PFC: {
            tier2EmissionFactor: {},
          },
        },
      }),
    });
    await expect(firstValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: '237' })))).resolves.toEqual(
      TestBed.inject(Router).parseUrl('/permit-application/237/pfc/emission-factor'),
    );

    store.setState({
      ...mockStateBuild({
        monitoringApproaches: {
          PFC: {},
        },
      }),
    });
    await expect(firstValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: '237' })))).resolves.toEqual(
      TestBed.inject(Router).parseUrl('/permit-application/237/pfc/emission-factor'),
    );
  });
});
