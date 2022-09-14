import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../../../../testing';
import { SharedModule } from '../../../../../shared/shared.module';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { mockPostBuild, mockStateBuild } from '../../../../testing/mock-state';
import { MeasurementModule } from '../../measurement.module';
import { AppliedStandardComponent } from './applied-standard.component';

describe('AppliedStandardComponent', () => {
  let fixture: ComponentFixture<AppliedStandardComponent>;
  let component: AppliedStandardComponent;
  let page: Page;
  let store: PermitApplicationStore;
  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub({ index: 0 }, null, {
    taskKey: 'monitoringApproaches.MEASUREMENT.sourceStreamCategoryAppliedTiers',
    statusKey: 'MEASUREMENT_Applied_Standard',
  });
  let router: Router;
  let activatedRoute: ActivatedRoute;

  class Page extends BasePage<AppliedStandardComponent> {
    get caption() {
      return this.query<HTMLElement>('.govuk-caption-l');
    }

    get paragraphs() {
      return this.queryAll<HTMLParagraphElement>('p');
    }

    get ulists() {
      return this.queryAll<HTMLUListElement>('ul > li');
    }

    get parameter() {
      return this.getInputValue('#parameter');
    }

    set parameter(value: string) {
      this.setInputValue('#parameter', value);
    }

    get appliedStandard() {
      return this.getInputValue<HTMLInputElement>('#appliedStandard');
    }

    get deviationFromAppliedStandardExist() {
      return this.query<HTMLInputElement>('#deviationFromAppliedStandardExist-option0');
    }

    get deviationFromAppliedStandardNotExist() {
      return this.query<HTMLInputElement>('#deviationFromAppliedStandardExist-option1');
    }

    get deviationFromAppliedStandardDetails() {
      return this.getInputValue<HTMLTextAreaElement>('#deviationFromAppliedStandardDetails');
    }

    get laboratoryName() {
      return this.getInputValue<HTMLInputElement>('#laboratoryName');
    }

    get laboratoryAccredited() {
      return this.query<HTMLInputElement>('#laboratoryAccredited-option0');
    }

    get laboratoryAccreditationEvidence() {
      return this.getInputValue<HTMLTextAreaElement>('#laboratoryAccreditationEvidence');
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('.govuk-button');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(AppliedStandardComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, MeasurementModule, RouterTestingModule],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();
  });

  describe('applied standard not completed and category not completed', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(
        mockStateBuild(
          {
            monitoringApproaches: {
              MEASUREMENT: {
                type: 'MEASUREMENT',
                sourceStreamCategoryAppliedTiers: [],
              },
            },
          },
          {
            sourceStreams: [false],
            MEASUREMENT_Category: [],
            MEASUREMENT_Applied_Standard: [],
          },
        ),
      );
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display the not started page', () => {
      expect(page.caption.textContent.trim()).toEqual('Monitoring approach, Measurement');
      expect(
        page.paragraphs.find((p) => p.textContent.trim() === 'Other tasks must be completed before you can start:'),
      ).toBeTruthy();

      expect(page.ulists.find((ul) => ul.textContent.trim() === 'Source stream category')).toBeTruthy();
    });
  });

  describe('applied standard not completed and category and prerequisities completed', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(
        mockStateBuild(
          {
            monitoringApproaches: {
              MEASUREMENT: {
                type: 'MEASUREMENT',
                sourceStreamCategoryAppliedTiers: [
                  {
                    sourceStreamCategory: {
                      sourceStream: '16236817394240.1574963093314663',
                      emissionSources: ['16245246343280.27155194483385103'],
                      emissionPoints: ['16363790610230.8369404469603225'],
                      annualEmittedCO2Tonnes: 23.5,
                      categoryType: 'MAJOR',
                    },
                  },
                ],
              },
            },
          },
          {
            sourceStreams: [true],
            emissionSources: [true],
            emissionPoints: [true],
            MEASUREMENT_Category: [true],
            MEASUREMENT_Applied_Standard: [],
          },
        ),
      );
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should not display the not started page', () => {
      expect(page.caption.textContent.trim()).toEqual('Measurement, 13123124 White Spirit & SBP: Major');
      expect(
        page.paragraphs.find((p) => p.textContent.trim() === 'Other tasks must be completed before you can start:'),
      ).toBeFalsy();

      expect(page.ulists.find((ul) => ul.textContent.trim() === 'Source stream category')).toBeFalsy();
    });
  });

  describe('applied standard completed and category and prerequisities not completed', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(
        mockStateBuild(
          {
            monitoringApproaches: {
              MEASUREMENT: {
                type: 'MEASUREMENT',
                sourceStreamCategoryAppliedTiers: [
                  {
                    sourceStreamCategory: {
                      sourceStream: '16236817394240.1574963093314663',
                      emissionSources: ['16245246343280.27155194483385103'],
                      emissionPoints: ['16363790610230.8369404469603225'],
                      annualEmittedCO2Tonnes: 23.5,
                      categoryType: 'MAJOR',
                    },
                  },
                ],
              },
            },
          },
          {
            sourceStreams: [false],
            MEASUREMENT_Category: [false],
            MEASUREMENT_Applied_Standard: [true],
          },
        ),
      );
    });
    beforeEach(createComponent);

    it('should not display the not started page', () => {
      expect(page.caption.textContent.trim()).toEqual('Measurement, 13123124 White Spirit & SBP: Major');
      expect(
        page.paragraphs.find((p) => p.textContent.trim() === 'Other tasks must be completed before you can start:'),
      ).toBeFalsy();

      expect(page.ulists.find((ul) => ul.textContent.trim() === 'Source stream category')).toBeFalsy();
    });
  });

  describe('applied standard completed', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(
        mockStateBuild(
          {
            monitoringApproaches: {
              MEASUREMENT: {
                type: 'MEASUREMENT',
                sourceStreamCategoryAppliedTiers: [
                  {
                    sourceStreamCategory: {
                      sourceStream: '16236817394240.1574963093314663',
                      emissionSources: ['16245246343280.27155194483385103'],
                      emissionPoints: ['16363790610230.8369404469603225'],
                      annualEmittedCO2Tonnes: 23.5,
                      categoryType: 'MAJOR',
                    },
                    appliedStandard: {
                      parameter: 'parameter',
                      appliedStandard: 'appliedStandard',
                      deviationFromAppliedStandardExist: true,
                      deviationFromAppliedStandardDetails: 'deviationFromAppliedStandardDetails',
                      laboratoryName: 'laboratoryName',
                      laboratoryAccredited: true,
                    },
                  },
                ],
              },
            },
          },
          {
            sourceStreams: [false],
            MEASUREMENT_Category: [true],
            MEASUREMENT_Applied_Standard: [true],
          },
        ),
      );
    });
    beforeEach(createComponent);

    it('should populate form with values', () => {
      expect(page.parameter).toEqual('parameter');
      expect(page.appliedStandard).toEqual('appliedStandard');
      expect(page.deviationFromAppliedStandardExist.checked).toBeTruthy();
      expect(page.deviationFromAppliedStandardDetails).toEqual('deviationFromAppliedStandardDetails');
      expect(page.laboratoryName).toEqual('laboratoryName');
      expect(page.laboratoryAccredited.checked).toBeTruthy();
      expect(page.laboratoryAccreditationEvidence).toBeFalsy();
    });

    it('should submit updated values', () => {
      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));

      page.parameter = 'new parameter';
      page.deviationFromAppliedStandardNotExist.click();
      fixture.detectChanges();

      const navigateSpy = jest.spyOn(router, 'navigate');
      page.submitButton.click();
      fixture.detectChanges();

      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
        mockPostBuild(
          {
            monitoringApproaches: {
              ...store.getState().permit.monitoringApproaches,
              MEASUREMENT: {
                ...store.getState().permit.monitoringApproaches.MEASUREMENT,
                sourceStreamCategoryAppliedTiers: [
                  {
                    sourceStreamCategory: {
                      sourceStream: '16236817394240.1574963093314663',
                      emissionSources: ['16245246343280.27155194483385103'],
                      emissionPoints: ['16363790610230.8369404469603225'],
                      annualEmittedCO2Tonnes: 23.5,
                      categoryType: 'MAJOR',
                    },
                    appliedStandard: {
                      parameter: 'new parameter',
                      appliedStandard: 'appliedStandard',
                      deviationFromAppliedStandardExist: false,
                      laboratoryName: 'laboratoryName',
                      laboratoryAccredited: true,
                    },
                  },
                ],
              },
            },
          },
          { ...store.getState().permitSectionsCompleted, MEASUREMENT_Applied_Standard: [true] },
        ),
      );
      expect(navigateSpy).toHaveBeenCalledWith(['summary'], {
        relativeTo: activatedRoute,
        state: { notification: true },
      });
      expect(navigateSpy).toHaveBeenCalledTimes(1);
    });
  });
});
