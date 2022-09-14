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
import { RegulatedActivitiesComponent } from './regulated-activities.component';

describe('RegulatedActivitiesComponent', () => {
  let page: Page;
  let router: Router;
  let route: ActivatedRoute;
  let store: PermitApplicationStore;
  let component: RegulatedActivitiesComponent;
  let fixture: ComponentFixture<RegulatedActivitiesComponent>;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<RegulatedActivitiesComponent> {
    get combustion() {
      return this.query<HTMLInputElement>('#COMBUSTION_GROUP-0');
    }

    get refining() {
      return this.query<HTMLInputElement>('#REFINING_GROUP-0');
    }

    get combustionCapacity() {
      return this.getInputValue('#COMBUSTION_CAPACITY');
    }

    set combustionCapacity(value: string) {
      this.setInputValue('#COMBUSTION_CAPACITY', value);
    }

    get combustionCapacityUnit() {
      return this.getInputValue('#COMBUSTION_CAPACITY_UNIT');
    }

    set combustionCapacityUnit(value: string) {
      this.setInputValue('#COMBUSTION_CAPACITY_UNIT', value);
    }

    set refiningCapacity(value: string) {
      this.setInputValue('#MINERAL_OIL_REFINING_CAPACITY', value);
    }

    set refiningCapacityUnit(value: string) {
      this.setInputValue('#MINERAL_OIL_REFINING_CAPACITY_UNIT', value);
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }

    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }
    get confirmDeleteButton() {
      return this.query<HTMLButtonElement>('button.govuk-button--warning');
    }
    get confirmDeleteCancelLink() {
      return this.queryAll<HTMLLinkElement>('a').slice(-1)[0];
    }
    get confirmDeleteHeaderInfo() {
      return this.query<HTMLElement>('.govuk-heading-xl');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(RegulatedActivitiesComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    route = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PermitApplicationModule, RouterTestingModule],
      providers: [{ provide: TasksService, useValue: tasksService }],
    }).compileComponents();
  });

  describe('for existing regulated activities', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(mockState);
    });
    beforeEach(createComponent);

    it('should fill the form from the store', () => {
      expect(page.combustion.checked).toBeTruthy();
      expect(page.combustionCapacity).toEqual('34344');
      expect(page.combustionCapacityUnit).toEqual('MW_TH');
    });

    it('should update a valid form, update the store and navigate to summary', () => {
      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');

      page.combustionCapacity = '111';
      fixture.detectChanges();
      page.submitButton.click();
      fixture.detectChanges();

      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['summary'], { relativeTo: route, state: { notification: true } });
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
        mockPostBuild(
          {
            regulatedActivities: [
              {
                id: '16236817394240.1574963093314664',
                type: 'COMBUSTION',
                capacity: 111,
                capacityUnit: 'MW_TH',
              },
              {
                id: '16236817394240.1574963093314665',
                type: 'MINERAL_OIL_REFINING',
                capacity: 34345,
                capacityUnit: 'MW',
              },
            ],
          },
          { regulatedActivities: [true] },
        ),
      );
    });

    it('should show confirm delete page if an existing activity is unchecked and, upon confirming the delete, update the store and navigate to summary', () => {
      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');

      expect(page.confirmDeleteButton).toBeNull();
      expect(page.confirmDeleteHeaderInfo).toBeFalsy();

      page.combustion.click();
      fixture.detectChanges();
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.confirmDeleteButton).toBeTruthy();
      expect(page.confirmDeleteHeaderInfo.textContent).toContain('Combustion');
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(0);

      page.confirmDeleteButton.click();
      fixture.detectChanges();
      expect(navigateSpy).toHaveBeenCalledWith(['summary'], { relativeTo: route, state: { notification: true } });
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
        mockPostBuild(
          {
            regulatedActivities: [
              {
                id: '16236817394240.1574963093314665',
                type: 'MINERAL_OIL_REFINING',
                capacity: 34345,
                capacityUnit: 'MW',
              },
            ],
          },
          { regulatedActivities: [true] },
        ),
      );
    });

    it('should show confirm delete page if an existing activity is unchecked, and upon pressing cancel link should show the edit form', () => {
      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));

      expect(page.confirmDeleteButton).toBeNull();
      expect(page.confirmDeleteHeaderInfo).toBeFalsy();

      page.combustion.click();
      fixture.detectChanges();
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.confirmDeleteButton).toBeTruthy();
      expect(page.confirmDeleteHeaderInfo.textContent).toContain('Combustion');
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(0);

      page.confirmDeleteCancelLink.click();
      fixture.detectChanges();

      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(0);
      expect(page.combustion.checked).toBeFalsy();
    });
  });

  describe('for new regulated activities', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      mockPermitApplyPayload.permit.regulatedActivities = [];
      store.setState(mockState);
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should submit a valid form, update the store and navigate to summary', () => {
      expect(page.errorSummary).toBeFalsy();

      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummary.querySelectorAll('a')[0].textContent.trim()).toEqual(
        'Select all the regulated activities carried out the installation',
      );

      page.combustion.click();
      page.combustionCapacity = '123';
      page.combustionCapacityUnit = 'MW_TH';

      page.refining.click();
      page.refiningCapacity = '1234';
      page.refiningCapacityUnit = 'MW';

      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();

      page.submitButton.click();
      fixture.detectChanges();
      expect(page.errorSummary).toBeFalsy();

      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['summary'], { relativeTo: route, state: { notification: true } });
    });

    it('should require capacity and unit', () => {
      expect(page.errorSummary).toBeFalsy();

      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
      page.combustion.click();
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummary.querySelectorAll('a')[0].textContent.trim()).toEqual(
        'Enter the capacity for combustion',
      );
      expect(page.errorSummary.querySelectorAll('a')[1].textContent.trim()).toEqual(
        'Enter the capacity unit for combustion',
      );

      page.combustionCapacity = '34344';
      page.combustionCapacityUnit = 'MW_TH';
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
        mockPostBuild(
          {
            regulatedActivities: [
              {
                id: expect.any(String),
                type: 'COMBUSTION',
                capacity: 34344,
                capacityUnit: 'MW_TH',
              },
            ],
          },
          { regulatedActivities: [true] },
        ),
      );
    });

    it('should accepts only numbers for capacities', () => {
      page.combustion.click();
      page.combustionCapacity = '123abc';
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummary.querySelectorAll('a')[0].textContent.trim()).toEqual('Enter a numerical value');
    });
  });
});
