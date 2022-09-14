import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { BasePage, mockClass } from '../../../testing';
import { PermitApplicationModule } from '../permit-application.module';
import { PermitApplicationStore } from '../store/permit-application.store';
import { mockPermitApplyPayload } from '../testing/mock-permit-apply-action';
import { mockPostBuild, mockState, mockStateBuild } from '../testing/mock-state';
import { MeasurementDevicesComponent } from './measurement-devices.component';

describe('MeasurementDevicesComponent', () => {
  let component: MeasurementDevicesComponent;
  let fixture: ComponentFixture<MeasurementDevicesComponent>;
  let page: Page;
  let router: Router;
  let route: ActivatedRoute;
  let store: PermitApplicationStore;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<MeasurementDevicesComponent> {
    get submitButton() {
      return Array.from(this.queryAll<HTMLButtonElement>('button[type="button"]')).find(
        (button) => button.textContent.trim() === 'Confirm and complete',
      );
    }

    get addMeasurementDeviceBtn() {
      return Array.from(this.queryAll<HTMLButtonElement>('button[type="button"]')).find(
        (button) => button.textContent.trim() === 'Add a measurement device',
      );
    }
    get addAnotherMeasurementDeviceBtn() {
      return Array.from(this.queryAll<HTMLButtonElement>('button[type="button"]')).find(
        (button) => button.textContent.trim() === 'Add another measurement device',
      );
    }
    get measurementDevices() {
      return this.queryAll<HTMLDListElement>('dl');
    }
    get measurementDevicesTextContents() {
      return this.measurementDevices.map((measurementDevice) =>
        Array.from(measurementDevice.querySelectorAll('dd')).map((dd) => dd.textContent.trim()),
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
    fixture = TestBed.createComponent(MeasurementDevicesComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    route = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  describe('for adding new measurement device', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(mockStateBuild({ measurementDevicesOrMethods: [] }));
    });

    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should show add new measurement device button and hide complete button', () => {
      expect(page.submitButton).toBeFalsy();
      expect(page.addMeasurementDeviceBtn).toBeTruthy();
      expect(page.addAnotherMeasurementDeviceBtn).toBeFalsy();
      expect(page.measurementDevices.length).toEqual(0);
    });
  });

  describe('for existing measurement devices', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(mockState);
    });

    beforeEach(createComponent);

    it('should show add another measurement device and complete button', () => {
      expect(page.submitButton).toBeTruthy();
      expect(page.addMeasurementDeviceBtn).toBeFalsy();
      expect(page.addAnotherMeasurementDeviceBtn).toBeTruthy();
      expect(page.measurementDevices.length).toEqual(2);
    });

    it('should display the measurement devices', () => {
      expect(page.measurementDevicesTextContents).toEqual([
        ['ref1', 'Change | Delete', 'Ultrasonic meter', '3', 'litres', '± 2 %', 'north terminal'],
        ['ref2', 'Change | Delete', 'Ultrasonic meter', '3', 'litres', 'None', 'north terminal'],
      ]);
    });

    it('should submit the measurement device and navigate to summary', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({ data: { id: 'test' } }));

      page.submitButton.click();

      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['summary'], { relativeTo: route, state: { notification: true } });
      expect(store.payload.permitSectionsCompleted.measurementDevicesOrMethods).toEqual([true]);
      expect(store.permit.measurementDevicesOrMethods).toEqual(
        mockPermitApplyPayload.permit.measurementDevicesOrMethods,
      );
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
        mockPostBuild({}, { measurementDevicesOrMethods: [true] }),
      );
    });
  });
});
