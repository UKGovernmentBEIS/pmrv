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
import { DescriptionComponent } from './description.component';

describe('DescriptionComponent', () => {
  let page: Page;
  let router: Router;
  let store: PermitApplicationStore;
  let component: DescriptionComponent;
  let fixture: ComponentFixture<DescriptionComponent>;

  const route = new ActivatedRouteStub({}, {}, { taskKey: 'monitoringApproaches.PFC', statusKey: 'pfcDescription' });
  const tasksService = mockClass(TasksService);

  class Page extends BasePage<DescriptionComponent> {
    get approachDescription() {
      return this.getInputValue('#approachDescription');
    }
    set approachDescription(value: string) {
      this.setInputValue('#approachDescription', value);
    }

    get submitButton() {
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
    fixture = TestBed.createComponent(DescriptionComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
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

  describe('for new pfc approach description', () => {
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
              approachDescription: undefined,
            } as PFCMonitoringApproach,
          },
        },
      });
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
      expect(page.errorSummaryErrorList).toEqual(['Enter the approach description used to determine perfluorocarbons']);

      page.approachDescription = 'PFC approach description';

      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
        mockPostBuild(
          {
            ...store.getState().permit,
            monitoringApproaches: {
              ...store.getState().permit.monitoringApproaches,
              PFC: {
                ...store.getState().permit.monitoringApproaches.PFC,
                approachDescription: 'PFC approach description',
              } as PFCMonitoringApproach,
            },
          },
          { ...mockPermitApplyPayload.permitSectionsCompleted, pfcDescription: [true] },
        ),
      );

      expect(navigateSpy).toHaveBeenCalledWith(['summary'], {
        relativeTo: route,
        state: { notification: true },
      });
    });
  });

  describe('for existing pfc approach description', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(mockState);
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should fill the form from the store', () => {
      expect(page.approachDescription).toEqual(store.getState().permit.monitoringApproaches.PFC['approachDescription']);
    });

    it('should submit a valid form, update the store and navigate to summary', () => {
      expect(page.errorSummary).toBeFalsy();

      page.approachDescription = '';

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryErrorList).toEqual(['Enter the approach description used to determine perfluorocarbons']);

      page.approachDescription = 'PFC approach description';

      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
        mockPostBuild(
          {
            ...store.getState().permit,
            monitoringApproaches: {
              ...store.getState().permit.monitoringApproaches,
              PFC: {
                ...store.getState().permit.monitoringApproaches.PFC,
                approachDescription: 'PFC approach description',
              } as PFCMonitoringApproach,
            },
          },
          { ...mockPermitApplyPayload.permitSectionsCompleted, pfcDescription: [true] },
        ),
      );

      expect(navigateSpy).toHaveBeenCalledWith(['summary'], {
        relativeTo: route,
        state: { notification: true },
      });
    });
  });
});
