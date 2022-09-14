import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { ActivatedRouteStub, BasePage } from '../../../../testing';
import { PermitApplicationModule } from '../../permit-application.module';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockState } from '../../testing/mock-state';
import { AmendSummaryComponent } from './amend-summary.component';

describe('AmendSummaryComponent', () => {
  let component: AmendSummaryComponent;
  let fixture: ComponentFixture<AmendSummaryComponent>;
  let store: PermitApplicationStore;
  let page: Page;

  const route = new ActivatedRouteStub({ section: 'fuels' });

  class Page extends BasePage<AmendSummaryComponent> {
    get summaryLists() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PermitApplicationModule, RouterTestingModule],
      providers: [{ provide: ActivatedRoute, useValue: route }],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(PermitApplicationStore);
    store.setState({
      ...mockState,
      reviewGroupDecisions: {
        FUELS_AND_EQUIPMENT: {
          type: 'OPERATOR_AMENDS_NEEDED',
          changesRequired: 'Changes required',
          notes: 'notes',
        },
      },
    });
    fixture = TestBed.createComponent(AmendSummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display appropriate information', () => {
    expect(page.summaryLists).toHaveLength(2);

    const markAsCompleteSummaryListRows = page.summaryLists[1].querySelectorAll('.govuk-summary-list__row');
    expect(markAsCompleteSummaryListRows).toHaveLength(1);
    expect(markAsCompleteSummaryListRows[0].querySelector('dt').textContent.trim()).toEqual(
      'I have made changes and want to mark this task as complete',
    );
    expect(markAsCompleteSummaryListRows[0].querySelector('dd').textContent.trim()).toEqual('Yes');
  });
});
