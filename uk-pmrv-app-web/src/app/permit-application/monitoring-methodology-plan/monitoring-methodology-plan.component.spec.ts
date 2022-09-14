import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../testing';
import { SharedModule } from '../../shared/shared.module';
import { PermitApplicationModule } from '../permit-application.module';
import { SharedPermitModule } from '../shared/shared-permit.module';
import { PermitApplicationStore } from '../store/permit-application.store';
import { mockPostBuild, mockState, mockStateBuild } from '../testing/mock-state';
import { MonitoringMethodologyPlanComponent } from './monitoring-methodology-plan.component';

describe('MonitoringMethodologyPlanComponent', () => {
  let page: Page;
  let store: PermitApplicationStore;
  let component: MonitoringMethodologyPlanComponent;
  let fixture: ComponentFixture<MonitoringMethodologyPlanComponent>;

  const route = new ActivatedRouteStub({}, {}, { permitTask: 'monitoringMethodologyPlans' });

  class Page extends BasePage<MonitoringMethodologyPlanComponent> {
    get existRadios() {
      return this.queryAll<HTMLInputElement>('input[name$="exist"]');
    }

    get continueButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }
  }

  const tasksService = mockClass(TasksService);
  const createComponent = () => {
    fixture = TestBed.createComponent(MonitoringMethodologyPlanComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule, SharedPermitModule, PermitApplicationModule],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();
  });

  describe('for new monitoring methodology plan', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(
        mockStateBuild(
          {
            monitoringMethodologyPlans: {},
          },
          {
            monitoringMethodologyPlans: [false],
          },
        ),
      );
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should submit a valid form', () => {
      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));

      expect(page.errorSummary).toBeFalsy();

      page.continueButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();

      page.existRadios[0].click();
      page.continueButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();

      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
        mockPostBuild(
          {
            monitoringMethodologyPlans: {
              exist: true,
            },
          },
          {
            ...mockState.permitSectionsCompleted,
            monitoringMethodologyPlans: [false],
          },
        ),
      );
    });
  });

  describe('for existing monitoring methodology plan', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(
        mockStateBuild(
          {
            monitoringMethodologyPlans: {
              exist: true,
            },
          },
          {
            monitoringMethodologyPlans: [false],
          },
        ),
      );
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should fill the form', () => {
      expect(page.errorSummary).toBeFalsy();

      expect(page.existRadios.length).toEqual(2);
      expect(page.existRadios[0].checked).toBeTruthy();
      expect(page.existRadios[1].checked).toBeFalsy();
    });
  });
});
