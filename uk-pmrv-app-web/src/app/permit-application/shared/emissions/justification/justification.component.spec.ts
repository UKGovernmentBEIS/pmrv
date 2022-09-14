import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../../../testing';
import { SharedModule } from '../../../../shared/shared.module';
import { MeasurementModule } from '../../../approaches/measurement/measurement.module';
import { N2oModule } from '../../../approaches/n2o/n2o.module';
import { PermitApplicationStore } from '../../../store/permit-application.store';
import { mockPostBuild, mockStateBuild } from '../../../testing/mock-state';
import { JustificationComponent } from './justification.component';

describe('JustificationComponent', () => {
  let store: PermitApplicationStore;
  let component: JustificationComponent;
  let fixture: ComponentFixture<JustificationComponent>;
  let page: Page;
  let router: Router;
  const tasksService = mockClass(TasksService);
  class Page extends BasePage<JustificationComponent> {
    get unreasonableCost() {
      return this.query<HTMLInputElement>('#justification-0');
    }

    get technicalInfeasibility() {
      return this.query<HTMLInputElement>('#justification-1');
    }
    get technicalInfeasibilityExplanation() {
      return this.getInputValue('#technicalInfeasibilityExplanation');
    }
    set technicalInfeasibilityExplanation(value: string) {
      this.setInputValue('#technicalInfeasibilityExplanation', value);
    }
    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(JustificationComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  const taskKeys = ['N2O', 'MEASUREMENT'];

  for (const taskKey of taskKeys) {
    describe(taskKey, () => {
      const route = new ActivatedRouteStub(
        {},
        {},
        {
          taskKey: taskKey,
        },
      );

      beforeEach(async () => {
        await TestBed.configureTestingModule({
          imports: [RouterTestingModule, N2oModule, MeasurementModule, SharedModule],
          providers: [
            { provide: ActivatedRoute, useValue: route },
            { provide: TasksService, useValue: tasksService },
          ],
        });
      });

      describe('submit data', () => {
        beforeEach(() => {
          store = TestBed.inject(PermitApplicationStore);
          store.setState(
            mockStateBuild(
              {
                monitoringApproaches: {
                  [taskKey]: {
                    type: taskKey,
                    sourceStreamCategoryAppliedTiers: [
                      {
                        sourceStreamCategory: {
                          sourceStream: '16236817394240.1574963093314663',
                          emissionSources: ['16245246343280.27155194483385103'],
                          emissionPoints: ['16363790610230.8369404469603225'],
                          emissionType: 'ABATED',
                          monitoringApproachType: 'CALCULATION',
                          annualEmittedCO2Tonnes: 23.5,
                          categoryType: 'MAJOR',
                        },
                        measuredEmissions: {
                          measurementDevicesOrMethods: ['16236817394240.1574963093314700'],
                          samplingFrequency: 'MONTHLY',
                          tier: 'TIER_1',
                          isHighestRequiredTier: false,
                          noHighestRequiredTierJustification: {
                            isCostUnreasonable: true,
                            isTechnicallyInfeasible: true,
                            technicalInfeasibilityExplanation: 'This is an explanation',
                          },
                        },
                      },
                    ],
                  },
                },
              },
              {
                measurementDevicesOrMethods: [true],
                [`${taskKey}_Category`]: [true],
                [`${taskKey}_Measured_Emissions`]: [false],
              },
            ),
          );
        });

        beforeEach(() => {
          createComponent();
        });

        it('should create', () => {
          expect(component).toBeTruthy();
        });

        it('should fill form from store', () => {
          expect(page.unreasonableCost.value).toEqual('isCostUnreasonable');
          expect(page.technicalInfeasibility.value).toEqual('isTechnicallyInfeasible');
          expect(page.technicalInfeasibilityExplanation).toEqual('This is an explanation');
        });

        it('should submit updated values and navigate to justification', () => {
          const navigateSpy = jest.spyOn(router, 'navigate');
          tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));

          page.technicalInfeasibilityExplanation = 'This is another explanation';
          fixture.detectChanges();

          page.submitButton.click();
          fixture.detectChanges();

          expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
          expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
            mockPostBuild(
              {
                monitoringApproaches: {
                  ...store.getState().permit.monitoringApproaches,
                  [taskKey]: {
                    ...store.getState().permit.monitoringApproaches[taskKey],
                    sourceStreamCategoryAppliedTiers: [
                      {
                        sourceStreamCategory: {
                          sourceStream: '16236817394240.1574963093314663',
                          emissionSources: ['16245246343280.27155194483385103'],
                          emissionPoints: ['16363790610230.8369404469603225'],
                          emissionType: 'ABATED',
                          monitoringApproachType: 'CALCULATION',
                          annualEmittedCO2Tonnes: 23.5,
                          categoryType: 'MAJOR',
                        },
                        measuredEmissions: {
                          measurementDevicesOrMethods: ['16236817394240.1574963093314700'],
                          samplingFrequency: 'MONTHLY',
                          tier: 'TIER_1',
                          isHighestRequiredTier: false,
                          noHighestRequiredTierJustification: {
                            isCostUnreasonable: true,
                            isTechnicallyInfeasible: true,
                            technicalInfeasibilityExplanation: 'This is another explanation',
                            files: [],
                          },
                        },
                      },
                    ],
                  },
                },
              },
              { ...store.getState().permitSectionsCompleted, [`${taskKey}_Measured_Emissions`]: [false] },
            ),
          );

          expect(navigateSpy).toHaveBeenCalledWith(['../answers'], {
            relativeTo: route,
          });
        });
      });
    });
  }
});
