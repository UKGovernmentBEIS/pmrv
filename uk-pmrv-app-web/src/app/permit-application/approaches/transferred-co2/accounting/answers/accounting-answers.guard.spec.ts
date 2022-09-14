import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom } from 'rxjs';

import { AccountingEmissionsDetails, TasksService } from 'pmrv-api';

import { ActivatedRouteSnapshotStub, mockClass } from '../../../../../../testing';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { mockState, mockStateBuild } from '../../../../testing/mock-state';
import { AccountingAnswersGuard } from './accounting-answers.guard';

describe('AccountingAnswersGuard', () => {
  let guard: AccountingAnswersGuard;
  let store: PermitApplicationStore;
  let router: Router;

  function getMockAccountingEmissionsDetails(
    tier: AccountingEmissionsDetails['tier'],
    high: boolean,
  ): AccountingEmissionsDetails {
    return {
      measurementDevicesOrMethods: ['16236817394240.1574963093314700'],
      samplingFrequency: 'DAILY',
      tier: tier,
      isHighestRequiredTier: high,
    };
  }

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [{ provide: TasksService, useValue: mockClass(TasksService) }],
    });
    store = TestBed.inject(PermitApplicationStore);
    guard = TestBed.inject(AccountingAnswersGuard);
    router = TestBed.inject(Router);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should activate accounting answers view', async () => {
    store.setState(
      mockStateBuild(undefined, {
        applyStatusesCompleted: {
          TRANSFERRED_CO2_Accounting: [true],
        },
      }),
    );

    await expect(firstValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: '237' })))).resolves.toEqual(
      true,
    );

    store.setState({
      ...mockStateBuild({
        monitoringApproaches: {
          TRANSFERRED_CO2: {
            type: 'TRANSFERRED_CO2',
            accountingEmissions: {
              chemicallyBound: false,
            },
          },
        },
      }),
    });
    const wizardUrl = '/permit-application/237/transferred-co2/accounting';

    await expect(firstValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: '237' })))).resolves.toEqual(
      router.parseUrl(wizardUrl),
    );

    store.setState(
      mockStateBuild({
        monitoringApproaches: {
          TRANSFERRED_CO2: {
            accountingEmissions: {
              chemicallyBound: true,
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

    store.setState(
      mockStateBuild({
        monitoringApproaches: {
          TRANSFERRED_CO2: {
            accountingEmissions: {
              chemicallyBound: false,
              accountingEmissionsDetails: {
                ...getMockAccountingEmissionsDetails('NO_TIER', undefined),
                noTierJustification: 'explanation',
              },
            },
          },
        },
      }),
    );

    await expect(firstValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: '237' })))).resolves.toEqual(
      true,
    );

    store.setState(
      mockStateBuild({
        monitoringApproaches: {
          TRANSFERRED_CO2: {
            accountingEmissions: {
              chemicallyBound: false,
              accountingEmissionsDetails: {
                ...getMockAccountingEmissionsDetails('TIER_3', false),
                noHighestRequiredTierJustification: {
                  isCostUnreasonable: true,
                  isTechnicallyInfeasible: true,
                  technicalInfeasibilityExplanation: 'technicalInfeasibilityExplanation',
                },
              },
            },
          },
        },
      }),
    );

    await expect(firstValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: '237' })))).resolves.toEqual(
      true,
    );

    store.setState(
      mockStateBuild({
        monitoringApproaches: {
          TRANSFERRED_CO2: {
            accountingEmissions: {
              chemicallyBound: false,
              accountingEmissionsDetails: {
                ...getMockAccountingEmissionsDetails('TIER_3', false),
                noHighestRequiredTierJustification: { isCostUnreasonable: true },
              },
            },
          },
        },
      }),
    );

    await expect(firstValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: '237' })))).resolves.toEqual(
      true,
    );

    store.setState(
      mockStateBuild({
        monitoringApproaches: {
          TRANSFERRED_CO2: {
            accountingEmissions: {
              chemicallyBound: false,
              accountingEmissionsDetails: {
                ...getMockAccountingEmissionsDetails('TIER_2', false),
                noHighestRequiredTierJustification: { isCostUnreasonable: true },
              },
            },
          },
        },
      }),
    );

    await expect(firstValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: '237' })))).resolves.toEqual(
      true,
    );

    store.setState(
      mockStateBuild({
        monitoringApproaches: {
          TRANSFERRED_CO2: {
            accountingEmissions: {
              chemicallyBound: false,
              accountingEmissionsDetails: {
                ...getMockAccountingEmissionsDetails('TIER_1', false),
                noHighestRequiredTierJustification: { isCostUnreasonable: true },
              },
            },
          },
        },
      }),
    );

    await expect(firstValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: '237' })))).resolves.toEqual(
      true,
    );

    store.setState({
      ...mockStateBuild({
        monitoringApproaches: {
          TRANSFERRED_CO2: {
            type: 'TRANSFERRED_CO2',
            accountingEmissions: {
              chemicallyBound: false,
              accountingEmissionsDetails: getMockAccountingEmissionsDetails('TIER_1', false),
            },
          },
        },
      }),
    });

    await expect(firstValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: '237' })))).resolves.toEqual(
      router.parseUrl(wizardUrl),
    );

    store.setState({
      ...mockStateBuild({
        monitoringApproaches: {
          TRANSFERRED_CO2: {
            type: 'TRANSFERRED_CO2',
            accountingEmissions: {
              chemicallyBound: false,
              accountingEmissionsDetails: getMockAccountingEmissionsDetails('TIER_2', false),
            },
          },
        },
      }),
    });

    await expect(firstValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: '237' })))).resolves.toEqual(
      router.parseUrl(wizardUrl),
    );

    store.setState({
      ...mockStateBuild({
        monitoringApproaches: {
          TRANSFERRED_CO2: {
            type: 'TRANSFERRED_CO2',
            accountingEmissions: {
              chemicallyBound: false,
              accountingEmissionsDetails: getMockAccountingEmissionsDetails('TIER_3', false),
            },
          },
        },
      }),
    });

    await expect(firstValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: '237' })))).resolves.toEqual(
      router.parseUrl(wizardUrl),
    );
  });

  it('should redirect to accounting view', async () => {
    store.setState({
      ...mockStateBuild({
        monitoringApproaches: {
          TRANSFERRED_CO2: {
            type: 'TRANSFERRED_CO2',
          },
        },
        measurementDevicesOrMethods: [],
      }),
    });
    await expect(firstValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: '237' })))).resolves.toEqual(
      TestBed.inject(Router).parseUrl('/permit-application/237/transferred-co2/accounting'),
    );

    store.setState({
      ...mockStateBuild({
        monitoringApproaches: {
          TRANSFERRED_CO2: {
            type: 'TRANSFERRED_CO2',
          },
        },
      }),
      permitSectionsCompleted: {
        measurementDevicesOrMethods: [true],
      },
    });
    await expect(firstValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: '237' })))).resolves.toEqual(
      TestBed.inject(Router).parseUrl('/permit-application/237/transferred-co2/accounting'),
    );
  });
});
