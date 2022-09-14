import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { PermitSurrenderReviewDeterminationReject } from 'pmrv-api';

import { BasePage } from '../../../../../../testing';
import { SharedModule } from '../../../../../shared/shared.module';
import { PermitSurrenderStore } from '../../../../store/permit-surrender.store';
import { mockTaskState } from '../../../../testing/mock-state';
import { SummaryComponent } from './summary.component';
import { SummaryDetailsComponent } from './summary-details.component';

describe('SummaryComponent', () => {
  let component: SummaryComponent;
  let fixture: ComponentFixture<SummaryComponent>;
  let page: Page;
  let store: PermitSurrenderStore;

  class Page extends BasePage<SummaryComponent> {
    get rows() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(SummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SummaryComponent, SummaryDetailsComponent],
      imports: [RouterTestingModule, SharedModule],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(PermitSurrenderStore);
    store.setState({
      ...mockTaskState,
      reviewDecision: {
        type: 'REJECTED',
        notes: 'rejected notes',
      },
      reviewDetermination: {
        type: 'REJECTED',
        reason: 'reason',
        officialRefusalLetter: 'official refusal letter',
        shouldFeeBeRefundedToOperator: true,
      } as PermitSurrenderReviewDeterminationReject,
    });
  });
  beforeEach(createComponent);

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('show reject details', () => {
    expect(page.rows).toEqual([
      ['Decision', 'Reject'],
      ['Reason for decision', 'reason'],
      ['Official refusal letter', 'official refusal letter'],
      ['Operator refund', 'Yes'],
    ]);
  });
});
