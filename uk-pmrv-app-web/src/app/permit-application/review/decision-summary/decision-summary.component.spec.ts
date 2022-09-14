import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BehaviorSubject } from 'rxjs';

import { UserStatusDTO } from 'pmrv-api';

import { BasePage } from '../../../../testing';
import { AuthService } from '../../../core/services/auth.service';
import { SharedModule } from '../../../shared/shared.module';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockState } from '../../testing/mock-state';
import { DecisionSummaryComponent } from './decision-summary.component';

describe('DecisionSummaryComponent', () => {
  let component: DecisionSummaryComponent;
  let fixture: ComponentFixture<DecisionSummaryComponent>;
  let store: PermitApplicationStore;
  let page: Page;
  const authService: Partial<jest.Mocked<AuthService>> = {
    userStatus: new BehaviorSubject<UserStatusDTO>({
      roleType: 'REGULATOR',
    }),
  };

  class Page extends BasePage<DecisionSummaryComponent> {
    get heading() {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }
    get titles() {
      return this.queryAll<HTMLDListElement>('dl').map((el) =>
        Array.from(el.querySelectorAll('dt')).map((dt) => dt.textContent.trim()),
      );
    }
    get values() {
      return this.queryAll<HTMLDListElement>('dl').map((el) =>
        Array.from(el.querySelectorAll('dd')).map((dd) => dd.textContent.trim()),
      );
    }
  }

  const createComponent = async () => {
    fixture = TestBed.createComponent(DecisionSummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule],
      providers: [{ provide: AuthService, useValue: authService }],
      declarations: [DecisionSummaryComponent],
    }).compileComponents();
  });

  describe('for granted permit application', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState({
        ...mockState,
        determination: {
          activationDate: '2022-02-22 14:26:44',
          reason: 'Grant reason',
          type: 'GRANTED',
        },
        permitDecisionNotification: {
          operators: ['op1', 'op2'],
          signatory: 'reg',
        },
        usersInfo: {
          op1: {
            contactTypes: ['PRIMARY'],
            name: 'Operator1 Name',
          },
          op2: {
            contactTypes: ['PRIMARY'],
            name: 'Operator2 Name',
          },
          reg: {
            name: 'Regulator Name',
          },
        },
        creationDate: '2022-02-20 14:26:44',
      });
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should show the details', () => {
      expect(page.heading).toEqual('Permit application approved 20 Feb 2022');
      expect(page.titles).toEqual([
        ['Permit application', 'Decision', 'Reason for decision', 'Permit effective date'],
        ['Users', 'Name and signature on the official notice'],
      ]);
      expect(page.values).toEqual([
        ['Permit application', 'Grant', 'Grant reason', '22 Feb 2022'],
        ['Operator1 Name - Primary contact  Operator2 Name - Primary contact', 'Regulator Name'],
      ]);
    });
  });

  describe('for rejected permit application', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState({
        ...mockState,
        determination: {
          reason: 'Reject reason',
          type: 'REJECTED',
        },
        permitDecisionNotification: {
          operators: ['op1', 'op2'],
          signatory: 'reg',
        },
        usersInfo: {
          op1: {
            contactTypes: ['PRIMARY'],
            name: 'Operator1 Name',
          },
          op2: {
            contactTypes: ['PRIMARY'],
            name: 'Operator2 Name',
          },
          reg: {
            name: 'Regulator Name',
          },
        },
        creationDate: '2022-02-20 14:26:44',
      });
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should show the details', () => {
      expect(page.heading).toEqual('Permit application rejected 20 Feb 2022');
      expect(page.titles).toEqual([
        ['Decision', 'Reason for decision'],
        ['Users', 'Name and signature on the official notice'],
      ]);
      expect(page.values).toEqual([
        ['Reject', 'Reject reason'],
        ['Operator1 Name - Primary contact  Operator2 Name - Primary contact', 'Regulator Name'],
      ]);
    });
  });

  describe('for deemed withdrawn permit application', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState({
        ...mockState,
        determination: {
          reason: 'Deemed withdrawn reason',
          type: 'DEEMED_WITHDRAWN',
        },
        permitDecisionNotification: {
          operators: ['op1', 'op2'],
          signatory: 'reg',
        },
        usersInfo: {
          op1: {
            contactTypes: ['PRIMARY'],
            name: 'Operator1 Name',
          },
          op2: {
            contactTypes: ['PRIMARY'],
            name: 'Operator2 Name',
          },
          reg: {
            name: 'Regulator Name',
          },
        },
        creationDate: '2022-02-20 14:26:44',
      });
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should show the details', () => {
      expect(page.heading).toEqual('Permit application deemed withdrawn 20 Feb 2022');
      expect(page.titles).toEqual([
        ['Decision', 'Reason for decision'],
        ['Users', 'Name and signature on the official notice'],
      ]);
      expect(page.values).toEqual([
        ['Deem withdrawn', 'Deemed withdrawn reason'],
        ['Operator1 Name - Primary contact  Operator2 Name - Primary contact', 'Regulator Name'],
      ]);
    });
  });
});
