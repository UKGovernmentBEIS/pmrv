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
import { AnswersComponent } from './answers.component';

describe('AnswersComponent', () => {
  let store: PermitApplicationStore;
  let component: AnswersComponent;
  let fixture: ComponentFixture<AnswersComponent>;
  let page: Page;
  let router: Router;
  const tasksService = mockClass(TasksService);

  class Page extends BasePage<AnswersComponent> {
    get measuredEmissions(): HTMLDListElement[] {
      return this.queryAll<HTMLDListElement>('dl');
    }

    get measuredEmissionsTextContents(): string[][] {
      return this.measuredEmissions.map((measuredEmission) =>
        Array.from(measuredEmission.querySelectorAll('dd')).map((dd) => dd.textContent.trim()),
      );
    }

    get errorSummaryLinks() {
      return Array.from(this.query<HTMLDivElement>('.govuk-error-summary')?.querySelectorAll('a') ?? []).map((item) =>
        item.textContent.trim(),
      );
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('.govuk-button');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(AnswersComponent);
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
        }).compileComponents();
      });

      describe('answers completed', () => {
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

        it('should display the measured emissions answers', () => {
          expect(page.measuredEmissionsTextContents).toEqual([
            ['ref1, Ultrasonic meter , Specified uncertainty ±2', 'Monthly', 'Tier 1', 'No'],
            ['Unreasonable cost  Technical infeasibility', 'This is an explanation'],
          ]);
        });

        it('should submit the emission summary task and navigate to summary', () => {
          const navigateSpy = jest.spyOn(router, 'navigate');
          tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));

          page.submitButton.click();
          fixture.detectChanges();

          expect(navigateSpy).toHaveBeenCalledTimes(1);
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
                            technicalInfeasibilityExplanation: 'This is an explanation',
                          },
                        },
                      },
                    ],
                  },
                },
              },
              { ...store.getState().permitSectionsCompleted, [`${taskKey}_Measured_Emissions`]: [true] },
            ),
          );
          expect(navigateSpy).toHaveBeenCalledWith(['../summary'], {
            relativeTo: route,
            state: { notification: true },
          });
        });
      });

      describe('answers contain errors', () => {
        beforeEach(() => {
          store = TestBed.inject(PermitApplicationStore);
          store.setState(
            mockStateBuild(
              {
                measurementDevicesOrMethods: [],
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

        it('should show errors and not submit form', () => {
          const navigateSpy = jest.spyOn(router, 'navigate');
          tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));

          page.submitButton.click();
          fixture.detectChanges();

          expect(navigateSpy).toHaveBeenCalledTimes(0);

          expect(page.errorSummaryLinks).toEqual(['Select at least one measurement device']);
        });
      });
    });
  }
});
