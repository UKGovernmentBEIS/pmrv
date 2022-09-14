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
import { AnswersComponent } from './answers.component';

describe('AnswersComponent', () => {
  let store: PermitApplicationStore;
  let component: AnswersComponent;
  let fixture: ComponentFixture<AnswersComponent>;
  let page: Page;
  let router: Router;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub({ index: '0' }, null, {
    taskKey: 'monitoringApproaches.CALCULATION.sourceStreamCategoryAppliedTiers',
    statusKey: 'CALCULATION_Calorific',
  });

  const sourceStreamCategory = (
    mockPermitApplyPayload.permit.monitoringApproaches.CALCULATION as CalculationMonitoringApproach
  ).sourceStreamCategoryAppliedTiers[0].sourceStreamCategory;

  class Page extends BasePage<AnswersComponent> {
    get heading() {
      return this.query<HTMLElement>('.govuk-heading-l');
    }
    get answers() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
    get confirmButton() {
      return this.query<HTMLButtonElement>('.govuk-button');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CalculationModule, RouterTestingModule, SharedModule],
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
                    tier: 'TIER_2B',
                    isHighestRequiredTier: true,
                    defaultValueApplied: true,
                    standardReferenceSource: {
                      type: 'MONITORING_REPORTING_REGULATION_ARTICLE_36_3',
                    },
                    analysisMethodUsed: false,
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
    fixture = TestBed.createComponent(AnswersComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should submit task status', () => {
    tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
    const navigateSpy = jest.spyOn(router, 'navigate');

    page.confirmButton.click();
    fixture.detectChanges();

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
                    tier: 'TIER_2B',
                    isHighestRequiredTier: true,
                    defaultValueApplied: true,
                    standardReferenceSource: {
                      type: 'MONITORING_REPORTING_REGULATION_ARTICLE_36_3',
                    },
                    analysisMethodUsed: false,
                  },
                },
              ],
            },
          },
        },
        {
          ...mockState.permitSectionsCompleted,
          CALCULATION_Calorific: [true],
        },
      ),
    );
    expect(navigateSpy).toHaveBeenCalledWith(['../summary'], { relativeTo: route, state: { notification: true } });
  });
});
