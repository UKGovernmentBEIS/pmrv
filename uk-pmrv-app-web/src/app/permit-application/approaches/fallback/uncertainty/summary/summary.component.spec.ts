import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { FallbackMonitoringApproach } from 'pmrv-api';

import { ActivatedRouteStub, BasePage } from '../../../../../../testing';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { mockPermitApplyPayload } from '../../../../testing/mock-permit-apply-action';
import { mockState } from '../../../../testing/mock-state';
import { FallbackModule } from '../../fallback.module';
import { SummaryComponent } from './summary.component';

describe('SummaryComponent', () => {
  let page: Page;
  let component: SummaryComponent;
  let store: PermitApplicationStore;
  let fixture: ComponentFixture<SummaryComponent>;

  class Page extends BasePage<SummaryComponent> {
    get summaryListValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }

    get notificationBanner() {
      return this.query('.govuk-notification-banner');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FallbackModule, RouterTestingModule],
      providers: [{ provide: ActivatedRoute, useValue: new ActivatedRouteStub({ taskId: '279' }) }],
    }).compileComponents();
  });

  beforeEach(() => {
    const router = TestBed.inject(Router);
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { notification: true } } } as any);
    fixture = TestBed.createComponent(SummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    store = TestBed.inject(PermitApplicationStore);
    store.setState(mockState);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the list of data', () => {
    const annualUncertaintyAnalysis = (
      mockPermitApplyPayload.permit.monitoringApproaches.FALLBACK as FallbackMonitoringApproach
    ).annualUncertaintyAnalysis;

    expect(page.summaryListValues).toEqual([
      ['Procedure description', annualUncertaintyAnalysis.procedureDescription],
      ['Procedure document', annualUncertaintyAnalysis.procedureDocumentName],
      ['Procedure reference', annualUncertaintyAnalysis.procedureReference],
      ['Department or role that’s responsible', annualUncertaintyAnalysis.responsibleDepartmentOrRole],
      ['Location of records', annualUncertaintyAnalysis.locationOfRecords],
    ]);
  });

  it('should display the notification banner', () => {
    expect(page.notificationBanner).toBeTruthy();
  });
});
