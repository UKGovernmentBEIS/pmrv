import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { CalculationMonitoringApproach, TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../../../../testing';
import { SharedModule } from '../../../../../shared/shared.module';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { mockPermitApplyPayload } from '../../../../testing/mock-permit-apply-action';
import { mockPostBuild, mockState, mockStateBuild } from '../../../../testing/mock-state';
import { CalculationModule } from '../../calculation.module';
import { AnalysisMethodDeleteComponent } from './analysis-method-delete.component';

describe('AnalysisMethodDeleteComponent', () => {
  let page: Page;
  let router: Router;
  let store: PermitApplicationStore;
  let component: AnalysisMethodDeleteComponent;
  let fixture: ComponentFixture<AnalysisMethodDeleteComponent>;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub({ index: '0', methodIndex: '0' }, null, {
    taskKey: 'monitoringApproaches.CALCULATION.sourceStreamCategoryAppliedTiers',
    statusKey: 'CALCULATION_Calorific',
  });
  const sourceStreamCategory = (
    mockPermitApplyPayload.permit.monitoringApproaches.CALCULATION as CalculationMonitoringApproach
  ).sourceStreamCategoryAppliedTiers[0].sourceStreamCategory;

  class Page extends BasePage<AnalysisMethodDeleteComponent> {
    get heading() {
      return this.query<HTMLHeadingElement>('h1');
    }
    get submitButton() {
      return this.queryAll<HTMLButtonElement>('button')[0];
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, CalculationModule, RouterTestingModule],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(PermitApplicationStore);
    store.setState(
      mockStateBuild(
        {
          monitoringApproaches: {
            CALCULATION: {
              type: 'CALCULATION',
              sourceStreamCategoryAppliedTiers: [
                {
                  sourceStreamCategory,
                  netCalorificValue: {
                    exist: true,
                    oneThirdRule: false,
                    tier: 'TIER_3',
                    defaultValueApplied: undefined,
                    analysisMethodUsed: true,
                    analysisMethods: [
                      {
                        analysis: 'analyze',
                        files: [],
                        frequencyMeetsMinRequirements: false,
                        laboratoryAccredited: false,
                        laboratoryName: 'lab',
                        samplingFrequency: 'WEEKLY',
                        subParameter: null,
                      },
                    ],
                  },
                },
              ],
            },
          },
        },
        {
          CALCULATION_Calorific: [false],
        },
      ),
    );

    fixture = TestBed.createComponent(AnalysisMethodDeleteComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should delete', () => {
    tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
    const navigateSpy = jest.spyOn(router, 'navigate');

    expect(page.heading).toBeTruthy();
    expect(page.heading.textContent.trim()).toEqual('Are you sure you want to delete the Analysis method?');

    page.submitButton.click();

    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
      mockPostBuild(
        {
          monitoringApproaches: {
            CALCULATION: {
              type: 'CALCULATION',
              sourceStreamCategoryAppliedTiers: [
                {
                  sourceStreamCategory,
                  netCalorificValue: {
                    exist: true,
                    oneThirdRule: false,
                    tier: 'TIER_3',
                    defaultValueApplied: undefined,
                    analysisMethodUsed: true,
                    analysisMethods: [],
                  },
                },
              ],
            },
          },
        },
        {
          ...mockState.permitSectionsCompleted,
          CALCULATION_Calorific: [false],
        },
      ),
    );
    expect(navigateSpy).toHaveBeenCalledTimes(1);
  });
});
