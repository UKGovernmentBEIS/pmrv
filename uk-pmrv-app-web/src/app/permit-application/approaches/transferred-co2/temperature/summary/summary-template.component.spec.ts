import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { TransferredCO2MonitoringApproach } from 'pmrv-api';

import { BasePage } from '../../../../../../testing';
import { SharedModule } from '../../../../../shared/shared.module';
import { MeasurementDevicesTypePipe } from '../../../../measurement-devices/measurement-devices-summary/measurement-devices-type.pipe';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { mockPermitApplyPayload } from '../../../../testing/mock-permit-apply-action';
import { mockState } from '../../../../testing/mock-state';
import { SummaryTemplateComponent } from './summary-template.component';

describe('SummaryTemplateComponent', () => {
  let component: SummaryTemplateComponent;
  let fixture: ComponentFixture<TestComponent>;
  let page: Page;
  let store: PermitApplicationStore;

  @Component({
    template: `<app-temperature-summary-template [details]="temperaturePressure"></app-temperature-summary-template>`,
  })
  class TestComponent {
    temperaturePressure = (
      mockPermitApplyPayload.permit.monitoringApproaches.TRANSFERRED_CO2 as TransferredCO2MonitoringApproach
    ).temperaturePressure;
  }

  class Page extends BasePage<TestComponent> {
    get summaryDefinitions() {
      return this.queryAll('dd').map((row) => row.textContent.trim());
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule],
      declarations: [TestComponent, SummaryTemplateComponent, MeasurementDevicesTypePipe],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(PermitApplicationStore);
    store.setState(mockState);
    fixture = TestBed.createComponent(TestComponent);
    component = fixture.debugElement.query(By.directive(SummaryTemplateComponent)).componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the details', () => {
    const temperaturePressure = (
      mockPermitApplyPayload.permit.monitoringApproaches.TRANSFERRED_CO2 as TransferredCO2MonitoringApproach
    ).temperaturePressure;
    expect(page.summaryDefinitions).toEqual([
      temperaturePressure.measurementDevices[0].reference,
      temperaturePressure.measurementDevices[0].otherTypeName,
      temperaturePressure.measurementDevices[0].location,
      temperaturePressure.measurementDevices[1].reference,
      'Balance',
      temperaturePressure.measurementDevices[1].location,
    ]);
  });
});
