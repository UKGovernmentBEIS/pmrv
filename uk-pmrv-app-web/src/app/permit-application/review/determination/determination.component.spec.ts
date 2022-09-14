import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../../testing';
import { SharedModule } from '../../../shared/shared.module';
import { SharedPermitModule } from '../../shared/shared-permit.module';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockDeterminationPostBuild, mockReviewState } from '../../testing/mock-state';
import { DeterminationComponent } from './determination.component';

describe('DeterminationComponent', () => {
  let page: Page;
  let router: Router;
  let store: PermitApplicationStore;
  let component: DeterminationComponent;
  let fixture: ComponentFixture<DeterminationComponent>;
  const route = new ActivatedRouteStub({}, {}, { statusKey: 'determination' });

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<DeterminationComponent> {
    get buttons() {
      return this.queryAll<HTMLLIElement>('button');
    }

    get grantButton() {
      return this.buttons.filter((el) => el.innerHTML.trim() === 'Grant')[0];
    }

    get rejectButton() {
      return this.buttons.filter((el) => el.innerHTML.trim() === 'Reject')[0];
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(DeterminationComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule, SharedPermitModule],
      declarations: [DeterminationComponent],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();
  });

  describe('for GRANTED determination not allowed (missing mandatory group)', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState({
        ...mockReviewState,
        reviewGroupDecisions: {
          FUELS_AND_EQUIPMENT: { type: 'ACCEPTED', notes: 'notes' },
          INSTALLATION_DETAILS: { type: 'ACCEPTED', notes: 'notes' },
          MANAGEMENT_PROCEDURES: { type: 'ACCEPTED', notes: 'notes' },
          MONITORING_METHODOLOGY_PLAN: { type: 'ACCEPTED', notes: 'notes' },
          ADDITIONAL_INFORMATION: { type: 'ACCEPTED', notes: 'notes' },
          DEFINE_MONITORING_APPROACHES: { type: 'ACCEPTED', notes: 'notes' },
          UNCERTAINTY_ANALYSIS: { type: 'ACCEPTED', notes: 'notes' },
          CALCULATION: { type: 'ACCEPTED', notes: 'notes' },
          PFC: { type: 'ACCEPTED', notes: 'notes' },
          N2O: { type: 'ACCEPTED', notes: 'notes' },
          INHERENT_CO2: { type: 'ACCEPTED', notes: 'notes' },
          TRANSFERRED_CO2: { type: 'ACCEPTED', notes: 'notes' },
          FALLBACK: { type: 'ACCEPTED', notes: 'notes' },
          MEASUREMENT: { type: 'ACCEPTED', notes: 'notes' },
        },
        reviewSectionsCompleted: {
          FUELS_AND_EQUIPMENT: true,
          INSTALLATION_DETAILS: true,
          MANAGEMENT_PROCEDURES: true,
          MONITORING_METHODOLOGY_PLAN: true,
          ADDITIONAL_INFORMATION: true,
          DEFINE_MONITORING_APPROACHES: true,
          UNCERTAINTY_ANALYSIS: true,
          CALCULATION: true,
          PFC: true,
          N2O: true,
          INHERENT_CO2: true,
          TRANSFERRED_CO2: true,
          FALLBACK: true,
          MEASUREMENT: true,
        },
      });
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should show only deem withdrawn button', () => {
      expect(page.buttons.length).toEqual(1);
      expect(page.buttons[0].innerHTML.trim()).toEqual('Deem withdrawn');
    });
  });

  describe('for GRANTED determination allowed', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState({
        ...mockReviewState,
        reviewGroupDecisions: {
          PERMIT_TYPE: { type: 'ACCEPTED', notes: 'notes' },
          CONFIDENTIALITY_STATEMENT: { type: 'ACCEPTED', notes: 'notes' },
          FUELS_AND_EQUIPMENT: { type: 'ACCEPTED', notes: 'notes' },
          INSTALLATION_DETAILS: { type: 'ACCEPTED', notes: 'notes' },
          MANAGEMENT_PROCEDURES: { type: 'ACCEPTED', notes: 'notes' },
          MONITORING_METHODOLOGY_PLAN: { type: 'ACCEPTED', notes: 'notes' },
          ADDITIONAL_INFORMATION: { type: 'ACCEPTED', notes: 'notes' },
          DEFINE_MONITORING_APPROACHES: { type: 'ACCEPTED', notes: 'notes' },
          UNCERTAINTY_ANALYSIS: { type: 'ACCEPTED', notes: 'notes' },
          CALCULATION: { type: 'ACCEPTED', notes: 'notes' },
          PFC: { type: 'ACCEPTED', notes: 'notes' },
          N2O: { type: 'ACCEPTED', notes: 'notes' },
          INHERENT_CO2: { type: 'ACCEPTED', notes: 'notes' },
          TRANSFERRED_CO2: { type: 'ACCEPTED', notes: 'notes' },
          FALLBACK: { type: 'ACCEPTED', notes: 'notes' },
          MEASUREMENT: { type: 'ACCEPTED', notes: 'notes' },
        },
        reviewSectionsCompleted: {
          PERMIT_TYPE: true,
          CONFIDENTIALITY_STATEMENT: true,
          FUELS_AND_EQUIPMENT: true,
          INSTALLATION_DETAILS: true,
          MANAGEMENT_PROCEDURES: true,
          MONITORING_METHODOLOGY_PLAN: true,
          ADDITIONAL_INFORMATION: true,
          DEFINE_MONITORING_APPROACHES: true,
          UNCERTAINTY_ANALYSIS: true,
          CALCULATION: true,
          PFC: true,
          N2O: true,
          INHERENT_CO2: true,
          TRANSFERRED_CO2: true,
          FALLBACK: true,
          MEASUREMENT: true,
        },
      });
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should show grant and deem withdrawn button', () => {
      expect(page.buttons.length).toEqual(2);
      expect(page.buttons[0].innerHTML.trim()).toEqual('Grant');
      expect(page.buttons[1].innerHTML.trim()).toEqual('Deem withdrawn');
    });
  });

  describe('for REJECTED determination allowed', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState({
        ...mockReviewState,
        reviewGroupDecisions: {
          PERMIT_TYPE: { type: 'ACCEPTED', notes: 'notes' },
          CONFIDENTIALITY_STATEMENT: { type: 'REJECTED', notes: 'notes' },
          FUELS_AND_EQUIPMENT: { type: 'ACCEPTED', notes: 'notes' },
          INSTALLATION_DETAILS: { type: 'ACCEPTED', notes: 'notes' },
          MANAGEMENT_PROCEDURES: { type: 'ACCEPTED', notes: 'notes' },
          MONITORING_METHODOLOGY_PLAN: { type: 'ACCEPTED', notes: 'notes' },
          ADDITIONAL_INFORMATION: { type: 'ACCEPTED', notes: 'notes' },
          DEFINE_MONITORING_APPROACHES: { type: 'ACCEPTED', notes: 'notes' },
          UNCERTAINTY_ANALYSIS: { type: 'ACCEPTED', notes: 'notes' },
          CALCULATION: { type: 'ACCEPTED', notes: 'notes' },
          PFC: { type: 'ACCEPTED', notes: 'notes' },
          N2O: { type: 'ACCEPTED', notes: 'notes' },
          INHERENT_CO2: { type: 'ACCEPTED', notes: 'notes' },
          TRANSFERRED_CO2: { type: 'ACCEPTED', notes: 'notes' },
          FALLBACK: { type: 'ACCEPTED', notes: 'notes' },
          MEASUREMENT: { type: 'ACCEPTED', notes: 'notes' },
        },
        reviewSectionsCompleted: {
          PERMIT_TYPE: true,
          CONFIDENTIALITY_STATEMENT: true,
          FUELS_AND_EQUIPMENT: true,
          INSTALLATION_DETAILS: true,
          MANAGEMENT_PROCEDURES: true,
          MONITORING_METHODOLOGY_PLAN: true,
          ADDITIONAL_INFORMATION: true,
          DEFINE_MONITORING_APPROACHES: true,
          UNCERTAINTY_ANALYSIS: true,
          CALCULATION: true,
          PFC: true,
          N2O: true,
          INHERENT_CO2: true,
          TRANSFERRED_CO2: true,
          FALLBACK: true,
          MEASUREMENT: true,
        },
      });
    });

    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should show only deem withdrawn button', () => {
      expect(page.buttons.length).toEqual(2);
      expect(page.buttons[0].innerHTML.trim()).toEqual('Reject');
      expect(page.buttons[1].innerHTML.trim()).toEqual('Deem withdrawn');
    });
  });

  describe('for posting permit determination GRANTED', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState({
        ...mockReviewState,
        reviewGroupDecisions: {
          PERMIT_TYPE: { type: 'ACCEPTED', notes: 'notes' },
          CONFIDENTIALITY_STATEMENT: { type: 'ACCEPTED', notes: 'notes' },
          FUELS_AND_EQUIPMENT: { type: 'ACCEPTED', notes: 'notes' },
          INSTALLATION_DETAILS: { type: 'ACCEPTED', notes: 'notes' },
          MANAGEMENT_PROCEDURES: { type: 'ACCEPTED', notes: 'notes' },
          MONITORING_METHODOLOGY_PLAN: { type: 'ACCEPTED', notes: 'notes' },
          ADDITIONAL_INFORMATION: { type: 'ACCEPTED', notes: 'notes' },
          DEFINE_MONITORING_APPROACHES: { type: 'ACCEPTED', notes: 'notes' },
          UNCERTAINTY_ANALYSIS: { type: 'ACCEPTED', notes: 'notes' },
          CALCULATION: { type: 'ACCEPTED', notes: 'notes' },
          PFC: { type: 'ACCEPTED', notes: 'notes' },
          N2O: { type: 'ACCEPTED', notes: 'notes' },
          INHERENT_CO2: { type: 'ACCEPTED', notes: 'notes' },
          TRANSFERRED_CO2: { type: 'ACCEPTED', notes: 'notes' },
          FALLBACK: { type: 'ACCEPTED', notes: 'notes' },
          MEASUREMENT: { type: 'ACCEPTED', notes: 'notes' },
        },
        reviewSectionsCompleted: {
          PERMIT_TYPE: true,
          CONFIDENTIALITY_STATEMENT: true,
          FUELS_AND_EQUIPMENT: true,
          INSTALLATION_DETAILS: true,
          MANAGEMENT_PROCEDURES: true,
          MONITORING_METHODOLOGY_PLAN: true,
          ADDITIONAL_INFORMATION: true,
          DEFINE_MONITORING_APPROACHES: true,
          UNCERTAINTY_ANALYSIS: true,
          CALCULATION: true,
          PFC: true,
          N2O: true,
          INHERENT_CO2: true,
          TRANSFERRED_CO2: true,
          FALLBACK: true,
          MEASUREMENT: true,
        },
      });
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should post a permit determination GRANTED', () => {
      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');

      page.grantButton.click();
      fixture.detectChanges();

      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith({
        ...mockDeterminationPostBuild(
          { type: 'GRANTED' },
          {
            PERMIT_TYPE: true,
            CONFIDENTIALITY_STATEMENT: true,
            FUELS_AND_EQUIPMENT: true,
            INSTALLATION_DETAILS: true,
            MANAGEMENT_PROCEDURES: true,
            MONITORING_METHODOLOGY_PLAN: true,
            ADDITIONAL_INFORMATION: true,
            DEFINE_MONITORING_APPROACHES: true,
            UNCERTAINTY_ANALYSIS: true,
            CALCULATION: true,
            PFC: true,
            N2O: true,
            INHERENT_CO2: true,
            TRANSFERRED_CO2: true,
            FALLBACK: true,
            MEASUREMENT: true,
            determination: false,
          },
        ),
      });

      expect(navigateSpy).toHaveBeenCalledWith(['reason'], { relativeTo: route });
    });
  });

  describe('for posting permit determination REJECTED', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState({
        ...mockReviewState,
        reviewGroupDecisions: {
          PERMIT_TYPE: { type: 'ACCEPTED', notes: 'notes' },
          CONFIDENTIALITY_STATEMENT: { type: 'REJECTED', notes: 'notes' },
          FUELS_AND_EQUIPMENT: { type: 'ACCEPTED', notes: 'notes' },
          INSTALLATION_DETAILS: { type: 'ACCEPTED', notes: 'notes' },
          MANAGEMENT_PROCEDURES: { type: 'ACCEPTED', notes: 'notes' },
          MONITORING_METHODOLOGY_PLAN: { type: 'ACCEPTED', notes: 'notes' },
          ADDITIONAL_INFORMATION: { type: 'ACCEPTED', notes: 'notes' },
          DEFINE_MONITORING_APPROACHES: { type: 'ACCEPTED', notes: 'notes' },
          UNCERTAINTY_ANALYSIS: { type: 'ACCEPTED', notes: 'notes' },
          CALCULATION: { type: 'ACCEPTED', notes: 'notes' },
          PFC: { type: 'ACCEPTED', notes: 'notes' },
          N2O: { type: 'ACCEPTED', notes: 'notes' },
          INHERENT_CO2: { type: 'ACCEPTED', notes: 'notes' },
          TRANSFERRED_CO2: { type: 'ACCEPTED', notes: 'notes' },
          FALLBACK: { type: 'ACCEPTED', notes: 'notes' },
          MEASUREMENT: { type: 'ACCEPTED', notes: 'notes' },
        },
        reviewSectionsCompleted: {
          PERMIT_TYPE: true,
          CONFIDENTIALITY_STATEMENT: true,
          FUELS_AND_EQUIPMENT: true,
          INSTALLATION_DETAILS: true,
          MANAGEMENT_PROCEDURES: true,
          MONITORING_METHODOLOGY_PLAN: true,
          ADDITIONAL_INFORMATION: true,
          DEFINE_MONITORING_APPROACHES: true,
          UNCERTAINTY_ANALYSIS: true,
          CALCULATION: true,
          PFC: true,
          N2O: true,
          INHERENT_CO2: true,
          TRANSFERRED_CO2: true,
          FALLBACK: true,
          MEASUREMENT: true,
        },
      });
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should post a permit determination REJECTED', () => {
      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');

      page.rejectButton.click();
      fixture.detectChanges();

      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
        mockDeterminationPostBuild(
          { type: 'REJECTED' },
          {
            PERMIT_TYPE: true,
            CONFIDENTIALITY_STATEMENT: true,
            FUELS_AND_EQUIPMENT: true,
            INSTALLATION_DETAILS: true,
            MANAGEMENT_PROCEDURES: true,
            MONITORING_METHODOLOGY_PLAN: true,
            ADDITIONAL_INFORMATION: true,
            DEFINE_MONITORING_APPROACHES: true,
            UNCERTAINTY_ANALYSIS: true,
            CALCULATION: true,
            PFC: true,
            N2O: true,
            INHERENT_CO2: true,
            TRANSFERRED_CO2: true,
            FALLBACK: true,
            MEASUREMENT: true,
            determination: false,
          },
        ),
      );

      expect(navigateSpy).toHaveBeenCalledWith(['reason'], { relativeTo: route });
    });
  });
});
