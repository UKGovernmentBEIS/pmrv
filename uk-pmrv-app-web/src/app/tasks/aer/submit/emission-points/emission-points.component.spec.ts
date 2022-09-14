import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { AerModule } from '@tasks/aer/aer.module';
import { EmissionPointsComponent } from '@tasks/aer/submit/emission-points/emission-points.component';
import { mockAerApplyPayload, mockState } from '@tasks/aer/submit/testing/mock-permit-apply-action';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { AerApplicationSubmitRequestTaskPayload, TasksService } from 'pmrv-api';

import { mockPostBuild } from '../testing/mock-state';

describe('EmissionPointsComponent', () => {
  let component: EmissionPointsComponent;
  let fixture: ComponentFixture<EmissionPointsComponent>;
  let page: Page;
  let router: Router;
  let route: ActivatedRoute;
  let store: CommonTasksStore;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<EmissionPointsComponent> {
    get submitButton() {
      return Array.from(this.queryAll<HTMLButtonElement>('button[type="button"]')).find(
        (button) => button.textContent.trim() === 'Confirm and complete',
      );
    }

    get addEmissionPointBtn() {
      return Array.from(this.queryAll<HTMLButtonElement>('button[type="button"]')).find(
        (button) => button.textContent.trim() === 'Add an emission point',
      );
    }

    get addAnotherEmissionPointBtn() {
      return Array.from(this.queryAll<HTMLButtonElement>('button[type="button"]')).find(
        (button) => button.textContent.trim() === 'Add another emission point',
      );
    }

    get emissionPoints() {
      return this.queryAll<HTMLDListElement>('tr');
    }

    get emissionPointsTextContents() {
      return this.emissionPoints.map((emissionPoint) =>
        Array.from(emissionPoint.querySelectorAll('td')).map((td) => td.textContent.trim()),
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
    fixture = TestBed.createComponent(EmissionPointsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    route = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  describe('for adding new emission points', () => {
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should show add new emission point button and hide complete button', () => {
      expect(page.submitButton).toBeFalsy();
      expect(page.addEmissionPointBtn).toBeFalsy();
      expect(page.addAnotherEmissionPointBtn).toBeFalsy();
      expect(page.emissionPoints.length).toEqual(0);

      store = TestBed.inject(CommonTasksStore);
      store.setState({
        ...store.getState(),
        isEditable: true,
      });
      fixture.detectChanges();

      expect(page.addEmissionPointBtn).toBeTruthy();
    });
  });

  describe('for existing emission points', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(mockState);
    });
    beforeEach(createComponent);

    it('should show add another emission point button and complete button', () => {
      expect(page.submitButton).toBeTruthy();
      expect(page.addEmissionPointBtn).toBeFalsy();
      expect(page.addAnotherEmissionPointBtn).toBeTruthy();
      expect(page.emissionPoints.length).toEqual(3);
    });

    it('should display the emission points', () => {
      expect(page.emissionPointsTextContents).toEqual([
        [],
        ['EP1', 'west side chimney', 'Change', 'Delete'],
        ['EP2', 'east side chimney', 'Change', 'Delete'],
      ]);
    });

    it('should submit the emission point task and navigate back to task list', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));

      page.submitButton.click();

      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['..'], { relativeTo: route });
      expect(
        (store.getState().requestTaskItem.requestTask.payload as AerApplicationSubmitRequestTaskPayload)
          .aerSectionsCompleted.emissionPoints,
      ).toEqual([true]);
      expect(
        (store.getState().requestTaskItem.requestTask.payload as AerApplicationSubmitRequestTaskPayload).aer
          .emissionPoints,
      ).toEqual(mockAerApplyPayload.aer.emissionPoints);
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
        mockPostBuild({}, { emissionPoints: [true] }),
      );
    });
  });
});