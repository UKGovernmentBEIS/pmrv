import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { CalculationMonitoringApproach, TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../../../../testing';
import { SharedModule } from '../../../../../shared/shared.module';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { mockPermitApplyPayload } from '../../../../testing/mock-permit-apply-action';
import { mockPostBuild, mockStateBuild } from '../../../../testing/mock-state';
import { CalculationModule } from '../../calculation.module';
import { ActivityDataComponent } from './activity-data.component';

describe('ActivityDataComponent', () => {
  let store: PermitApplicationStore;
  let component: ActivityDataComponent;
  let fixture: ComponentFixture<ActivityDataComponent>;
  let page: Page;
  let router: Router;
  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub({ index: '0' }, null, {
    taskKey: 'monitoringApproaches.CALCULATION.sourceStreamCategoryAppliedTiers',
    statusKey: 'CALCULATION_Activity_Data',
  });
  const sourceStreamCategory = (
    mockPermitApplyPayload.permit.monitoringApproaches.CALCULATION as CalculationMonitoringApproach
  ).sourceStreamCategoryAppliedTiers[0].sourceStreamCategory;

  class Page extends BasePage<ActivityDataComponent> {
    get paragraphs() {
      return this.queryAll<HTMLParagraphElement>('p');
    }

    get measurementDevicesOrMethods() {
      return this.fixture.componentInstance.form.get('measurementDevicesOrMethods').value;
    }

    set measurementDevicesOrMethods(value: string[]) {
      this.fixture.componentInstance.form.get('measurementDevicesOrMethods').setValue(value);
    }

    get uncertainty() {
      return this.fixture.componentInstance.form.get('uncertainty');
    }

    get tierRadios() {
      return this.queryAll<HTMLInputElement>('input[name$="tier"]');
    }

    get isHighestTierRequiredT1Options() {
      return this.queryAll<HTMLInputElement>('input[name$="isHighestRequiredTierT1"]');
    }

    get isHighestTierRequiredT2Options() {
      return this.queryAll<HTMLInputElement>('input[name$="isHighestRequiredTierT2"]');
    }

    get isHighestTierRequiredT3Options() {
      return this.queryAll<HTMLInputElement>('input[name$="isHighestRequiredTierT3"]');
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
    fixture = TestBed.createComponent(ActivityDataComponent);
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

  describe('for new activity data', () => {
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
                    sourceStreamCategory: sourceStreamCategory,
                  },
                ],
              },
            },
          },
          {
            measurementDevicesOrMethods: [true],
            CALCULATION_Category: [true],
            CALCULATION_Activity_Data: [false],
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

    it('should submit a valid form and navigate correctly', () => {
      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');

      expect(page.errorSummary).toBeFalsy();

      expect(page.tierRadios.length).toEqual(5);
      page.tierRadios.every((option) => expect(option.checked).toBeFalsy());

      page.continueButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryErrorList).toEqual([
        'Select at least one measurement device',
        'Select an overall metering uncertainty',
        'Select a tier',
      ]);

      page.measurementDevicesOrMethods = [mockPermitApplyPayload.permit.measurementDevicesOrMethods[0].id];
      page.uncertainty.setValue('LESS_OR_EQUAL_2_5');
      page.tierRadios[0].click();
      page.continueButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
        mockPostBuild(
          {
            monitoringApproaches: {
              ...store.getState().permit.monitoringApproaches,
              CALCULATION: {
                ...store.getState().permit.monitoringApproaches.CALCULATION,
                sourceStreamCategoryAppliedTiers: [
                  {
                    sourceStreamCategory: sourceStreamCategory,
                    activityData: {
                      isHighestRequiredTier: undefined,
                      measurementDevicesOrMethods: ['16236817394240.1574963093314700'],
                      uncertainty: 'LESS_OR_EQUAL_2_5',
                      tier: 'TIER_4',
                      noHighestRequiredTierJustification: null,
                    },
                  },
                ],
              },
            },
          },
          {
            ...store.getState().permitSectionsCompleted,
            CALCULATION_Activity_Data: [false],
          },
        ),
      );
      expect(navigateSpy).toHaveBeenCalledWith(['justification'], { relativeTo: route });
    });
  });

  describe('for existing activity data', () => {
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
                    sourceStreamCategory: sourceStreamCategory,
                    activityData: {
                      measurementDevicesOrMethods: ['16236817394240.1574963093314700'],
                      uncertainty: 'LESS_OR_EQUAL_2_5',
                      tier: 'TIER_4',
                    },
                  },
                ],
              },
            },
          },
          {
            measurementDevicesOrMethods: [true],
            CALCULATION_Category: [true],
            CALCULATION_Activity_Data: [false],
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

    it('should display exisitng activity data', () => {
      expect(page.errorSummary).toBeFalsy();

      expect(page.tierRadios.length).toEqual(5);
      expect(page.tierRadios[0].checked).toBeTruthy();
      expect(page.tierRadios[1].checked).toBeFalsy();
      expect(page.tierRadios[2].checked).toBeFalsy();
      expect(page.tierRadios[3].checked).toBeFalsy();
      expect(page.isHighestTierRequiredT1Options[0].checked).toBeFalsy();
      expect(page.isHighestTierRequiredT2Options[1].checked).toBeFalsy();
      expect(page.isHighestTierRequiredT3Options[0].checked).toBeFalsy();
    });

    it('should submit a valid form and navigate correctly', () => {
      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');

      expect(page.errorSummary).toBeFalsy();

      fixture.detectChanges();
      expect(page.tierRadios.length).toEqual(5);

      page.tierRadios[1].click();
      page.continueButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryErrorList).toEqual(['Select Yes or No']);

      expect(page.isHighestTierRequiredT1Options.length).toEqual(2);
      page.isHighestTierRequiredT3Options[0].click();
      page.continueButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
        mockPostBuild(
          {
            monitoringApproaches: {
              ...store.getState().permit.monitoringApproaches,
              CALCULATION: {
                ...store.getState().permit.monitoringApproaches.CALCULATION,
                sourceStreamCategoryAppliedTiers: [
                  {
                    sourceStreamCategory: sourceStreamCategory,
                    activityData: {
                      measurementDevicesOrMethods: ['16236817394240.1574963093314700'],
                      tier: 'TIER_3',
                      uncertainty: 'LESS_OR_EQUAL_2_5',
                      isHighestRequiredTier: true,
                      noHighestRequiredTierJustification: null,
                    },
                  },
                ],
              },
            },
          },
          {
            ...store.getState().permitSectionsCompleted,
            CALCULATION_Activity_Data: [false],
          },
        ),
      );
      expect(navigateSpy).toHaveBeenCalledWith(['justification'], { relativeTo: route });
    });
  });
});
