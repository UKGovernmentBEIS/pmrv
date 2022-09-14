import { HttpClientModule } from '@angular/common/http';
import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { TransferredCO2MonitoringApproach } from 'pmrv-api';

import { BasePage } from '../../../../../../testing';
import { SharedModule } from '../../../../../shared/shared.module';
import { AppliedTierPipe } from '../../../../shared/pipes/applied-tier.pipe';
import { MeasurementDevicesLabelPipe } from '../../../../shared/pipes/measurement-devices-label.pipe';
import { SamplingFrequencyPipe } from '../../../../shared/pipes/sampling-frequency.pipe';
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
    template: ` <app-accounting-summary-template [accounting]="accounting"></app-accounting-summary-template> `,
  })
  class TestComponent {
    accounting = (
      mockPermitApplyPayload.permit.monitoringApproaches.TRANSFERRED_CO2 as TransferredCO2MonitoringApproach
    ).accountingEmissions;
  }

  class Page extends BasePage<TestComponent> {
    get summaryDefinitions() {
      return this.queryAll('dd').map((row) => row.textContent.trim());
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientModule, SharedModule],
      declarations: [
        TestComponent,
        SummaryTemplateComponent,
        MeasurementDevicesLabelPipe,
        SamplingFrequencyPipe,
        AppliedTierPipe,
      ],
      providers: [],
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

  it('should display the accounting emissions summary', () => {
    expect(page.summaryDefinitions).toEqual([
      'No',
      'ref1, Ultrasonic meter , Specified uncertainty ±2',
      'Daily',
      'Tier 4',
    ]);
  });
});
