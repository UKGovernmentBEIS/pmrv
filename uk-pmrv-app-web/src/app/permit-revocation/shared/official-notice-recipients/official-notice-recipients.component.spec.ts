import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { CessationModule } from '@permit-revocation/cessation/cessation.module';
import { PermitRevocationModule } from '@permit-revocation/permit-revocation.module';
import { PermitRevocationStore } from '@permit-revocation/store/permit-revocation-store';

import { ActivatedRouteStub, BasePage } from '../../../../testing';
import { mockActionState } from '../../testing/mock-state';
import { OfficialNoticeRecipientsComponent } from './official-notice-recipients.component';

describe('OfficialNoticeRecipientsComponent', () => {
  let component: OfficialNoticeRecipientsComponent;
  let fixture: ComponentFixture<OfficialNoticeRecipientsComponent>;
  let store: PermitRevocationStore;
  let page: Page;

  class Page extends BasePage<OfficialNoticeRecipientsComponent> {
    get summaryList() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  const route = new ActivatedRouteStub({ actionId: mockActionState.requestActionId }, null, null);

  const createComponent = () => {
    fixture = TestBed.createComponent(OfficialNoticeRecipientsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    component.noticeRecipientsData = {
      decisionNotification: {
        operators: ['975eb886-cc78-40f4-95ca-a98e665af6ca'],
        signatory: 'c5508398-b325-46ab-81ff-d83024aff5ce',
      },
      usersInfo: {
        'c5508398-b325-46ab-81ff-d83024aff5ce': {
          name: 'Regulator England',
        },
        '975eb886-cc78-40f4-95ca-a98e665af6ca': {
          name: 'John Doe',
          roleCode: 'operator_admin',
          contactTypes: ['PRIMARY', 'FINANCIAL', 'SERVICE'],
        },
      },
      officialNotice: {
        name: 'off notice.pdf',
        uuid: 'b9d7472d-14b7-4a45-a1c1-1c3694842664',
      },
    };
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, PermitRevocationModule, CessationModule],
      providers: [{ provide: ActivatedRoute, useValue: route }],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(PermitRevocationStore);
    store.setState({
      ...store.getState(),
      requestActionId: mockActionState.requestActionId,
    });
  });

  afterEach(() => jest.clearAllMocks());

  it('should create', () => {
    createComponent();
    expect(component).toBeTruthy();
  });

  it('should render Official notice recipients', () => {
    createComponent();
    fixture.detectChanges();
    expect(page.summaryList).toEqual([
      ['Users', 'John Doe, Operator admin - Primary contact, Financial contact, Service contact'],
      ['Name and signature on the official notice', 'Regulator England'],
      ['Official notice', 'off notice.pdf'],
    ]);
  });

  it('should render Withdrawn official notice recipients', () => {
    createComponent();
    component.noticeRecipientsData = {
      ...component.noticeRecipientsData,
      officialNotice: null,
      withdrawnOfficialNotice: {
        name: 'withdraw off notice.pdf',
        uuid: 'b9d7472d-14b7-4a45-a1c1-1c3694842664',
      },
    };
    fixture.detectChanges();
    expect(page.summaryList).toEqual([
      ['Users', 'John Doe, Operator admin - Primary contact, Financial contact, Service contact'],
      ['Name and signature on the official notice', 'Regulator England'],
      ['Official notice', 'withdraw off notice.pdf'],
    ]);
  });
});
