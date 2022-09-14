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
import { EntryStepComponent } from './entry-step.component';

describe('EntryStepComponent', () => {
  let store: PermitApplicationStore;
  let component: EntryStepComponent;
  let fixture: ComponentFixture<EntryStepComponent>;

  let page: Page;
  let router: Router;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub({ index: '0' }, null, {
    taskKey: 'monitoringApproaches.CALCULATION.sourceStreamCategoryAppliedTiers',
    statusKey: 'CALCULATION_Calorific',
  });

  class Page extends BasePage<EntryStepComponent> {
    get paragraphs() {
      return this.queryAll<HTMLParagraphElement>('p');
    }

    get existRadios() {
      return this.queryAll<HTMLInputElement>('input[name$="exist"]');
    }

    get continueButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }

    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get errorSummaryErrorList() {
      return Array.from(this.query<HTMLDivElement>('.govuk-error-summary').querySelectorAll('a')).map((anchor) =>
        anchor.textContent.trim(),
      );
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(EntryStepComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CalculationModule, RouterTestingModule, SharedModule],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();
  });

  describe('task prerequisites not completed', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(
        mockStateBuild({
          monitoringApproaches: {
            CALCULATION: {
              sourceStreamCategoryAppliedTiers: [],
            },
          },
        }),
      );
    });

    beforeEach(() => {
      createComponent();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display the can not start page', () => {
      expect(
        page.paragraphs.find((p) => p.textContent.trim() === 'Other tasks must be completed before you can start:'),
      ).toBeTruthy();
    });
  });

  describe('for new net calorific value', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(
        mockStateBuild(
          {},
          {
            CALCULATION_Category: [true],
            CALCULATION_Calorific: [false],
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

    it('should require a yes or a no', () => {
      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');

      expect(page.errorSummary).toBeFalsy();

      page.continueButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryErrorList).toEqual(['Select Yes or No']);

      page.existRadios[0].click();
      page.continueButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
        mockPostBuild(
          {
            monitoringApproaches: {
              ...mockState.permit.monitoringApproaches,
              CALCULATION: {
                ...mockState.permit.monitoringApproaches.CALCULATION,
                sourceStreamCategoryAppliedTiers: [
                  {
                    sourceStreamCategory: (
                      mockState.permit.monitoringApproaches.CALCULATION as CalculationMonitoringApproach
                    ).sourceStreamCategoryAppliedTiers[0].sourceStreamCategory,
                    netCalorificValue: {
                      exist: true,
                    },
                  },
                ],
              },
            },
          },
          {
            ...mockState.permitSectionsCompleted,
            CALCULATION_Category: [true],
            CALCULATION_Calorific: [false],
          },
        ),
      );
      expect(navigateSpy).toHaveBeenCalledWith(['./tier'], { relativeTo: route });
    });
  });

  describe('for existing net calorific value', () => {
    beforeEach(() => {
      const sourceStreamCategory = (
        mockPermitApplyPayload.permit.monitoringApproaches.CALCULATION as CalculationMonitoringApproach
      ).sourceStreamCategoryAppliedTiers[0].sourceStreamCategory;

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
                      exist: false,
                    },
                  },
                ],
              },
            },
          },
          {
            CALCULATION_Category: [true],
            CALCULATION_Calorific: [false],
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

    it('should fill the form', () => {
      expect(page.errorSummary).toBeFalsy();

      expect(page.existRadios.length).toEqual(2);
      expect(page.existRadios[0].checked).toBeFalsy();
      expect(page.existRadios[1].checked).toBeTruthy();
    });
  });
});
