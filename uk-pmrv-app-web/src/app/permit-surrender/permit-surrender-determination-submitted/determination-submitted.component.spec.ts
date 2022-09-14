import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BehaviorSubject } from 'rxjs';

import {
  PermitSurrenderReviewDeterminationDeemWithdraw,
  PermitSurrenderReviewDeterminationGrant,
  PermitSurrenderReviewDeterminationReject,
  UserStatusDTO,
} from 'pmrv-api';

import { BasePage } from '../../../testing';
import { AuthService } from '../../core/services/auth.service';
import { PermitSurrenderModule } from '../permit-surrender.module';
import { PermitSurrenderStore } from '../store/permit-surrender.store';
import { mockTaskState } from '../testing/mock-state';
import { DeterminationSubmittedComponent } from './determination-submitted.component';

describe('DeterminationSubmittedComponent', () => {
  let page: Page;
  let component: DeterminationSubmittedComponent;
  let fixture: ComponentFixture<DeterminationSubmittedComponent>;
  let store: PermitSurrenderStore;

  let authService: Partial<jest.Mocked<AuthService>>;
  let userStatus$: BehaviorSubject<UserStatusDTO>;

  class Page extends BasePage<DeterminationSubmittedComponent> {
    get answers() {
      return this.queryAll('dd').map((row) => row.textContent.trim());
    }
  }

  const createModule = async () => {
    userStatus$ = new BehaviorSubject<UserStatusDTO>({
      loginStatus: 'ENABLED',
      roleType: 'REGULATOR',
      userId: 'opTestId',
    });

    authService = {
      userStatus: userStatus$,
      loadUserStatus: jest.fn(),
    };
    await TestBed.configureTestingModule({
      imports: [PermitSurrenderModule, RouterTestingModule],
      providers: [{ provide: AuthService, useValue: authService }],
    }).compileComponents();
  };

  const createComponent = () => {
    fixture = TestBed.createComponent(DeterminationSubmittedComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  describe('For Granted', () => {
    beforeEach(createModule);

    beforeEach(() => {
      store = TestBed.inject(PermitSurrenderStore);
      store.setState({
        ...mockTaskState,
        reviewDecisionNotification: {
          operators: ['471c1c9b-8f98-4107-ba32-400360d39ada'],
          externalContacts: [1],
          signatory: '45b2620b-c859-4296-bb58-e49f180f6137',
        },
        reviewDetermination: {
          type: 'GRANTED',
          reason: 'this is grant reason',
          stopDate: '2022-01-05',
          noticeDate: '2022-05-05',
          reportRequired: false,
          allowancesSurrenderRequired: false,
        } as PermitSurrenderReviewDeterminationGrant,
        usersInfo: {
          '471c1c9b-8f98-4107-ba32-400360d39ada': {
            name: '25@o makos',
            roleCode: 'operator_admin',
            contactTypes: ['PRIMARY', 'SERVICE', 'FINANCIAL'],
          },
          '45b2620b-c859-4296-bb58-e49f180f6137': {
            name: 'Regulator5 Makos',
          },
        },
        officialNotice: {
          name: 'off notice.pdf',
          uuid: 'b9d7472d-14b7-4a45-a1c1-1c3694842664',
        },
      });
    });

    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display the answers', () => {
      expect(page.answers).toEqual([
        'Grant',
        'this is grant reason',
        '5 Jan 2022',
        '5 May 2022',
        'No',
        'No',
        '25@o makos, Operator admin - Primary contact, Service contact, Financial contact',
        'Regulator5 Makos',
        'off notice.pdf',
      ]);
    });
  });

  describe('For Rejected', () => {
    beforeEach(createModule);

    beforeEach(() => {
      store = TestBed.inject(PermitSurrenderStore);
      store.setState({
        ...mockTaskState,
        reviewDecisionNotification: {
          operators: ['471c1c9b-8f98-4107-ba32-400360d39ada'],
          externalContacts: [1],
          signatory: '45b2620b-c859-4296-bb58-e49f180f6137',
        },
        reviewDetermination: {
          type: 'REJECTED',
          reason: 'a reason',
          officialRefusalLetter: 'text to be included',
          shouldFeeBeRefundedToOperator: true,
        } as PermitSurrenderReviewDeterminationReject,
        usersInfo: {
          '471c1c9b-8f98-4107-ba32-400360d39ada': {
            name: '25@o makos',
            roleCode: 'operator_admin',
            contactTypes: ['PRIMARY', 'SERVICE', 'FINANCIAL'],
          },
          '45b2620b-c859-4296-bb58-e49f180f6137': {
            name: 'Regulator5 Makos',
          },
        },
      });
    });

    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display the answers', () => {
      expect(page.answers).toEqual([
        'Reject',
        'a reason',
        'text to be included',
        'Yes',
        '25@o makos, Operator admin - Primary contact, Service contact, Financial contact',
        'Regulator5 Makos',
      ]);
    });
  });

  describe('For Deem withdrawn', () => {
    beforeEach(createModule);

    beforeEach(() => {
      store = TestBed.inject(PermitSurrenderStore);
      store.setState({
        ...mockTaskState,
        reviewDecisionNotification: {
          operators: ['471c1c9b-8f98-4107-ba32-400360d39ada'],
          externalContacts: [1],
          signatory: '45b2620b-c859-4296-bb58-e49f180f6137',
        },
        reviewDetermination: {
          type: 'DEEMED_WITHDRAWN',
          reason: 'tggfffff',
        } as PermitSurrenderReviewDeterminationDeemWithdraw,
        usersInfo: {
          '471c1c9b-8f98-4107-ba32-400360d39ada': {
            name: '25@o makos',
            roleCode: 'operator_admin',
            contactTypes: ['PRIMARY', 'SERVICE', 'FINANCIAL'],
          },
          '45b2620b-c859-4296-bb58-e49f180f6137': {
            name: 'Regulator5 Makos',
          },
        },
      });
    });

    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display the answers', () => {
      expect(page.answers).toEqual([
        'Deem withdrawn',
        'tggfffff',
        '25@o makos, Operator admin - Primary contact, Service contact, Financial contact',
        'Regulator5 Makos',
      ]);
    });
  });
});
