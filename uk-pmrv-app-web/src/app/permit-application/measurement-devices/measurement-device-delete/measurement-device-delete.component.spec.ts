import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../../testing';
import { PermitApplicationModule } from '../../permit-application.module';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockPermitApplyPayload } from '../../testing/mock-permit-apply-action';
import { mockPostBuild, mockState } from '../../testing/mock-state';
import { MeasurementDeviceDeleteComponent } from './measurement-device-delete.component';

describe('MeasurementDeviceDeleteComponent', () => {
  let component: MeasurementDeviceDeleteComponent;
  let fixture: ComponentFixture<MeasurementDeviceDeleteComponent>;
  let store: PermitApplicationStore;
  let router: Router;
  let page: Page;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub({
    deviceId: mockPermitApplyPayload.permit.measurementDevicesOrMethods[0].id,
  });

  class Page extends BasePage<MeasurementDeviceDeleteComponent> {
    get header() {
      return this.query<HTMLHeadingElement>('h1');
    }
    get submitButton() {
      return this.queryAll<HTMLButtonElement>('button')[0];
    }
  }
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PermitApplicationModule, RouterTestingModule],
      providers: [
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(PermitApplicationStore);
    store.setState(mockState);
    fixture = TestBed.createComponent(MeasurementDeviceDeleteComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the measurement device name', () => {
    expect(page.header.textContent.trim()).toContain(
      mockPermitApplyPayload.permit.measurementDevicesOrMethods[0].reference,
    );
  });

  it('should delete the measurement device', () => {
    expect(store.permit.measurementDevicesOrMethods).toEqual(mockPermitApplyPayload.permit.measurementDevicesOrMethods);

    tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
    const navigateSpy = jest.spyOn(router, 'navigate');
    const expectedMeasurementDevices = mockPermitApplyPayload.permit.measurementDevicesOrMethods.filter(
      (device) => device.id !== mockPermitApplyPayload.permit.measurementDevicesOrMethods[0].id,
    );

    page.submitButton.click();

    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
      mockPostBuild(
        { measurementDevicesOrMethods: expectedMeasurementDevices },
        { measurementDevicesOrMethods: [false] },
      ),
    );
    expect(navigateSpy).toHaveBeenCalledTimes(1);
    expect(store.permit.measurementDevicesOrMethods).toEqual(expectedMeasurementDevices);
  });
});
