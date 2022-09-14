import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { monitoringApproachTypeOptions } from '@shared/components/approaches/approaches-options';
import { AerModule } from '@tasks/aer/aer.module';
import { mockAerApplyPayload } from '@tasks/aer/submit/testing/mock-permit-apply-action';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

import { mockPostBuild, mockStateBuild } from '../testing/mock-state';
import { ApproachesComponent } from './approaches.component';

describe('ApproachesComponent', () => {
  let page: Page;
  let router: Router;
  let route: ActivatedRoute;
  let store: CommonTasksStore;
  let component: ApproachesComponent;
  let fixture: ComponentFixture<ApproachesComponent>;

  const tasksService = mockClass(TasksService);
  const availableMonitoringApproaches = monitoringApproachTypeOptions;

  const monitoringApproachTypes = mockAerApplyPayload.aer.monitoringApproachTypes.filter(
    (key) => key !== 'CALCULATION',
  );

  class Page extends BasePage<ApproachesComponent> {
    get confirmButton() {
      return Array.from(this.queryAll<HTMLButtonElement>('button[type="button"]')).find(
        (button) => button.textContent.trim() === 'Confirm and complete',
      );
    }

    get addMonitoringApproachesButton() {
      return Array.from(this.queryAll<HTMLButtonElement>('button[type="button"]')).find(
        (button) => button.textContent.trim() === 'Add monitoring approaches',
      );
    }

    get addAnotherMonitoringApproachButton() {
      return Array.from(this.queryAll<HTMLButtonElement>('button[type="button"]')).find(
        (button) => button.textContent.trim() === 'Add another monitoring approach',
      );
    }

    get monitoringApproaches() {
      return this.queryAll<HTMLDListElement>('dl').map((monitoringApproach) =>
        Array.from(monitoringApproach.querySelectorAll('dt')).map((dd) => dd.textContent.trim()),
      );
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AerModule, RouterTestingModule],
      providers: [KeycloakService, { provide: TasksService, useValue: tasksService }],
    }).compileComponents();
  });

  const createComponent = () => {
    fixture = TestBed.createComponent(ApproachesComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    route = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  describe('defining monitoring approaches for first time', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(mockStateBuild({ monitoringApproachTypes: [] }));
    });
    beforeEach(createComponent);

    it('should create component', () => {
      expect(component).toBeTruthy();
    });

    it('should display add monitoring approaches button and hide all others', () => {
      expect(page.addMonitoringApproachesButton).toBeTruthy();
      expect(page.confirmButton).toBeFalsy();
      expect(page.addAnotherMonitoringApproachButton).toBeFalsy();
    });
  });

  describe('adding more monitoring approaches', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(mockStateBuild({ monitoringApproachTypes }));
    });
    beforeEach(createComponent);

    it('should create component', () => {
      expect(component).toBeTruthy();
    });

    it('should display add another and confirm buttons, but hide add button', () => {
      expect(page.confirmButton).toBeTruthy();
      expect(page.addAnotherMonitoringApproachButton).toBeTruthy();
      expect(page.addMonitoringApproachesButton).toBeFalsy();
    });

    it('should display selected monitoring approaches', () => {
      expect(page.monitoringApproaches).toEqual([['Measurement approach']]);
    });

    it('should submit selected monitoring approaches and navigate to summary page', () => {
      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');

      page.confirmButton.click();

      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
        mockPostBuild({ monitoringApproachTypes }),
      );
      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['..'], { relativeTo: route });
    });
  });

  describe('adding more monitoring approaches not permitted', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(mockStateBuild({ monitoringApproachTypes: availableMonitoringApproaches }));
    });
    beforeEach(createComponent);

    it('should create component', () => {
      expect(component).toBeTruthy();
    });

    it('should display confirm button, but hide add and add another button', () => {
      expect(page.confirmButton).toBeTruthy();
      expect(page.addAnotherMonitoringApproachButton).toBeFalsy();
      expect(page.addMonitoringApproachesButton).toBeFalsy();
    });

    it('should display selected monitoring approaches', () => {
      expect(page.monitoringApproaches).toEqual([
        ['Calculation approach'],
        ['Measurement approach'],
        ['Fall-back approach'],
        ['Nitrous oxide (N2O) approach'],
        ['Perfluorocarbons (PFC) approach'],
        ['Inherent CO2 approach'],
        ['Transferred CO2 approach'],
      ]);
    });

    it('should submit selected monitoring approaches and navigate to summary page', () => {
      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');

      page.confirmButton.click();

      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
        mockPostBuild({ monitoringApproachTypes: availableMonitoringApproaches }, { monitoringApproachTypes: [true] }),
      );
      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['..'], { relativeTo: route });
    });
  });
});
