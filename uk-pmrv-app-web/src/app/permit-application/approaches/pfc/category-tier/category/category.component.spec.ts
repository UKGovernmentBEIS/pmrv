import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { ActivatedRouteSnapshotStub, ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { PFCMonitoringApproach, TasksService } from 'pmrv-api';

import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { mockPermitApplyPayload } from '../../../../testing/mock-permit-apply-action';
import { mockPostBuild, mockState, mockStateBuild } from '../../../../testing/mock-state';
import { PFCModule } from '../../pfc.module';
import { CategoryComponent } from './category.component';

describe('CategoryComponent', () => {
  let page: Page;
  let router: Router;
  let store: PermitApplicationStore;
  let activatedRoute: ActivatedRouteSnapshotStub;
  let component: CategoryComponent;
  let fixture: ComponentFixture<CategoryComponent>;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub({ taskId: '237', index: '0' }, null, {
    taskKey: 'monitoringApproaches.PFC.sourceStreamCategoryAppliedTiers',
  });

  class Page extends BasePage<CategoryComponent> {
    get sourceStream(): string {
      return this.getInputValue('#sourceStream');
    }
    set sourceStream(value: string) {
      this.setInputValue('#sourceStream', value);
    }
    get emissionSources() {
      return this.fixture.componentInstance.form.get('emissionSources').value;
    }
    set emissionSources(value: string[]) {
      this.fixture.componentInstance.form.get('emissionSources').setValue(value);
    }
    get emissionPoints() {
      return this.fixture.componentInstance.form.get('emissionPoints').value;
    }
    set emissionPoints(value: string[]) {
      this.fixture.componentInstance.form.get('emissionPoints').setValue(value);
    }
    get calculationMethod(): string {
      return this.fixture.componentInstance.form.get('calculationMethod').value;
    }
    set calculationMethod(value: string) {
      this.fixture.componentInstance.form.get('calculationMethod').setValue(value);
    }
    get annualEmittedCO2Tonnes(): string {
      return this.getInputValue('#annualEmittedCO2Tonnes');
    }
    set annualEmittedCO2Tonnes(value: string) {
      this.setInputValue('#annualEmittedCO2Tonnes', value);
    }
    get categoryType(): string {
      return this.fixture.componentInstance.form.get('categoryType').value;
    }
    set categoryType(value: string) {
      this.fixture.componentInstance.form.get('categoryType').setValue(value);
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }
    get errorSummaryLinks() {
      return Array.from(this.query<HTMLDivElement>('.govuk-error-summary').querySelectorAll('a')).map((item) =>
        item.textContent.trim(),
      );
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(CategoryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    activatedRoute = new ActivatedRouteSnapshotStub({ taskId: '237', index: '0' });
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PFCModule, RouterTestingModule],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: ActivatedRouteSnapshotStub, useValue: activatedRoute },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();
  });

  describe('for editing source stream category', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(
        mockStateBuild(mockState, {
          PFC_Category: [true],
          PFC_Activity_Data: [false],
          PFC_Emission_Factor: [false],
        }),
      );
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should submit a valid form, update the store and navigate to summary', () => {
      expect(page.errorSummary).toBeFalsy();

      page.annualEmittedCO2Tonnes = '';

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryLinks).toEqual(['Select an estimated tonnes of CO2e']);

      page.annualEmittedCO2Tonnes = '5000';

      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
        mockPostBuild(
          {
            ...mockState,
            monitoringApproaches: {
              ...mockState.permit.monitoringApproaches,
              PFC: {
                ...mockState.permit.monitoringApproaches.PFC,
                sourceStreamCategoryAppliedTiers: [
                  {
                    ...(mockState.permit.monitoringApproaches.PFC as PFCMonitoringApproach)
                      .sourceStreamCategoryAppliedTiers[0],
                    sourceStreamCategory: {
                      ...(mockState.permit.monitoringApproaches.PFC as PFCMonitoringApproach)
                        .sourceStreamCategoryAppliedTiers[0].sourceStreamCategory,
                      annualEmittedCO2Tonnes: 5000,
                    },
                  },
                ],
              },
            },
          },
          {
            ...mockState.permitSectionsCompleted,
            PFC_Category: [true],
            PFC_Activity_Data: [false],
            PFC_Emission_Factor: [false],
          },
        ),
      );

      expect(navigateSpy).toHaveBeenCalledWith(['summary'], {
        relativeTo: route,
        state: { notification: true },
      });
    });
  });

  describe('for new source stream category', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(
        mockStateBuild({
          ...mockPermitApplyPayload.permit,
          monitoringApproaches: {
            ...mockPermitApplyPayload.permit.monitoringApproaches,
            PFC: {
              ...mockPermitApplyPayload.permit.monitoringApproaches.PFC,
              sourceStreamCategoryAppliedTiers: undefined,
            } as PFCMonitoringApproach,
          },
        }),
      );
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should submit a valid form, update the store and navigate to summary', () => {
      expect(page.errorSummary).toBeFalsy();

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryLinks).toEqual([
        'Select a source stream',
        'Select at least one emission source',
        'Select at least one emission point',
        'Select an estimated tonnes of CO2e',
        'Select a calculation method',
        'Select a category',
      ]);

      const sourceStreamCategory = (mockPermitApplyPayload.permit.monitoringApproaches.PFC as PFCMonitoringApproach)
        .sourceStreamCategoryAppliedTiers[0].sourceStreamCategory;

      page.sourceStream = sourceStreamCategory.sourceStream;
      page.emissionSources = sourceStreamCategory.emissionSources;
      page.emissionPoints = sourceStreamCategory.emissionPoints;
      page.annualEmittedCO2Tonnes = sourceStreamCategory.annualEmittedCO2Tonnes.toString();
      page.calculationMethod = sourceStreamCategory.calculationMethod;
      page.categoryType = sourceStreamCategory.categoryType;

      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
        mockPostBuild(
          {
            ...store.permit,
            monitoringApproaches: {
              ...store.permit.monitoringApproaches,
              PFC: {
                ...store.permit.monitoringApproaches.PFC,
                sourceStreamCategoryAppliedTiers: [{ sourceStreamCategory: sourceStreamCategory }],
              } as PFCMonitoringApproach,
            },
          },
          {
            ...mockPermitApplyPayload.permitSectionsCompleted,
            PFC_Category: [true],
            PFC_Activity_Data: [false],
            PFC_Emission_Factor: [false],
          },
        ),
      );

      expect(navigateSpy).toHaveBeenCalledWith(['summary'], {
        relativeTo: route,
        state: { notification: true },
      });
    });
  });

  describe('for editing source stream category with deleted reference data', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(
        mockStateBuild(
          {
            sourceStreams: [],
            emissionSources: [],
            emissionPoints: [],
          },
          { PFC_Category: [true] },
        ),
      );
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });
  });
});
