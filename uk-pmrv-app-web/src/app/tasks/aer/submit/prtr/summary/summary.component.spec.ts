import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { AerModule } from '@tasks/aer/aer.module';
import { SummaryComponent } from '@tasks/aer/submit/prtr/summary/summary.component';
import { mockState } from '@tasks/aer/submit/testing/mock-permit-apply-action';
import { mockPostBuild, mockStateBuild } from '@tasks/aer/submit/testing/mock-state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

describe('SummaryComponent', () => {
  let page: Page;
  let router: Router;
  let component: SummaryComponent;
  let fixture: ComponentFixture<SummaryComponent>;
  let store: CommonTasksStore;
  let activatedRoute: ActivatedRoute;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<SummaryComponent> {
    get activities() {
      return this.queryAll<HTMLDListElement>('tr');
    }
    get activitiesTextContents() {
      return this.activities.map((sourceStream) =>
        Array.from(sourceStream.querySelectorAll('td')).map((td) => td.textContent.trim()),
      );
    }
    get addAnotherBtn() {
      return Array.from(this.queryAll<HTMLButtonElement>('button[type="button"]')).find(
        (button) => button.textContent.trim() === 'Add another',
      );
    }
    get notificationBanner() {
      return this.query('.govuk-notification-banner');
    }
    get summaryHeader() {
      return this.query<HTMLHeadingElement>('.govuk-heading-m');
    }
    get summaryDefinitions() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
    get submitButton() {
      return Array.from(this.queryAll<HTMLButtonElement>('button[type="button"]')).find(
        (button) => button.textContent.trim() === 'Confirm and complete',
      );
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AerModule, RouterTestingModule],
      providers: [KeycloakService, { provide: TasksService, useValue: tasksService }],
    }).compileComponents();
  });

  function createComponent() {
    store = TestBed.inject(CommonTasksStore);
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);

    fixture = TestBed.createComponent(SummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  }

  describe('summary with activities', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(
        mockStateBuild({
          pollutantRegisterActivities: {
            exist: true,
            activities: ['_1_A_2_C_CHEMICALS', '_1_A_3_C_RAILWAYS', '_1_B_2_A_OIL'],
          },
        }),
      );
      router = TestBed.inject(Router);
      jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { notification: true } } } as any);
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should show add another and complete button', () => {
      expect(page.submitButton).toBeTruthy();
      expect(page.addAnotherBtn).toBeTruthy();
      expect(page.activities.length).toEqual(4);
    });

    it('should display the activities and submit status', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));

      expect(page.summaryDefinitions).toEqual([]);
      expect(page.activitiesTextContents).toHaveLength(4);
      expect(page.activitiesTextContents).toEqual([
        [],
        ['Main activity', '1.A.2.c Chemicals', 'Delete'],
        ['Main activity', '1.A.3.c Railways', 'Delete'],
        ['Main activity', '1.B.2.a Oil', 'Delete'],
      ]);
      expect(page.notificationBanner).toBeTruthy();
      expect(page.summaryHeader.textContent).toEqual('EPRTR codes added');

      page.submitButton.click();
      fixture.detectChanges();

      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
        mockPostBuild(
          {
            pollutantRegisterActivities: {
              activities: ['_1_A_2_C_CHEMICALS', '_1_A_3_C_RAILWAYS', '_1_B_2_A_OIL'],
              exist: true,
            },
          },
          { pollutantRegisterActivities: [true] },
        ),
      );
      expect(navigateSpy).toHaveBeenCalledWith(['../..'], { relativeTo: activatedRoute });
    });
  });

  describe('summary with no activities', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(mockState);
      router = TestBed.inject(Router);
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should not show add another button', () => {
      expect(page.submitButton).toBeTruthy();
      expect(page.addAnotherBtn).toBeFalsy();
      expect(page.activities.length).toEqual(0);
    });

    it('should display the summary and submit status', () => {
      expect(page.summaryDefinitions).toEqual([
        ['Are emissions from the installation reported under the Pollutant Release and Transfer Register?', 'No'],
      ]);
      expect(page.summaryHeader).toBeFalsy();

      page.submitButton.click();
      fixture.detectChanges();

      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
        mockPostBuild(
          {
            pollutantRegisterActivities: {
              activities: undefined,
              exist: false,
            },
          },
          { pollutantRegisterActivities: [true] },
        ),
      );
    });
  });
});
