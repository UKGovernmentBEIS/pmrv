import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { AerModule } from '@tasks/aer/aer.module';
import { PrtrComponent } from '@tasks/aer/submit/prtr/prtr.component';
import { mockPostBuild, mockStateBuild } from '@tasks/aer/submit/testing/mock-state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

describe('PrtrComponent', () => {
  let page: Page;
  let router: Router;
  let store: CommonTasksStore;
  let activatedRoute: ActivatedRoute;
  let component: PrtrComponent;
  let fixture: ComponentFixture<PrtrComponent>;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<PrtrComponent> {
    get existRadioButtons() {
      return this.queryAll<HTMLInputElement>('input[name$="exist"]');
    }
    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }
    get errorSummaryList() {
      return Array.from(this.errorSummary.querySelectorAll('a')).map((anchor) => anchor.textContent.trim());
    }
    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(PrtrComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AerModule, RouterTestingModule],
      providers: [KeycloakService, { provide: TasksService, useValue: tasksService }],
    }).compileComponents();
  });

  describe('for new prtr', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(mockStateBuild({ pollutantRegisterActivities: null }));
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should submit a valid form', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryList).toEqual(['Enter an option']);

      page.existRadioButtons[1].click();
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.existRadioButtons[1].checked).toBeTruthy();
      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
        mockPostBuild(
          { pollutantRegisterActivities: { activities: [], exist: false } },
          { pollutantRegisterActivities: [false] },
        ),
      );
      expect(navigateSpy).toHaveBeenCalledWith(['summary'], {
        relativeTo: activatedRoute,
        state: { notification: true },
      });
    });
  });

  describe('for existing prtr', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(mockStateBuild({ pollutantRegisterActivities: { exist: false } }));
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should submit a valid form', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');

      page.existRadioButtons[0].click();
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.existRadioButtons[0].checked).toBeTruthy();
      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(0);
      expect(navigateSpy).toHaveBeenCalledWith(['activity', 0], { relativeTo: activatedRoute });
    });
  });
});
