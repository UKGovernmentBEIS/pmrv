import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { SummaryComponent } from '@permit-revocation/permit-revocation-apply/summary';
import { OfficialNoticeRecipientsComponent } from '@permit-revocation/shared/official-notice-recipients/official-notice-recipients.component';
import { PermitRevocationStore } from '@permit-revocation/store/permit-revocation-store';
import { mockActionState } from '@permit-revocation/testing/mock-state';

import { ActivatedRouteStub } from '../../../testing';
import { SharedModule } from '../../shared/shared.module';
import { PermitRevocationSubmittedComponent } from './permit-revocation-submitted.component';

describe('PermitRevocationSubmittedComponent', () => {
  let component: PermitRevocationSubmittedComponent;
  let fixture: ComponentFixture<PermitRevocationSubmittedComponent>;
  let store: PermitRevocationStore;

  const route = new ActivatedRouteStub({ actionId: mockActionState.requestTaskId }, null, {
    pageTitle: 'Permit revocation submitted',
  });

  const createComponent = () => {
    fixture = TestBed.createComponent(PermitRevocationSubmittedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [PermitRevocationSubmittedComponent, SummaryComponent, OfficialNoticeRecipientsComponent],
      imports: [RouterTestingModule, SharedModule],
      providers: [{ provide: ActivatedRoute, useValue: route }],
    }).compileComponents();
    store = TestBed.inject(PermitRevocationStore);
  });

  beforeEach(() => {
    store.setState({
      ...store.getState(),
      requestTaskId: mockActionState.requestTaskId,
      isEditable: false,
      permitRevocation: mockActionState.permitRevocation,
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
      decisionNotification: {
        operators: ['975eb886-cc78-40f4-95ca-a98e665af6ca'],
        signatory: 'c5508398-b325-46ab-81ff-d83024aff5ce',
      },
      officialNotice: {
        name: 'off notice.pdf',
        uuid: 'b9d7472d-14b7-4a45-a1c1-1c3694842664',
      },
    });
  });

  it('should create', () => {
    createComponent();
    expect(component).toBeTruthy();
  });
});
