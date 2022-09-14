import { HttpClientModule } from '@angular/common/http';
import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { PFCMonitoringApproach } from 'pmrv-api';

import { BasePage } from '../../../../../../testing';
import { SharedModule } from '../../../../../shared/shared.module';
import { ProcedureFormSummaryComponent } from '../../../../shared/procedure-form-summary/procedure-form-summary.component';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { mockPermitApplyPayload } from '../../../../testing/mock-permit-apply-action';
import { mockState } from '../../../../testing/mock-state';
import { SummaryDetailsComponent } from './summary-details.component';

describe('SummaryDetailsComponent', () => {
  let component: SummaryDetailsComponent;
  let fixture: ComponentFixture<TestComponent>;
  let page: Page;
  let store: PermitApplicationStore;

  @Component({
    template: `
      <app-emission-factor-summary-details [emissionFactor]="emissionFactor"></app-emission-factor-summary-details>
    `,
  })
  class TestComponent {
    emissionFactor = (mockPermitApplyPayload.permit.monitoringApproaches.PFC as PFCMonitoringApproach)
      .tier2EmissionFactor;
  }

  class Page extends BasePage<TestComponent> {
    get summaryDefinitions() {
      return this.queryAll('dd').map((row) => row.textContent.trim());
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientModule, SharedModule],
      declarations: [TestComponent, ProcedureFormSummaryComponent, SummaryDetailsComponent],
      providers: [],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(PermitApplicationStore);
    store.setState(mockState);
    fixture = TestBed.createComponent(TestComponent);
    component = fixture.debugElement.query(By.directive(SummaryDetailsComponent)).componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the tier2 emission factor summary', () => {
    expect(page.summaryDefinitions).toEqual([
      'Yes',
      'procedureDescription2',
      'procedureDocumentName2',
      'procedureReference2',
      'responsibleDepartmentOrRole2',
      'locationOfRecords2',
      'procedureDescription1',
      'procedureDocumentName1',
      'procedureReference1',
      'responsibleDepartmentOrRole1',
      'locationOfRecords1',
    ]);
  });
});
