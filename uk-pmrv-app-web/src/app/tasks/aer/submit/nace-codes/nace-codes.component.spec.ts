import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { AerModule } from '@tasks/aer/aer.module';
import { NaceCodesComponent } from '@tasks/aer/submit/nace-codes/nace-codes.component';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

import { mockPostBuild, mockStateBuild } from '../testing/mock-state';

describe('NaceCodesComponent', () => {
  let page: Page;
  let router: Router;
  let route: ActivatedRoute;
  let store: CommonTasksStore;
  let component: NaceCodesComponent;
  let fixture: ComponentFixture<NaceCodesComponent>;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<NaceCodesComponent> {
    get confirmButton() {
      return Array.from(this.queryAll<HTMLButtonElement>('button[type="button"]')).find(
        (button) => button.textContent.trim() === 'Confirm and complete',
      );
    }

    get addNaceCodeButton() {
      return Array.from(this.queryAll<HTMLButtonElement>('button[type="button"]')).find(
        (button) => button.textContent.trim() === 'Add a NACE code',
      );
    }

    get addAnotherNaceCodeButton() {
      return Array.from(this.queryAll<HTMLButtonElement>('button[type="button"]')).find(
        (button) => button.textContent.trim() === 'Add another',
      );
    }

    get naceCodes() {
      return this.queryAll<HTMLDListElement>('tr').map((naceCode) =>
        Array.from(naceCode.querySelectorAll('td')).map((dd) => dd.textContent.trim()),
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
    fixture = TestBed.createComponent(NaceCodesComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    route = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  describe('defining nace codes for first time', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(mockStateBuild({ naceCodes: { codes: [] } }));
    });
    beforeEach(createComponent);

    it('should create component', () => {
      expect(component).toBeTruthy();
    });

    it('should display add nace codes button and hide all others', () => {
      expect(page.addNaceCodeButton).toBeTruthy();
      expect(page.confirmButton).toBeFalsy();
      expect(page.addAnotherNaceCodeButton).toBeFalsy();
    });
  });

  describe('adding more nace codes', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(mockStateBuild({ naceCodes: { codes: ['_2229_MANUFACTURE_OF_OTHER_PLASTIC_PRODUCTS'] } }));
    });
    beforeEach(createComponent);

    it('should create component', () => {
      expect(component).toBeTruthy();
    });

    it('should display add another and confirm buttons, but hide add button', () => {
      expect(page.confirmButton).toBeTruthy();
      expect(page.addAnotherNaceCodeButton).toBeTruthy();
      expect(page.addNaceCodeButton).toBeFalsy();
    });

    it('should display selected nace code', () => {
      expect(page.naceCodes[1]).toEqual(['Main activity', '2229 Manufacture of other plastic products', 'Delete']);
    });

    it('should submit selected nace codes and navigate to summary page', () => {
      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');

      page.confirmButton.click();

      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
        mockPostBuild({ naceCodes: { codes: ['_2229_MANUFACTURE_OF_OTHER_PLASTIC_PRODUCTS'] } }, { naceCodes: [true] }),
      );
      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['..'], { relativeTo: route });
    });
  });
});
