import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { OfficialNoticeRecipientsComponent } from '@permit-revocation/shared/official-notice-recipients/official-notice-recipients.component';
import { PermitRevocationStore } from '@permit-revocation/store/permit-revocation-store';
import { mockTaskState } from '@permit-revocation/testing/mock-state';
import { WithdrawSummaryComponent } from '@permit-revocation/wait-for-appeal-reason/summary';
import { SharedModule } from '@shared/shared.module';

import { WithdrawCompletedComponent } from './withdraw-completed.component';

describe('WithdrawCompleteComponent', () => {
  let component: WithdrawCompletedComponent;
  let fixture: ComponentFixture<WithdrawCompletedComponent>;
  let store: PermitRevocationStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule],
      declarations: [WithdrawCompletedComponent, WithdrawSummaryComponent, OfficialNoticeRecipientsComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(WithdrawCompletedComponent);
    component = fixture.componentInstance;
    store = TestBed.inject(PermitRevocationStore);
    store.setState({
      ...mockTaskState,
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
    });
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
