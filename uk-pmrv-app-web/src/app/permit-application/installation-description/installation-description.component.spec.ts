import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { BasePage, mockClass } from '../../../testing';
import { PermitApplicationModule } from '../permit-application.module';
import { PermitApplicationStore } from '../store/permit-application.store';
import { mockPermitApplyPayload } from '../testing/mock-permit-apply-action';
import { mockPostBuild, mockState, mockStateBuild } from '../testing/mock-state';
import { InstallationDescriptionComponent } from './installation-description.component';

describe('InstallationDescriptionComponent', () => {
  let page: Page;
  let router: Router;
  let store: PermitApplicationStore;
  let activatedRoute: ActivatedRoute;
  let component: InstallationDescriptionComponent;
  let fixture: ComponentFixture<InstallationDescriptionComponent>;

  const tasksService = mockClass(TasksService);
  const mockInstallationDescription = mockPermitApplyPayload.permit.installationDescription;

  class Page extends BasePage<InstallationDescriptionComponent> {
    get activitiesValue() {
      return this.getInputValue('#mainActivitiesDesc');
    }
    set activitiesValue(value: string) {
      this.setInputValue('#mainActivitiesDesc', value);
    }
    get siteDescriptionValue() {
      return this.getInputValue('#siteDescription');
    }
    set siteDescriptionValue(value: string) {
      this.setInputValue('#siteDescription', value);
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
    fixture = TestBed.createComponent(InstallationDescriptionComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PermitApplicationModule, RouterTestingModule],
      providers: [{ provide: TasksService, useValue: tasksService }],
    }).compileComponents();
  });

  describe('for new installation description', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(mockStateBuild({ installationDescription: null }));
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
      expect(page.errorSummaryErrorList).toEqual([
        'Enter the primary purpose of the installation',
        'Enter the description of the site',
      ]);

      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');

      page.activitiesValue = mockInstallationDescription.mainActivitiesDesc;
      page.siteDescriptionValue = mockInstallationDescription.siteDescription;
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
        mockPostBuild(undefined, { installationDescription: [true] }),
      );
      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['summary'], {
        relativeTo: activatedRoute,
        state: { notification: true },
      });
    });
  });

  describe('for existing installation description', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(mockState);
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should fill the form from the store', () => {
      expect(page.activitiesValue).toEqual(mockInstallationDescription.mainActivitiesDesc);
      expect(page.siteDescriptionValue).toEqual(mockInstallationDescription.siteDescription);
    });

    it('should submit a valid form, update the store and navigate to summary', () => {
      expect(page.errorSummary).toBeFalsy();

      page.activitiesValue = '';
      page.siteDescriptionValue = '';
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(Array.from(page.errorSummary.querySelectorAll('a')).map((el) => el.textContent.trim())).toEqual([
        'Enter the primary purpose of the installation',
        'Enter the description of the site',
      ]);

      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');

      page.activitiesValue = mockInstallationDescription.mainActivitiesDesc;
      page.siteDescriptionValue = mockInstallationDescription.siteDescription;
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
        mockPostBuild(undefined, { installationDescription: [true] }),
      );
      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['summary'], {
        relativeTo: activatedRoute,
        state: { notification: true },
      });
    });
  });
});
