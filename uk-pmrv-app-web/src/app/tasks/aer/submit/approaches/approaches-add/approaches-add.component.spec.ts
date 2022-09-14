import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { AerModule } from '@tasks/aer/aer.module';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

import { mockPostBuild, mockStateBuild } from '../../testing/mock-state';
import { ApproachesAddComponent } from './approaches-add.component';

describe('ApproachesAddComponent', () => {
  let page: Page;
  let router: Router;
  let route: ActivatedRoute;
  let store: CommonTasksStore;
  let component: ApproachesAddComponent;
  let fixture: ComponentFixture<ApproachesAddComponent>;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<ApproachesAddComponent> {
    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }

    get monitoringApproaches() {
      return this.queryAll<HTMLInputElement>('.govuk-checkboxes__input');
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

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AerModule, RouterTestingModule],
      providers: [KeycloakService, { provide: TasksService, useValue: tasksService }],
    }).compileComponents();
  });

  const createComponent = () => {
    fixture = TestBed.createComponent(ApproachesAddComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    route = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  describe('selecting monitoring approaches', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(mockStateBuild({ monitoringApproachTypes: [] }));
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display all available monitoring approaches', () => {
      expect(page.monitoringApproaches.length).toEqual(7);
    });

    it('should submit a valid form, update the store and navigate correctly', () => {
      store.setState(mockStateBuild({ monitoringApproachTypes: [] }));
      fixture.detectChanges();

      expect(page.monitoringApproaches.length).toEqual(7);
      page.monitoringApproaches.forEach((item) => expect(item.checked).toBe(false));
      expect(page.errorSummary).toBeFalsy();

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryErrorList).toEqual(['Select at least one monitoring approach']);

      page.monitoringApproaches[0].click();
      page.monitoringApproaches[1].click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();

      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');

      page.submitButton.click();
      fixture.detectChanges();
      expect(page.errorSummary).toBeFalsy();

      const selectedMonitoringApproaches = ['CALCULATION', 'MEASUREMENT'];

      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
        mockPostBuild({ monitoringApproachTypes: selectedMonitoringApproaches }, { monitoringApproachTypes: [false] }),
      );
      expect((store.getState().requestTaskItem.requestTask.payload as any).aer.monitoringApproachTypes).toEqual(
        selectedMonitoringApproaches,
      );
      expect(navigateSpy).toHaveBeenCalledWith(['..'], { relativeTo: route });
    });
  });
});
