import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom } from 'rxjs';

import { mockClass } from '@testing';

import { TasksService } from 'pmrv-api';

import { n2oCategoryTierStatuses, n2oStaticStatuses } from '../../approaches/n2o/n2o-status';
import { transferredCo2Statuses } from '../../approaches/transferred-co2/transferred-co2-status';
import { initialState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockState, mockStateBuild } from '../../testing/mock-state';
import { TaskStatusPipe } from './task-status.pipe';

describe('TaskStatusPipe', () => {
  let pipe: TaskStatusPipe;
  let store: PermitApplicationStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      declarations: [TaskStatusPipe],
      providers: [{ provide: TasksService, useValue: mockClass(TasksService) }],
    });

    store = TestBed.inject(PermitApplicationStore);
    store.setState({ ...initialState, isRequestTask: true });
  });

  beforeEach(() => (pipe = new TaskStatusPipe(store)));

  it('should create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should resolve statuses as completed if request action', async () => {
    store.setState({ ...store.getState(), isRequestTask: false });
    await expect(firstValueFrom(pipe.transform('sourceStreams'))).resolves.toEqual('complete');
    await expect(firstValueFrom(pipe.transform('emissionSources'))).resolves.toEqual('complete');
    await expect(firstValueFrom(pipe.transform('assignmentOfResponsibilities'))).resolves.toEqual('complete');
    await expect(firstValueFrom(pipe.transform('environmentalPermitsAndLicences'))).resolves.toEqual('complete');
    await expect(firstValueFrom(pipe.transform('assignmentOfResponsibilities'))).resolves.toEqual('complete');
    await expect(firstValueFrom(pipe.transform('environmentalPermitsAndLicences'))).resolves.toEqual('complete');
  });

  it('should resolve statuses', async () => {
    await expect(firstValueFrom(pipe.transform('sourceStreams'))).resolves.toEqual('not started');
    await expect(firstValueFrom(pipe.transform('emissionSources'))).resolves.toEqual('not started');
    await expect(firstValueFrom(pipe.transform('assignmentOfResponsibilities'))).resolves.toEqual('not started');
    await expect(firstValueFrom(pipe.transform('environmentalPermitsAndLicences'))).resolves.toEqual('not started');

    store.setState(mockState);

    await expect(firstValueFrom(pipe.transform('assignmentOfResponsibilities'))).resolves.toEqual('complete');
    await expect(firstValueFrom(pipe.transform('environmentalPermitsAndLicences'))).resolves.toEqual('complete');
  });

  it('should calculate the permit type', async () => {
    store.setState(mockState);

    await expect(firstValueFrom(pipe.transform('permitType'))).resolves.toEqual('complete');

    store.setState({ ...store.getState(), permitType: undefined });

    await expect(firstValueFrom(pipe.transform('permitType'))).resolves.toEqual('not started');
  });

  it('should calculate the source streams status', async () => {
    store.setState(mockState);

    await expect(firstValueFrom(pipe.transform('sourceStreams'))).resolves.toEqual('in progress');

    store.setState({ ...store.getState(), permit: { ...store.permit, sourceStreams: [] } });

    await expect(firstValueFrom(pipe.transform('sourceStreams'))).resolves.toEqual('not started');

    store.setState({
      ...store.getState(),
      permitSectionsCompleted: { ...store.payload.permitSectionsCompleted, sourceStreams: [true] },
    });

    await expect(firstValueFrom(pipe.transform('sourceStreams'))).resolves.toEqual('complete');
  });

  it('should calculate the emission summaries status', async () => {
    store.setState(mockState);

    await expect(firstValueFrom(pipe.transform('emissionSummaries'))).resolves.toEqual('in progress');

    store.setState({ ...store.getState(), permit: { ...store.permit, emissionPoints: [] } });

    await expect(firstValueFrom(pipe.transform('emissionSummaries'))).resolves.toEqual('needs review');

    store.setState({ ...store.getState(), permit: { ...store.permit, emissionSummaries: [] } });

    await expect(firstValueFrom(pipe.transform('emissionSummaries'))).resolves.toEqual('cannot start yet');

    store.setState({
      ...store.getState(),
      permitSectionsCompleted: {
        ...store.payload.permitSectionsCompleted,
        sourceStreams: [true],
        emissionPoints: [true],
        emissionSources: [true],
        regulatedActivities: [true],
      },
    });

    await expect(firstValueFrom(pipe.transform('emissionSummaries'))).resolves.toEqual('not started');
  });

  it('should calculate the transferred co2 main task status', async () => {
    store.setState({
      ...mockStateBuild({
        monitoringApproaches: {
          TRANSFERRED_CO2: {
            type: 'TRANSFERRED_CO2',
          },
        },
      }),
    });
    await expect(firstValueFrom(pipe.transform('TRANSFERRED_CO2'))).resolves.toEqual('not started');

    store.setState({
      ...mockState,
      permitSectionsCompleted: {
        TRANSFERRED_CO2_Description: [true],
      },
    });

    await expect(firstValueFrom(pipe.transform('TRANSFERRED_CO2'))).resolves.toEqual('in progress');

    store.setState({
      ...mockState,
      permitSectionsCompleted: transferredCo2Statuses.reduce((result, item) => ({ ...result, [item]: [true] }), {}),
    });

    await expect(firstValueFrom(pipe.transform('TRANSFERRED_CO2'))).resolves.toEqual('complete');

    store.setState({
      ...mockStateBuild({
        measurementDevicesOrMethods: null,
      }),
      permitSectionsCompleted: transferredCo2Statuses.reduce((result, item) => ({ ...result, [item]: [true] }), {}),
    });
    await expect(firstValueFrom(pipe.transform('TRANSFERRED_CO2'))).resolves.toEqual('needs review');
  });

  it('should calculate the n2o main task status', async () => {
    store.setState({
      ...store.getState(),
      permit: {
        ...store.getState().permit,
        monitoringApproaches: {
          N2O: {},
        },
      },
    });

    await expect(firstValueFrom(pipe.transform('N2O'))).resolves.toEqual('not started');

    store.setState({
      ...store.getState(),
      permitSectionsCompleted: {
        N2O_Description: [true],
      },
    });

    await expect(firstValueFrom(pipe.transform('N2O'))).resolves.toEqual('in progress');

    store.setState({
      ...mockState,
      permitSectionsCompleted: {
        ...n2oStaticStatuses.reduce((result, item) => ({ ...result, [item]: [true] }), {}),
        ...n2oCategoryTierStatuses.reduce((result, item) => ({ ...result, [item]: [true] }), {}),
        emissionPoints: [true],
        emissionSources: [true],
        sourceStreams: [true],
      },
    });

    await expect(firstValueFrom(pipe.transform('N2O'))).resolves.toEqual('complete');
  });

  it('should calculate the n2o main sub task status', async () => {
    store.setState({
      ...mockState,
      permitSectionsCompleted: {
        N2O_Category: [true],
        N2O_Measured_Emissions: [true],
        N2O_Applied_Standard: [true],
        emissionPoints: [true],
        emissionSources: [true],
        sourceStreams: [true],
      },
    });

    await expect(firstValueFrom(pipe.transform('N2O_Category_Tier', 0))).resolves.toEqual('complete');

    store.setState({
      ...mockState,
      permitSectionsCompleted: {
        N2O_Category: [true],
        N2O_Measured_Emissions: [true],
        N2O_Applied_Standard: [true],
      },
    });

    await expect(firstValueFrom(pipe.transform('N2O_Category_Tier', 0))).resolves.toEqual('complete');

    store.setState({
      ...mockState,
      permitSectionsCompleted: {
        N2O_Category: [true],
        N2O_Measured_Emissions: [false],
        N2O_Applied_Standard: [true],
        emissionPoints: [true],
        emissionSources: [true],
        sourceStreams: [true],
      },
    });

    await expect(firstValueFrom(pipe.transform('N2O_Category_Tier', 0))).resolves.toEqual('in progress');

    store.setState({
      ...mockState,
      permitSectionsCompleted: {
        N2O_Category: [true],
        N2O_Measured_Emissions: [false],
        N2O_Applied_Standard: [false],
        emissionPoints: [true],
        emissionSources: [true],
        sourceStreams: [true],
      },
    });

    await expect(firstValueFrom(pipe.transform('N2O_Category_Tier', 0))).resolves.toEqual('in progress');

    store.setState({
      ...mockStateBuild({
        monitoringApproaches: {
          N2O: {
            type: 'N2O',
            sourceStreamCategoryAppliedTiers: [
              {
                sourceStreamCategory: {
                  sourceStream: 'unknown',
                  emissionSources: ['16245246343280.27155194483385103'],
                  emissionPoints: ['16363790610230.8369404469603225'],
                  emissionType: 'ABATED',
                  monitoringApproachType: 'CALCULATION',
                  annualEmittedCO2Tonnes: 23.5,
                  categoryType: 'MAJOR',
                },
              },
            ],
          },
        },
      }),
      permitSectionsCompleted: {
        N2O_Category: [true],
        N2O_Measured_Emissions: [true],
        N2O_Applied_Standard: [true],
        emissionPoints: [true],
        emissionSources: [true],
        sourceStreams: [true],
      },
    });

    await expect(firstValueFrom(pipe.transform('N2O_Category_Tier', 0))).resolves.toEqual('needs review');

    store.setState({
      ...mockStateBuild({
        monitoringApproaches: {
          N2O: {
            type: 'N2O',
            sourceStreamCategoryAppliedTiers: [
              {
                sourceStreamCategory: {
                  sourceStream: '16236817394240.1574963093314663',
                  emissionSources: ['unknown'],
                  emissionPoints: ['16363790610230.8369404469603225'],
                  emissionType: 'ABATED',
                  monitoringApproachType: 'CALCULATION',
                  annualEmittedCO2Tonnes: 23.5,
                  categoryType: 'MAJOR',
                },
              },
            ],
          },
        },
      }),
      permitSectionsCompleted: {
        N2O_Category: [true],
        N2O_Measured_Emissions: [true],
        N2O_Applied_Standard: [true],
        emissionPoints: [true],
        emissionSources: [true],
        sourceStreams: [true],
      },
    });

    await expect(firstValueFrom(pipe.transform('N2O_Category_Tier', 0))).resolves.toEqual('needs review');

    store.setState({
      ...mockStateBuild({
        monitoringApproaches: {
          N2O: {
            type: 'N2O',
            sourceStreamCategoryAppliedTiers: [
              {
                sourceStreamCategory: {
                  sourceStream: '16236817394240.1574963093314663',
                  emissionSources: ['16245246343280.27155194483385103'],
                  emissionPoints: ['unknown'],
                  emissionType: 'ABATED',
                  monitoringApproachType: 'CALCULATION',
                  annualEmittedCO2Tonnes: 23.5,
                  categoryType: 'MAJOR',
                },
              },
            ],
          },
        },
      }),
      permitSectionsCompleted: {
        N2O_Category: [true],
        N2O_Measured_Emissions: [true],
        N2O_Applied_Standard: [true],
        emissionPoints: [true],
        emissionSources: [true],
        sourceStreams: [true],
      },
    });

    await expect(firstValueFrom(pipe.transform('N2O_Category_Tier', 0))).resolves.toEqual('needs review');
  });

  it('should calculate the n2o sub task status', async () => {
    await expect(firstValueFrom(pipe.transform('N2O_Category', 0))).resolves.toEqual('not started');

    store.setState({
      ...mockState,
      permitSectionsCompleted: {
        N2O_Category: [true, true, false],
        emissionPoints: [true],
        emissionSources: [true],
        sourceStreams: [true],
      },
    });

    await expect(firstValueFrom(pipe.transform('N2O_Category', 0))).resolves.toEqual('complete');
    await expect(firstValueFrom(pipe.transform('N2O_Category', 1))).resolves.toEqual('needs review');
    await expect(firstValueFrom(pipe.transform('N2O_Category', 2))).resolves.toEqual('not started');

    store.setState({
      ...mockState,
      permitSectionsCompleted: {
        N2O_Category: [true, true, false],
        emissionPoints: [true],
        sourceStreams: [true],
      },
    });

    await expect(firstValueFrom(pipe.transform('N2O_Category', 0))).resolves.toEqual('complete');
    await expect(firstValueFrom(pipe.transform('N2O_Category', 1))).resolves.toEqual('needs review');
    await expect(firstValueFrom(pipe.transform('N2O_Category', 2))).resolves.toEqual('not started');
  });

  it('should calculate the transferred CO2 accounting sub task status', async () => {
    // All in place
    store.setState({
      ...mockState,
      permitSectionsCompleted: {
        measurementDevicesOrMethods: [true],
        TRANSFERRED_CO2_Accounting: [true],
      },
    });
    await expect(firstValueFrom(pipe.transform('TRANSFERRED_CO2_Accounting'))).resolves.toEqual('complete');

    // both completed with false measurement device id
    store.setState({
      ...mockStateBuild({
        monitoringApproaches: {
          TRANSFERRED_CO2: {
            type: 'TRANSFERRED_CO2',
            accountingEmissions: {
              chemicallyBound: false,
              accountingEmissionsDetails: {
                measurementDevicesOrMethods: ['unspecified'],
                samplingFrequency: 'DAILY',
                tier: 'TIER_4',
              },
            },
          },
        },
      }),
      permitSectionsCompleted: {
        measurementDevicesOrMethods: [true],
        TRANSFERRED_CO2_Accounting: [true],
      },
    });
    await expect(firstValueFrom(pipe.transform('TRANSFERRED_CO2_Accounting'))).resolves.toEqual('needs review');

    // With 1 step only
    store.setState({
      ...mockStateBuild({
        monitoringApproaches: {
          TRANSFERRED_CO2: {
            type: 'TRANSFERRED_CO2',
            accountingEmissions: {
              chemicallyBound: true,
            },
          },
        },
      }),
      permitSectionsCompleted: {
        measurementDevicesOrMethods: [true],
        TRANSFERRED_CO2_Accounting: [true],
      },
    });
    await expect(firstValueFrom(pipe.transform('TRANSFERRED_CO2_Accounting'))).resolves.toEqual('complete');

    // With 1 step and measurement devices in progress
    store.setState({
      ...mockStateBuild({
        monitoringApproaches: {
          TRANSFERRED_CO2: {
            type: 'TRANSFERRED_CO2',
            accountingEmissions: {
              chemicallyBound: true,
            },
          },
        },
      }),
      permitSectionsCompleted: {
        TRANSFERRED_CO2_Accounting: [true],
      },
    });
    await expect(firstValueFrom(pipe.transform('TRANSFERRED_CO2_Accounting'))).resolves.toEqual('complete');

    // With 1 step and measurement devices deleted
    store.setState({
      ...mockStateBuild({
        measurementDevicesOrMethods: null,
        monitoringApproaches: {
          TRANSFERRED_CO2: {
            type: 'TRANSFERRED_CO2',
            accountingEmissions: {
              chemicallyBound: true,
            },
          },
        },
      }),
      permitSectionsCompleted: {
        TRANSFERRED_CO2_Accounting: [true],
      },
    });
    await expect(firstValueFrom(pipe.transform('TRANSFERRED_CO2_Accounting'))).resolves.toEqual('complete');

    // With 2 steps and measurement devices in progress
    store.setState({
      ...mockState,
      permitSectionsCompleted: {
        TRANSFERRED_CO2_Accounting: [true],
      },
    });
    await expect(firstValueFrom(pipe.transform('TRANSFERRED_CO2_Accounting'))).resolves.toEqual('complete');

    // With 2 steps and deleted measurement devices
    store.setState({
      ...mockStateBuild({
        measurementDevicesOrMethods: null,
      }),
      permitSectionsCompleted: {
        TRANSFERRED_CO2_Accounting: [true],
      },
    });
    await expect(firstValueFrom(pipe.transform('TRANSFERRED_CO2_Accounting'))).resolves.toEqual('needs review');

    // With 2 steps and deleted dependent measurement device
    store.setState({
      ...mockStateBuild({
        measurementDevicesOrMethods: [
          {
            id: '16236817394240.1574963093314800',
            reference: 'ref2',
            type: 'ULTRASONIC_METER',
            measurementRange: '3',
            meteringRangeUnits: 'litres',
            location: 'north terminal',
            uncertaintySpecified: false,
          },
        ],
      }),
      permitSectionsCompleted: {
        TRANSFERRED_CO2_Accounting: [true],
      },
    });
    await expect(firstValueFrom(pipe.transform('TRANSFERRED_CO2_Accounting'))).resolves.toEqual('needs review');

    // Accounting not completed
    store.setState({
      ...mockState,
      permitSectionsCompleted: {
        measurementDevicesOrMethods: [true],
      },
    });
    await expect(firstValueFrom(pipe.transform('TRANSFERRED_CO2_Accounting'))).resolves.toEqual('in progress');

    // Accounting and measurementDevicesOrMethods not completed
    store.setState({
      ...mockState,
    });
    await expect(firstValueFrom(pipe.transform('TRANSFERRED_CO2_Accounting'))).resolves.toEqual('in progress');

    // Accounting not completed and deleted measurement devices
    store.setState({
      ...mockStateBuild({
        measurementDevicesOrMethods: null,
      }),
    });
    await expect(firstValueFrom(pipe.transform('TRANSFERRED_CO2_Accounting'))).resolves.toEqual('needs review');

    // With 1 step in progress and deleted measurement devices
    store.setState({
      ...mockStateBuild({
        measurementDevicesOrMethods: null,
        monitoringApproaches: {
          TRANSFERRED_CO2: {
            type: 'TRANSFERRED_CO2',
            accountingEmissions: {
              chemicallyBound: true,
            },
          },
        },
      }),
    });
    await expect(firstValueFrom(pipe.transform('TRANSFERRED_CO2_Accounting'))).resolves.toEqual('in progress');

    // With 2 steps in progress and deleted measurement devices
    store.setState({
      ...mockStateBuild({
        measurementDevicesOrMethods: null,
      }),
    });
    await expect(firstValueFrom(pipe.transform('TRANSFERRED_CO2_Accounting'))).resolves.toEqual('needs review');

    // With 2 steps in progress and deleted dependent measurement device
    store.setState({
      ...mockStateBuild({
        measurementDevicesOrMethods: [
          {
            id: '16236817394240.1574963093314800',
            reference: 'ref2',
            type: 'ULTRASONIC_METER',
            measurementRange: '3',
            meteringRangeUnits: 'litres',
            location: 'north terminal',
            uncertaintySpecified: false,
          },
        ],
      }),
    });
    await expect(firstValueFrom(pipe.transform('TRANSFERRED_CO2_Accounting'))).resolves.toEqual('in progress');

    // With measurement device in progress
    store.setState({
      ...mockStateBuild({
        monitoringApproaches: {
          TRANSFERRED_CO2: {
            type: 'TRANSFERRED_CO2',
          },
        },
      }),
    });
    await expect(firstValueFrom(pipe.transform('TRANSFERRED_CO2_Accounting'))).resolves.toEqual('cannot start yet');

    // With no measurement devices
    store.setState({
      ...mockStateBuild({
        measurementDevicesOrMethods: null,
        monitoringApproaches: {
          TRANSFERRED_CO2: {
            type: 'TRANSFERRED_CO2',
          },
        },
      }),
    });
    await expect(firstValueFrom(pipe.transform('TRANSFERRED_CO2_Accounting'))).resolves.toEqual('cannot start yet');

    // With measurement devices completed
    store.setState({
      ...mockStateBuild(
        {
          monitoringApproaches: {
            TRANSFERRED_CO2: {
              type: 'TRANSFERRED_CO2',
            },
          },
        },
        {
          measurementDevicesOrMethods: [true],
        },
      ),
    });
    await expect(firstValueFrom(pipe.transform('TRANSFERRED_CO2_Accounting'))).resolves.toEqual('not started');
  });
});
