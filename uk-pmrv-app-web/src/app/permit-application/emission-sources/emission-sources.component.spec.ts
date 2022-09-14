import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { BasePage, mockClass } from '../../../testing';
import { PermitApplicationModule } from '../permit-application.module';
import { PermitApplicationStore } from '../store/permit-application.store';
import { mockPermitApplyPayload } from '../testing/mock-permit-apply-action';
import { mockPostBuild, mockState } from '../testing/mock-state';
import { EmissionSourcesComponent } from './emission-sources.component';

describe('EmissionSourcesComponent', () => {
  let component: EmissionSourcesComponent;
  let fixture: ComponentFixture<EmissionSourcesComponent>;
  let page: Page;
  let router: Router;
  let route: ActivatedRoute;
  let store: PermitApplicationStore;

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
      return this.queryAll<HTMLDListElement>('dl');
    }

    get emissionSourcesTextContents() {
      return this.emissionSources.map((emissionSource) =>
        Array.from(emissionSource.querySelectorAll('dd')).map((dd) => dd.textContent.trim()),
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

      store = TestBed.inject(PermitApplicationStore);
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
      store = TestBed.inject(PermitApplicationStore);
      store.setState(mockState);
    });
    beforeEach(createComponent);

    it('should show add another emission source button and complete button', () => {
      expect(page.submitButton).toBeTruthy();
      expect(page.addEmissionSourceBtn).toBeFalsy();
      expect(page.addAnotherEmissionSourceBtn).toBeTruthy();
      expect(page.emissionSources.length).toEqual(2);
    });

    it('should display the emission sources', () => {
      expect(page.emissionSourcesTextContents).toEqual([
        ['S1 Boiler', 'Change | Delete'],
        ['S2 Boiler 2', 'Change | Delete'],
      ]);
    });

    it('should submit the emission source task and navigate to summary', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));

      page.submitButton.click();

      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['summary'], { relativeTo: route, state: { notification: true } });
      expect(store.payload.permitSectionsCompleted.emissionSources).toEqual([true]);
      expect(store.permit.emissionSources).toEqual(mockPermitApplyPayload.permit.emissionSources);
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
        mockPostBuild({}, { emissionSources: [true] }),
      );
    });
  });
});
