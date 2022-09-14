import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { ActivatedRouteStub, BasePage } from '../../../../testing';
import { SharedModule } from '../../../shared/shared.module';
import { PermitApplicationModule } from '../../permit-application.module';
import { SharedPermitModule } from '../../shared/shared-permit.module';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockState } from '../../testing/mock-state';
import { AmendSummaryTemplateComponent } from './amend-summary-template.component';

describe('AmendSummaryTemplateComponent', () => {
  let component: AmendSummaryTemplateComponent;
  let fixture: ComponentFixture<AmendSummaryTemplateComponent>;
  let store: PermitApplicationStore;
  let page: Page;

  const route = new ActivatedRouteStub({ section: 'monitoring-approaches' });

  class Page extends BasePage<AmendSummaryTemplateComponent> {
    get heading() {
      return this.query<HTMLElement>('app-page-heading');
    }

    get summaryLists() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, PermitApplicationModule, SharedModule, SharedPermitModule],
      providers: [{ provide: ActivatedRoute, useValue: route }],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(PermitApplicationStore);
    store.setState({
      ...mockState,
      reviewGroupDecisions: {
        DEFINE_MONITORING_APPROACHES: {
          type: 'OPERATOR_AMENDS_NEEDED',
          changesRequired: 'Changes required for monitoring approaches',
          notes: 'notes',
        },
        CALCULATION: {
          type: 'OPERATOR_AMENDS_NEEDED',
          changesRequired: 'Changes required for calculation approach',
          notes: 'notes',
        },
      },
    });
    fixture = TestBed.createComponent(AmendSummaryTemplateComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display appropriate information', () => {
    expect(page.heading.textContent.trim()).toEqual('Amends needed for monitoring approaches');

    expect(page.summaryLists).toHaveLength(2);
    expect(page.summaryLists[0].querySelector('dt').textContent.trim()).toEqual('Changes required');
    expect(page.summaryLists[0].querySelector('dd').textContent.trim()).toEqual(
      'Changes required for monitoring approaches',
    );

    expect(page.summaryLists[1].querySelector('dt').textContent.trim()).toEqual('Changes required');
    expect(page.summaryLists[1].querySelector('dd').textContent.trim()).toEqual(
      'Changes required for calculation approach',
    );
  });
});
