import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { ActivatedRouteStub, BasePage } from '../../../../testing';
import { SharedModule } from '../../../shared/shared.module';
import { PermitApplicationModule } from '../../permit-application.module';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockState } from '../../testing/mock-state';
import { MonitoringMethodologyPlanSummaryDetailsComponent } from './summary-details.component';

describe('MonitoringMethodologyPlanSummaryDetailsComponent', () => {
  let store: PermitApplicationStore;
  let component: MonitoringMethodologyPlanSummaryDetailsComponent;
  let fixture: ComponentFixture<MonitoringMethodologyPlanSummaryDetailsComponent>;
  let page: Page;

  const route = new ActivatedRouteStub({}, {}, { permitTask: 'monitoringMethodologyPlans' });

  class Page extends BasePage<MonitoringMethodologyPlanSummaryDetailsComponent> {
    get answers() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PermitApplicationModule, RouterTestingModule, SharedModule],
      providers: [{ provide: ActivatedRoute, useValue: route }],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(PermitApplicationStore);
    store.setState(mockState);
    fixture = TestBed.createComponent(MonitoringMethodologyPlanSummaryDetailsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display summary details', () => {
    expect(page.answers).toEqual([
      ['Are you supplying a Monitoring and Methodology Plan as part of your permit application?', 'No'],
    ]);
  });
});
