import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { AerModule } from '@tasks/aer/aer.module';
import { mockAerApplyPayload, mockState } from '@tasks/aer/submit/testing/mock-permit-apply-action';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { AerApplicationSubmitRequestTaskPayload, TasksService } from 'pmrv-api';

import { mockPostBuild } from '../testing/mock-state';
import { EmissionSourcesComponent } from './emission-sources.component';

describe('EmissionSourcesComponent', () => {
  let component: EmissionSourcesComponent;
  let fixture: ComponentFixture<EmissionSourcesComponent>;
  let page: Page;
  let router: Router;
  let route: ActivatedRoute;
  let store: CommonTasksStore;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<EmissionSourcesComponent> {
    get submitButton() {
      return Array.from(this.queryAll<HTMLButtonElement>('button[type="button"]')).find(
        (button) => button.textContent.trim() === 'Confirm and complete',
      );
    }

    get addEmissionSourceBtn() {
      return Array.from(this.queryAll<HTMLButtonElement>('button[type="button"]')).find(
        (button) => button.textContent.trim() === 'Add an emission source',
      );
    }

    get addAnotherEmissionSourceBtn() {
      return Array.from(this.queryAll<HTMLButtonElement>('button[type="button"]')).find(
        (button) => button.textContent.trim() === 'Add another emission source',
      );
    }

    get emissionSources() {
      return this.queryAll<HTMLDListElement>('tr');
    }

    get emissionSourcesTextContents() {
      return this.emissionSources.map((emissionSource) =>
        Array.from(emissionSource.querySelectorAll('td')).map((td) => td.textContent.trim()),
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
    fixture = TestBed.createComponent(EmissionSourcesComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    route = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  describe('for adding new emission source', () => {
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should show add new emission source button and hide complete button', () => {
      expect(page.submitButton).toBeFalsy();
      expect(page.addEmissionSourceBtn).toBeFalsy();
      expect(page.addAnotherEmissionSourceBtn).toBeFalsy();
      expect(page.emissionSources.length).toEqual(0);

      store = TestBed.inject(CommonTasksStore);
      store.setState({
        ...store.getState(),
        isEditable: true,
      });
      fixture.detectChanges();

      expect(page.addEmissionSourceBtn).toBeTruthy();
    });
  });

  describe('for existing emission sources', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(mockState);
    });
    beforeEach(createComponent);

    it('should show add another emission source button and complete button', () => {
      expect(page.submitButton).toBeTruthy();
      expect(page.addEmissionSourceBtn).toBeFalsy();
      expect(page.addAnotherEmissionSourceBtn).toBeTruthy();
      expect(page.emissionSources.length).toEqual(3);
    });

    it('should display the emission sources', () => {
      expect(page.emissionSourcesTextContents).toEqual([
        [],
        ['emission source 1 reference', 'emission source 1 description', 'Change', 'Delete'],
        ['emission source 2 reference', 'emission source 2 description', 'Change', 'Delete'],
      ]);
    });

    it('should submit the emission source task and navigate to summary', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));

      page.submitButton.click();

      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['..'], { relativeTo: route });
      expect(
        (store.getState().requestTaskItem.requestTask.payload as AerApplicationSubmitRequestTaskPayload)
          .aerSectionsCompleted.emissionSources,
      ).toEqual([true]);
      expect(
        (store.getState().requestTaskItem.requestTask.payload as AerApplicationSubmitRequestTaskPayload).aer
          .emissionSources,
      ).toEqual(mockAerApplyPayload.aer.emissionSources);
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
        mockPostBuild({}, { emissionSources: [true] }),
      );
    });
  });
});
