import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { AerModule } from '@tasks/aer/aer.module';
import { RegulatedActivitiesComponent } from '@tasks/aer/submit/regulated-activities/regulated-activities.component';
import { mockAerApplyPayload, mockState } from '@tasks/aer/submit/testing/mock-permit-apply-action';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { AerApplicationSubmitRequestTaskPayload, TasksService } from 'pmrv-api';

import { mockPostBuild } from '../testing/mock-state';

describe('RegulatedActivitiesComponent', () => {
  let component: RegulatedActivitiesComponent;
  let fixture: ComponentFixture<RegulatedActivitiesComponent>;
  let page: Page;
  let router: Router;
  let route: ActivatedRoute;
  let store: CommonTasksStore;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<RegulatedActivitiesComponent> {
    get submitButton() {
      return Array.from(this.queryAll<HTMLButtonElement>('button[type="button"]')).find(
        (button) => button.textContent.trim() === 'Confirm and complete',
      );
    }

    get addRegulatedActivityBtn() {
      return Array.from(this.queryAll<HTMLButtonElement>('button[type="button"]')).find(
        (button) => button.textContent.trim() === 'Add a regulated activity',
      );
    }

    get addAnotherRegulatedActivityBtn() {
      return Array.from(this.queryAll<HTMLButtonElement>('button[type="button"]')).find(
        (button) => button.textContent.trim() === 'Add another regulated activity',
      );
    }

    get regulatedActivities() {
      return this.queryAll<HTMLDListElement>('dl');
    }

    get regulatedActivitiesTextContents() {
      return this.regulatedActivities.map((activity) =>
        Array.from(activity.querySelectorAll('dd')).map((dd) => dd.textContent.trim()),
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
    fixture = TestBed.createComponent(RegulatedActivitiesComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    route = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  describe('for adding new regulated activity', () => {
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should show add new regulated activity button and hide complete button', () => {
      expect(page.submitButton).toBeFalsy();
      expect(page.addRegulatedActivityBtn).toBeFalsy();
      expect(page.addAnotherRegulatedActivityBtn).toBeFalsy();
      expect(page.regulatedActivities.length).toEqual(0);

      store = TestBed.inject(CommonTasksStore);
      store.setState({
        ...store.getState(),
        isEditable: true,
      });
      fixture.detectChanges();

      expect(page.addRegulatedActivityBtn).toBeTruthy();
    });
  });

  describe('for existing regulated activities', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(mockState);
    });
    beforeEach(createComponent);

    it('should show add another regulated activity button and complete button', () => {
      expect(page.submitButton).toBeTruthy();
      expect(page.addRegulatedActivityBtn).toBeFalsy();
      expect(page.addAnotherRegulatedActivityBtn).toBeTruthy();
      expect(page.regulatedActivities.length).toEqual(1);
    });

    it('should display the regulated activities', () => {
      expect(page.regulatedActivitiesTextContents).toEqual([
        [
          'Ammonia production (Carbon dioxide)',
          'Change',
          '100 kVA',
          'Change',
          '1.A.1.a Public Electricity and Heat Production2.A.4 Other Process uses of Carbonates',
          'Change',
        ],
      ]);
    });

    it('should submit the regulated activity task and navigate to capacity', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));

      page.submitButton.click();

      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['..'], { relativeTo: route });
      expect(
        (store.getState().requestTaskItem.requestTask.payload as AerApplicationSubmitRequestTaskPayload)
          .aerSectionsCompleted.regulatedActivities,
      ).toEqual([true]);
      expect(
        (store.getState().requestTaskItem.requestTask.payload as AerApplicationSubmitRequestTaskPayload).aer
          .regulatedActivities,
      ).toEqual(mockAerApplyPayload.aer.regulatedActivities);
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
        mockPostBuild({}, { regulatedActivities: [true] }),
      );
    });
  });
});
