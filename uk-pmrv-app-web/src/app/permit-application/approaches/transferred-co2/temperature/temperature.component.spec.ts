import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { TasksService, TransferredCO2MonitoringApproach } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../../../testing';
import { PermitApplicationStore } from '../../../store/permit-application.store';
import { mockPermitApplyPayload } from '../../../testing/mock-permit-apply-action';
import { mockPostBuild, mockState, mockStateBuild } from '../../../testing/mock-state';
import { TransferredCO2Module } from '../transferred-co2.module';
import { TemperatureComponent } from './temperature.component';

describe('TemperatureComponent', () => {
  let page: Page;
  let router: Router;
  let store: PermitApplicationStore;
  let component: TemperatureComponent;
  let fixture: ComponentFixture<TemperatureComponent>;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub(
    {},
    {},
    { taskKey: 'monitoringApproaches.TRANSFERRED_CO2.temperaturePressure', statusKey: 'transferredCo2Temperature' },
  );

  class Page extends BasePage<TemperatureComponent> {
    get existRadios() {
      return this.queryAll<HTMLInputElement>('input[name$="exist"]');
    }

    get measurementDevices() {
      return this.queryAll<HTMLInputElement>('input[name$="reference"]');
    }

    get references() {
      return this.queryAll<HTMLInputElement>('input[name$="reference"]');
    }

    set referenceValues(values: string[]) {
      this.references.forEach((input, index) => this.setInputValue(`#${input.id}`, values[index]));
    }

    get types() {
      return this.queryAll<HTMLSelectElement>('select');
    }

    set typeValues(values: string[]) {
      this.types.forEach((input, index) => this.setInputValue(`#${input.id}`, values[index]));
    }

    get otherTypeNames() {
      return this.queryAll<HTMLInputElement>('input[name$="otherTypeName"]');
    }

    set otherTypeNameValues(values: string[]) {
      this.otherTypeNames.forEach((input, index) => this.setInputValue(`#${input.id}`, values[index]));
    }

    get locations() {
      return this.queryAll<HTMLInputElement>('input[name$="location"]');
    }

    set locationValues(values: string[]) {
      this.locations.forEach((input, index) => this.setInputValue(`#${input.id}`, values[index]));
    }

    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get errors() {
      return Array.from(this.errorSummary.querySelectorAll('a'));
    }

    get addAnotherButton() {
      const secondaryButtons = this.queryAll<HTMLButtonElement>('button[type="button"]');
      return secondaryButtons[secondaryButtons.length - 1];
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(TemperatureComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, TransferredCO2Module],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();
  });

  describe('for new temperature pressure', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(
        mockStateBuild({
          ...mockPermitApplyPayload.permit,
          monitoringApproaches: {
            ...mockPermitApplyPayload.permit.monitoringApproaches,
            TRANSFERRED_CO2: {
              ...mockPermitApplyPayload.permit.monitoringApproaches.TRANSFERRED_CO2,
              temperaturePressure: undefined,
            } as TransferredCO2MonitoringApproach,
          },
        }),
      );
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should submit a valid form, update the store and navigate to summary', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');

      page.existRadios[0].click();
      fixture.detectChanges();

      page.referenceValues = ['reference'];
      page.typeValues = ['BALANCE'];
      page.locationValues = ['location'];

      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
        mockPostBuild(
          {
            ...store.getState().permit,
            monitoringApproaches: {
              ...store.getState().permit.monitoringApproaches,
              TRANSFERRED_CO2: {
                ...store.getState().permit.monitoringApproaches.TRANSFERRED_CO2,
                temperaturePressure: {
                  exist: true,
                  measurementDevices: [
                    {
                      reference: 'reference',
                      type: 'BALANCE',
                      location: 'location',
                    },
                  ],
                },
              } as TransferredCO2MonitoringApproach,
            },
          },
          {
            ...mockPermitApplyPayload.permitSectionsCompleted,
            transferredCo2Temperature: [true],
          },
        ),
      );

      expect(navigateSpy).toHaveBeenCalledWith(['summary'], {
        relativeTo: route,
        state: { notification: true },
      });
    });
  });

  describe('for existing temperature pressure', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(mockState);

      fixture = TestBed.createComponent(TemperatureComponent);
      component = fixture.componentInstance;
      page = new Page(fixture);
      fixture.detectChanges();

      router = TestBed.inject(Router);
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display an non empty form', () => {
      expect(page.existRadios.some((radio) => radio.checked)).toBeTruthy();
      expect(page.measurementDevices).toHaveLength(2);
    });

    it('should clear measurement devices if no is submitted', () => {
      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');

      page.existRadios[1].click();
      fixture.detectChanges();

      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
        mockPostBuild(
          {
            ...store.getState().permit,
            monitoringApproaches: {
              ...store.getState().permit.monitoringApproaches,
              TRANSFERRED_CO2: {
                ...store.getState().permit.monitoringApproaches.TRANSFERRED_CO2,
                temperaturePressure: {
                  exist: false,
                },
              } as TransferredCO2MonitoringApproach,
            },
          },
          {
            ...mockPermitApplyPayload.permitSectionsCompleted,
            transferredCo2Temperature: [true],
          },
        ),
      );

      expect(navigateSpy).toHaveBeenCalledWith(['summary'], {
        relativeTo: route,
        state: { notification: true },
      });
    });

    it('should require all fields to be populated', () => {
      page.addAnotherButton.click();
      fixture.detectChanges();

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errors.map((error) => error.textContent.trim())).toEqual([
        'Provide a reference',
        'Select a type of measurement device',
        'Enter a location',
      ]);

      page.typeValues = ['OTHER', 'OTHER', 'OTHER'];
      fixture.detectChanges();

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errors.map((error) => error.textContent.trim())).toEqual([
        'Enter a short name',
        'Provide a reference',
        'Enter a short name',
        'Enter a location',
      ]);
    });

    it('should add another measurement device', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');

      page.addAnotherButton.click();
      fixture.detectChanges();

      page.referenceValues = ['reference1', 'reference2', 'reference3'];
      page.typeValues = ['OTHER', 'BALANCE', 'BALANCE'];
      page.otherTypeNameValues = ['otherTypeName1'];
      page.locationValues = ['location1', 'location2', 'location3'];

      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
      page.submitButton.click();
      fixture.detectChanges();

      (
        mockPermitApplyPayload.permit.monitoringApproaches['TRANSFERRED_CO2'] as TransferredCO2MonitoringApproach
      ).temperaturePressure.measurementDevices.push({
        reference: 'reference3',
        type: 'BALANCE',
        location: 'location3',
      });

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
        mockPostBuild(
          {},
          {
            ...mockPermitApplyPayload.permitSectionsCompleted,
            transferredCo2Temperature: [true],
          },
        ),
      );

      expect(navigateSpy).toHaveBeenCalledWith(['summary'], {
        relativeTo: route,
        state: { notification: true },
      });
    });
  });
});
