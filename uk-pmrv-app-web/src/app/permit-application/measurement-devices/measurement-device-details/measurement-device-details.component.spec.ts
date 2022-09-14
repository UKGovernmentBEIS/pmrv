import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteSnapshotStub, ActivatedRouteStub, BasePage, mockClass } from '../../../../testing';
import { PermitApplicationModule } from '../../permit-application.module';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockPermitApplyPayload } from '../../testing/mock-permit-apply-action';
import { mockPostBuild, mockState } from '../../testing/mock-state';
import { MeasurementDeviceDetailsComponent } from './measurement-device-details.component';

describe('MeasurementDeviceDetailsComponent', () => {
  let component: MeasurementDeviceDetailsComponent;
  let fixture: ComponentFixture<MeasurementDeviceDetailsComponent>;
  let page: Page;
  let router: Router;
  let route: ActivatedRoute;
  let store: PermitApplicationStore;

  const tasksService = mockClass(TasksService);
  const activatedRoute = new ActivatedRouteStub();

  class Page extends BasePage<MeasurementDeviceDetailsComponent> {
    get reference() {
      return this.getInputValue('#reference');
    }

    set reference(value: string) {
      this.setInputValue('#reference', value);
    }

    get type() {
      return this.getInputValue('#type');
    }

    set type(value: string) {
      this.setInputValue('#type', value);
    }

    get measurementRange() {
      return this.getInputValue('#measurementRange');
    }

    set measurementRange(value: string) {
      this.setInputValue('#measurementRange', value);
    }

    get meteringRangeUnits() {
      return this.getInputValue('#meteringRangeUnits');
    }

    set meteringRangeUnits(value: string) {
      this.setInputValue('#meteringRangeUnits', value);
    }

    get specifiedUncertaintyPercentage() {
      return this.getInputValue('#specifiedUncertaintyPercentage');
    }

    set specifiedUncertaintyPercentage(value: number) {
      this.setInputValue('#specifiedUncertaintyPercentage', value);
    }

    get location() {
      return this.getInputValue('#location');
    }

    set location(value: string) {
      this.setInputValue('#location', value);
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }
    get errorSummaryLinks() {
      return Array.from(this.query<HTMLDivElement>('.govuk-error-summary').querySelectorAll('a')).map((item) =>
        item.textContent.trim(),
      );
    }

    get uncertaintySpecifiedYesValue() {
      return this.query<HTMLInputElement>('#uncertaintySpecified-option0');
    }

    get title() {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PermitApplicationModule, RouterTestingModule],
      providers: [
        { provide: ActivatedRoute, useValue: activatedRoute },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();
  });

  const createComponent = () => {
    store = TestBed.inject(PermitApplicationStore);
    store.setState(mockState);
    route = TestBed.inject(ActivatedRoute);
    fixture = TestBed.createComponent(MeasurementDeviceDetailsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  describe('for adding new measurement devices', () => {
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display add title', () => {
      expect(page.title).toEqual('Add a measurement device or method');
    });

    it('should submit a valid form, update the store and navigate back to task', () => {
      expect(page.errorSummary).toBeFalsy();

      const expectedMeasurementDevices = [
        ...mockPermitApplyPayload.permit.measurementDevicesOrMethods,
        ...[
          {
            id: expect.any(String),
            reference: 'ref 1',
            type: 'ULTRASONIC_METER',
            measurementRange: '3',
            meteringRangeUnits: 'litres',
            specifiedUncertaintyPercentage: 2.0,
            location: 'London',
            uncertaintySpecified: true,
          },
        ],
      ];
      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryLinks).toEqual([
        'Provide a reference',
        'Select a type of measurement device',
        'Enter a measurement range',
        'Enter the metering range units',
        'Select Yes or No',
        'Enter a location',
      ]);

      page.reference = 'ref 1';
      page.type = 'ULTRASONIC_METER';
      page.measurementRange = '3';
      page.meteringRangeUnits = 'litres';
      page.location = 'London';
      page.uncertaintySpecifiedYesValue.click();
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryLinks).toEqual([
        'Enter a specified uncertainty percentage between 0-99.999 and to a maximum of 3 decimal places',
      ]);

      page.specifiedUncertaintyPercentage = 2.0;
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();

      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
        mockPostBuild(
          { measurementDevicesOrMethods: expectedMeasurementDevices },
          { measurementDevicesOrMethods: [false] },
        ),
      );

      expect(store.permit.measurementDevicesOrMethods).toEqual(expectedMeasurementDevices);
      expect(navigateSpy).toHaveBeenCalledWith(['..'], { relativeTo: activatedRoute });
    });
  });

  describe('for editing existing measurement device', () => {
    beforeEach(() => {
      route.snapshot = new ActivatedRouteSnapshotStub({
        deviceId: mockPermitApplyPayload.permit.measurementDevicesOrMethods[0].id,
      });
    });
    beforeEach(createComponent);

    it('should display edit title', () => {
      expect(page.title).toEqual('Edit measurement device or method');
    });

    it('should fill the form from the store', () => {
      expect(page.reference).toEqual(mockPermitApplyPayload.permit.measurementDevicesOrMethods[0].reference);
      expect(page.type).toContain(mockPermitApplyPayload.permit.measurementDevicesOrMethods[0].type);
      expect(page.measurementRange).toContain(
        mockPermitApplyPayload.permit.measurementDevicesOrMethods[0].measurementRange,
      );
      expect(page.meteringRangeUnits).toContain(
        mockPermitApplyPayload.permit.measurementDevicesOrMethods[0].meteringRangeUnits,
      );
      expect(page.specifiedUncertaintyPercentage).toContain(
        mockPermitApplyPayload.permit.measurementDevicesOrMethods[0].specifiedUncertaintyPercentage.toString(),
      );
      expect(page.location).toContain(mockPermitApplyPayload.permit.measurementDevicesOrMethods[0].location);
    });

    it('should submit a valid form, update the store and navigate to summary', () => {
      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');
      const expectedMeasurementDevices = [
        ...[
          {
            id: '16236817394240.1574963093314700',
            reference: 'ref 1',
            type: 'ULTRASONIC_METER',
            measurementRange: '3',
            meteringRangeUnits: 'litres',
            specifiedUncertaintyPercentage: 2.0,
            location: 'north terminal',
            uncertaintySpecified: true,
          },
        ],
        ...mockPermitApplyPayload.permit.measurementDevicesOrMethods.filter(
          (stream) => stream.id !== mockPermitApplyPayload.permit.measurementDevicesOrMethods[0].id,
        ),
      ];

      page.reference = 'ref 1';
      page.submitButton.click();

      expect(navigateSpy).toHaveBeenCalledWith(['..'], { relativeTo: route });
      expect(store.payload.permitSectionsCompleted.measurementDevicesOrMethods).toEqual([false]);
      expect(store.permit.measurementDevicesOrMethods).toEqual(expectedMeasurementDevices);
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
        mockPostBuild(
          { measurementDevicesOrMethods: expectedMeasurementDevices },
          { measurementDevicesOrMethods: [false] },
        ),
      );
    });
  });
});
