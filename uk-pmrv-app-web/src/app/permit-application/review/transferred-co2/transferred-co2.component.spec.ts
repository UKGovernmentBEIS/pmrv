import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { ActivatedRouteStub, BasePage } from '../../../../testing';
import { SharedModule } from '../../../shared/shared.module';
import { SharedPermitModule } from '../../shared/shared-permit.module';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockPermitCompletePayload } from '../../testing/mock-permit-apply-action';
import { mockReviewState } from '../../testing/mock-state';
import { TransferredCO2Component } from './transferred-co2.component';

describe('TransferredCO2Component', () => {
  let component: TransferredCO2Component;
  let fixture: ComponentFixture<TransferredCO2Component>;
  let page: Page;

  const route = new ActivatedRouteStub(
    {},
    {},
    {
      groupKey: 'TRANSFERRED_CO2',
    },
  );

  class Page extends BasePage<TransferredCO2Component> {
    get staticSections(): HTMLAnchorElement[] {
      return Array.from(this.queryAll<HTMLAnchorElement>('ul > li > span.app-task-list__task-name > a'));
    }

    get decisionForm() {
      return this.query<HTMLFormElement>('form');
    }
    get summaryDecisionValues() {
      return this.queryAll<HTMLDListElement>('app-review-group-decision > dl').map((decision) =>
        Array.from(decision.querySelectorAll('dd')).map((dd) => dd.textContent.trim()),
      );
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, SharedPermitModule, RouterTestingModule],
      providers: [{ provide: ActivatedRoute, useValue: route }],
      declarations: [TransferredCO2Component],
    }).compileComponents();
  });

  beforeEach(() => {
    const store = TestBed.inject(PermitApplicationStore);
    store.setState({
      ...mockReviewState,
      permit: mockPermitCompletePayload.permit,
      permitSectionsCompleted: mockPermitCompletePayload.permitSectionsCompleted,
      reviewGroupDecisions: {
        TRANSFERRED_CO2: {
          type: 'ACCEPTED',
          notes: 'notes',
        },
      },
      reviewSectionsCompleted: {
        TRANSFERRED_CO2: true,
      },
    });
    fixture = TestBed.createComponent(TransferredCO2Component);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the sections', () => {
    expect(page.staticSections.map((el) => el.textContent.trim())).toEqual(
      expect.arrayContaining([
        'Receiving and transferring installations',
        'Accounting for emissions from transferred CO2',
        'Deductions to amount of transferred CO2',
        'Procedure for leakage events',
        'Temperature and pressure measurement equipment',
        'Transfer of CO2',
        'Quantification methodologies',
        'Approach description',
      ]),
    );
  });

  it('should display appropriate sections for review', () => {
    expect(page.decisionForm).toBeFalsy();
    expect(page.summaryDecisionValues).toEqual([['Accepted', 'notes']]);
  });
});
