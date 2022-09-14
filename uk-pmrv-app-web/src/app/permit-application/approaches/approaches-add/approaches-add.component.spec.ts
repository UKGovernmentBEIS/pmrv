import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { cloneDeep } from 'lodash-es';

import { TasksService } from 'pmrv-api';

import { BasePage, mockClass } from '../../../../testing';
import { PermitApplicationModule } from '../../permit-application.module';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockPermitApplyPayload } from '../../testing/mock-permit-apply-action';
import { mockPostBuild, mockState, mockStateBuild } from '../../testing/mock-state';
import { ApproachesAddComponent } from './approaches-add.component';

describe('ApproachesAddComponent', () => {
  let page: Page;
  let router: Router;
  let route: ActivatedRoute;
  let store: PermitApplicationStore;
  let component: ApproachesAddComponent;
  let fixture: ComponentFixture<ApproachesAddComponent>;

  const tasksService = mockClass(TasksService);

  const monitoringApproaches = Object.keys(mockState.permit.monitoringApproaches)
    .filter((key) => key !== 'CALCULATION')
    .reduce((obj, key) => {
      obj[key] = mockState.permit.monitoringApproaches[key];
      return obj;
    }, {});

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
      imports: [PermitApplicationModule, RouterTestingModule],
      providers: [{ provide: TasksService, useValue: tasksService }],
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

  describe('selecting monitoring approaches for first time', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(mockStateBuild({ monitoringApproaches: undefined }));
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display all available monitoring approaches', () => {
      expect(page.monitoringApproaches.length).toEqual(7);
    });

    it('should submit a valid form, update the store and navigate correctly', () => {
      store.setState(mockStateBuild({ monitoringApproaches: undefined }));
      fixture.detectChanges();

      expect(page.monitoringApproaches.length).toEqual(7);
      page.monitoringApproaches.forEach((item) => expect(item.checked).toBe(false));
      expect(page.errorSummary).toBeFalsy();

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryErrorList).toEqual(['Select at least one monitoring approach']);

      page.monitoringApproaches[0].click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();

      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');

      page.submitButton.click();
      fixture.detectChanges();
      expect(page.errorSummary).toBeFalsy();

      const selectedMonitoringApproaches = {
        CALCULATION: {
          type: 'CALCULATION',
        },
      };

      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
        mockPostBuild({ monitoringApproaches: selectedMonitoringApproaches }, { monitoringApproaches: [false] }),
      );
      expect(store.permit.monitoringApproaches).toEqual(selectedMonitoringApproaches);
      expect(navigateSpy).toHaveBeenCalledWith(['..'], { relativeTo: route });
    });
  });

  describe('adding more monitoring approaches', () => {
    const clonedPermitApplyActionPayload = cloneDeep(mockPermitApplyPayload);
    delete clonedPermitApplyActionPayload.permit.monitoringApproaches.CALCULATION;

    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(mockStateBuild({ monitoringApproaches }));
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should submit a valid form, update the store and navigate correctly', () => {
      expect(page.monitoringApproaches.length).toEqual(1);
      page.monitoringApproaches.forEach((item) => expect(item.checked).toBe(false));
      expect(page.errorSummary).toBeFalsy();

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryErrorList).toEqual(['Select at least one monitoring approach']);

      page.monitoringApproaches[0].click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();

      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');

      page.submitButton.click();
      fixture.detectChanges();
      expect(page.errorSummary).toBeFalsy();

      const selectedMonitoringApproaches = {
        ...mockPermitApplyPayload.permit.monitoringApproaches,
        CALCULATION: {
          type: 'CALCULATION',
        },
      };

      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
        mockPostBuild({ monitoringApproaches: selectedMonitoringApproaches }, { monitoringApproaches: [false] }),
      );
      expect(store.permit.monitoringApproaches).toEqual(selectedMonitoringApproaches);
      expect(navigateSpy).toHaveBeenCalledWith(['..'], { relativeTo: route });
    });
  });
});
