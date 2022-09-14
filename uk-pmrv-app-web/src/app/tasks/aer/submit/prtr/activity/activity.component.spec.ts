import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { AerModule } from '@tasks/aer/aer.module';
import { ActivityComponent } from '@tasks/aer/submit/prtr/activity/activity.component';
import { mockPostBuild, mockStateBuild } from '@tasks/aer/submit/testing/mock-state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

describe('ActivityComponent', () => {
  let page: Page;
  let store: CommonTasksStore;
  let component: ActivityComponent;
  let fixture: ComponentFixture<ActivityComponent>;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<ActivityComponent> {
    get activityRadioButtons() {
      return this.queryAll<HTMLInputElement>('input[name$="activity"]');
    }

    get subActivityA2RadioButtons() {
      return this.queryAll<HTMLInputElement>('input[name$="subActivityA2"]');
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
    fixture = TestBed.createComponent(ActivityComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AerModule, RouterTestingModule],
      providers: [
        KeycloakService,
        { provide: TasksService, useValue: tasksService },
        {
          provide: ActivatedRoute,
          useValue: {
            queryParams: of({ activityItem: '_1_A' }),
            paramMap: of(convertToParamMap({ index: 0 })),
            snapshot: {
              queryParams: { activityItem: '_1_A' },
              paramMap: convertToParamMap({ index: 0 }),
            },
          },
        },
      ],
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

    it('should show activities and submit form', () => {
      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));

      expect(page.activityRadioButtons.map((el) => el.value)).toEqual([
        '_1_A_1',
        '_1_A_2',
        '_1_A_3',
        '_1_A_4',
        '_1_A_5',
      ]);

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryList).toEqual(['Enter the activity']);

      page.activityRadioButtons[1].click();
      fixture.detectChanges();

      expect(page.subActivityA2RadioButtons.map((el) => el.value)).toEqual([
        '_1_A_2_A',
        '_1_A_2_B',
        '_1_A_2_C',
        '_1_A_2_D',
        '_1_A_2_E',
        '_1_A_2_F',
        '_1_A_2_GVII',
        '_1_A_2_GVIII',
      ]);

      page.subActivityA2RadioButtons[1].click();
      page.submitButton.click();
      fixture.detectChanges();

      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
        mockPostBuild(
          {
            pollutantRegisterActivities: {
              activities: ['_1_A_2_B_NON_FERROUS_METALS'],
              exist: true,
            },
          },
          { pollutantRegisterActivities: [false] },
        ),
      );
    });
  });

  describe('for existing prtr', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(
        mockStateBuild({
          pollutantRegisterActivities: {
            activities: ['_1_A_2_B_NON_FERROUS_METALS', '_2_D_3_OTHER', '_2_H_OTHER'],
            exist: true,
          },
        }),
      );
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should show activities and submit form', () => {
      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));

      expect(page.activityRadioButtons.map((el) => el.value)).toEqual([
        '_1_A_1',
        '_1_A_2',
        '_1_A_3',
        '_1_A_4',
        '_1_A_5',
      ]);
      page.activityRadioButtons[1].click();
      fixture.detectChanges();

      page.subActivityA2RadioButtons[0].click();
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
        mockPostBuild(
          {
            pollutantRegisterActivities: {
              activities: ['_1_A_2_A_IRON_AND_STEEL', '_2_D_3_OTHER', '_2_H_OTHER'],
              exist: true,
            },
          },
          { pollutantRegisterActivities: [false] },
        ),
      );
    });

    it('should show error when submit same activity', () => {
      page.activityRadioButtons[1].click();
      fixture.detectChanges();

      page.subActivityA2RadioButtons[1].click();
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryList).toEqual(['You have already added this activity']);
    });
  });
});
