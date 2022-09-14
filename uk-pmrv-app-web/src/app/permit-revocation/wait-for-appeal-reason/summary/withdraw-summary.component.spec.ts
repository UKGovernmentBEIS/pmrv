import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { CessationModule } from '@permit-revocation/cessation/cessation.module';
import { PermitRevocationModule } from '@permit-revocation/permit-revocation.module';
import { PermitRevocationStore } from '@permit-revocation/store/permit-revocation-store';

import { ActivatedRouteStub, BasePage } from '../../../../testing';
import { mockTaskState } from '../../testing/mock-state';
import { WithdrawSummaryComponent } from './withdraw-summary.component';

describe('Summary Component', () => {
  let component: WithdrawSummaryComponent;
  let fixture: ComponentFixture<WithdrawSummaryComponent>;
  let store: PermitRevocationStore;
  let page: Page;

  class Page extends BasePage<WithdrawSummaryComponent> {
    get summaryList() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  const route = new ActivatedRouteStub({ taskId: mockTaskState.requestTaskId }, null, null);

  const createComponent = () => {
    fixture = TestBed.createComponent(WithdrawSummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, PermitRevocationModule, CessationModule],
      providers: [{ provide: ActivatedRoute, useValue: route }],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(PermitRevocationStore);
  });

  afterEach(() => jest.clearAllMocks());

  beforeEach(() => {
    store.setState({
      ...store.getState(),
      requestTaskId: mockTaskState.requestTaskId,
      isEditable: true,
      reason: 'because i have to...',
      withdrawFiles: ['823db18f-5a46-47a8-adb4-67eb5f711646'],
      revocationAttachments: { '823db18f-5a46-47a8-adb4-67eb5f711646': 'pd_ratings.csv' },
    });
  });

  it('should create', () => {
    createComponent();
    expect(component).toBeTruthy();
  });

  it('should render surrender values', () => {
    createComponent();
    expect(page.summaryList).toEqual([
      ['Provide a reason', 'because i have to...'],
      ['Upload additional evidence (optional)', 'pd_ratings.csv'],
    ]);
  });
});
