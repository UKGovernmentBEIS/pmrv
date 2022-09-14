import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { CessationModule } from '@permit-revocation/cessation/cessation.module';
import { PermitRevocationModule } from '@permit-revocation/permit-revocation.module';
import { SummaryComponent } from '@permit-revocation/permit-revocation-apply/summary';
import { PermitRevocationStore } from '@permit-revocation/store/permit-revocation-store';
import { mockTaskState } from '@permit-revocation/testing/mock-state';

import { ActivatedRouteStub, BasePage } from '../../../../testing';

describe('SummaryComponent', () => {
  let component: SummaryComponent;
  let fixture: ComponentFixture<SummaryComponent>;
  let store: PermitRevocationStore;
  let page: Page;

  class Page extends BasePage<SummaryComponent> {
    get details() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  const route = new ActivatedRouteStub({ taskId: mockTaskState.requestTaskId }, null, {
    statusKey: 'REVOCATION_APPLY',
  });

  const createComponent = () => {
    fixture = TestBed.createComponent(SummaryComponent);
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
      permitRevocation: {
        reason: 'Because i have to',
        activitiesStopped: true,
        stoppedDate: '2022-04-14',
        effectiveDate: '2022-05-16',
        surrenderRequired: false,
        annualEmissionsReportRequired: true,
        annualEmissionsReportDate: '2022-04-14',
        feeCharged: false,
      },
      sectionsCompleted: { REVOCATION_APPLY: true },
    });
  });

  beforeEach(createComponent);

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render permit revocation details', () => {
    expect(page.details).toEqual([
      ['Reason for revoking permit', 'Because i have to'],
      ['Have the regulated activities at the installation stopped?', '14 Apr 2022'],
      ['Effective date of notice', '16 May 2022'],
      ['Annual emissions of monitoring revocation report required', 'Required by 14 Apr 2022'],
      ['Surrender of allowances required', 'No'],
      ['Operator fee required', 'No'],
    ]);
  });
});
