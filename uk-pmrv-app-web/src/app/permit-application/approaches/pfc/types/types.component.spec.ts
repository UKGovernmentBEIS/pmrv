import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { PFCMonitoringApproach, TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../../../testing';
import { PermitApplicationStore } from '../../../store/permit-application.store';
import { mockPermitApplyPayload } from '../../../testing/mock-permit-apply-action';
import { mockPostBuild, mockState } from '../../../testing/mock-state';
import { PFCModule } from '../pfc.module';
import { TypesComponent } from './types.component';

describe('TypesComponent', () => {
  let page: Page;
  let router: Router;
  let component: TypesComponent;
  let store: PermitApplicationStore;
  let activatedRoute: ActivatedRoute;
  let fixture: ComponentFixture<TypesComponent>;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub(
    {},
    {},
    { taskKey: 'monitoringApproaches.PFC.cellAndAnodeTypes', statusKey: 'pfcTypes' },
  );
  const mockedCellTypeValues = (
    mockPermitApplyPayload.permit.monitoringApproaches.PFC as PFCMonitoringApproach
  ).cellAndAnodeTypes.map((item) => item.cellType);
  const mockedAnodeTypeValues = (
    mockPermitApplyPayload.permit.monitoringApproaches.PFC as PFCMonitoringApproach
  ).cellAndAnodeTypes.map((item) => item.anodeType);

  class Page extends BasePage<TypesComponent> {
    get cellTypes() {
      return this.queryAll<HTMLInputElement>('input[id^="cellAndAnodeTypes"][id$="cellType"]');
    }

    get cellTypeValues() {
      return this.cellTypes.map((input) => input.value);
    }

    set cellTypeValues(values: string[]) {
      values.forEach((value, index) => this.setInputValue(`#cellAndAnodeTypes.${index}.cellType`, value));
    }

    get anodeTypes() {
      return this.queryAll<HTMLInputElement>('input[id^="cellAndAnodeTypes"][id$="anodeType"]');
    }

    get anodeTypeValues() {
      return this.anodeTypes.map((input) => input.value);
    }

    set anodeTypeValues(values: string[]) {
      values.forEach((value, index) => this.setInputValue(`#cellAndAnodeTypes.${index}.anodeType`, value));
    }

    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get errorSummaryList() {
      return Array.from(this.errorSummary.querySelectorAll('a')).map((anchor) => anchor.textContent.trim());
    }

    get addAnotherButton() {
      return this.query<HTMLButtonElement>('button.govuk-button--secondary:not(.moj-add-another__remove-button)');
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(TypesComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PFCModule, RouterTestingModule],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();
  });

  describe('for new cell and anode types', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState({
        ...mockState,
        permit: {
          ...mockState.permit,
          monitoringApproaches: {
            ...mockPermitApplyPayload.permit.monitoringApproaches,
            PFC: {
              ...mockPermitApplyPayload.permit.monitoringApproaches.PFC,
              cellAndAnodeTypes: null,
            } as PFCMonitoringApproach,
          },
        },
      });
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display a form with emprty values', () => {
      expect(page.cellTypes).toHaveLength(1);
      expect(page.cellTypeValues).toEqual(['']);
      expect(page.anodeTypes).toHaveLength(1);
      expect(page.anodeTypeValues).toEqual(['']);
      expect(page.errorSummary).toBeFalsy();
    });

    it('should submit a valid form', () => {
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryList).toEqual(['Enter a cell type', 'Enter an anode type']);

      page.submitButton.click();
      fixture.detectChanges();

      page.cellTypeValues = mockedCellTypeValues;
      page.anodeTypeValues = mockedAnodeTypeValues;

      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();

      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
        mockPostBuild({}, { ...mockPermitApplyPayload.permitSectionsCompleted, pfcTypes: [true] }),
      );

      expect(navigateSpy).toHaveBeenCalledWith(['summary'], {
        relativeTo: activatedRoute,
        state: { notification: true },
      });
    });
  });

  describe('for existing cell and anode types', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(mockState);
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should fill the form from the store', () => {
      expect(page.cellTypes).toHaveLength(1);
      expect(page.cellTypeValues).toEqual(mockedCellTypeValues);
      expect(page.anodeTypes).toHaveLength(1);
      expect(page.anodeTypeValues).toEqual(mockedAnodeTypeValues);
      expect(page.errorSummary).toBeFalsy();
    });

    it('should submit valid form', () => {
      page.addAnotherButton.click();
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryList).toEqual(['Enter a cell type', 'Enter an anode type']);

      mockedCellTypeValues.push('cell type two');
      page.cellTypeValues = mockedCellTypeValues;
      mockedAnodeTypeValues.push('anode type two');
      page.anodeTypeValues = mockedAnodeTypeValues;

      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
        mockPostBuild(
          {
            ...store.getState().permit,
            monitoringApproaches: {
              ...mockPermitApplyPayload.permit.monitoringApproaches,
              PFC: {
                ...mockPermitApplyPayload.permit.monitoringApproaches.PFC,
                cellAndAnodeTypes: [
                  {
                    cellType: 'cell type one',
                    anodeType: 'anode type one',
                  },
                  {
                    cellType: 'cell type two',
                    anodeType: 'anode type two',
                  },
                ],
                type: 'PFC',
              },
            },
          },
          { ...mockPermitApplyPayload.permitSectionsCompleted, pfcTypes: [true] },
        ),
      );

      expect(navigateSpy).toHaveBeenCalledWith(['summary'], {
        relativeTo: activatedRoute,
        state: { notification: true },
      });
    });
  });
});
