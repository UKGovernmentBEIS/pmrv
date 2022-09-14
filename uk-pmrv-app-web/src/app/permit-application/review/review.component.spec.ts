import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { RequestActionsService, RequestItemsService, TasksService } from 'pmrv-api';

import { BasePage, mockClass } from '../../../testing';
import { AuthService } from '../../core/services/auth.service';
import { SharedModule } from '../../shared/shared.module';
import { SharedPermitModule } from '../shared/shared-permit.module';
import { PermitApplicationStore } from '../store/permit-application.store';
import { mockReviewState } from '../testing/mock-state';
import { ReviewComponent } from './review.component';

describe('ReviewComponent', () => {
  let component: ReviewComponent;
  let fixture: ComponentFixture<ReviewComponent>;
  let page: Page;
  let store: PermitApplicationStore;

  const requestActionsService = mockClass(RequestActionsService);
  const tasksService = mockClass(TasksService);
  const authService = mockClass(AuthService);
  const requestItemsService = mockClass(RequestItemsService);

  class Page extends BasePage<ReviewComponent> {
    get reviewGroups() {
      return this.queryAll<HTMLLIElement>('li[app-task-item]');
    }
    get heading() {
      return this.query('.govuk-heading-xl').textContent;
    }
    get headings() {
      return this.queryAll('.govuk-heading-m')?.map((el) => el.textContent);
    }
    get relatedTasks() {
      return this.queryAll('app-related-tasks h3.govuk-heading-s')?.map((el) => el.textContent);
    }
    get items() {
      return this.queryAll('.govuk-heading-s')?.map((el) => el.textContent);
    }
    get relatedActionsLinks() {
      return this.queryAll<HTMLLinkElement>('aside li a');
    }
    get headingInfo() {
      return this.queryAll<HTMLParagraphElement>('.govuk-heading-xl p');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule, SharedPermitModule],
      declarations: [ReviewComponent],
      providers: [
        { provide: RequestActionsService, useValue: requestActionsService },
        { provide: TasksService, useValue: tasksService },
        { provide: AuthService, useValue: authService },
        { provide: RequestItemsService, useValue: requestItemsService },
      ],
    });

    store = TestBed.inject(PermitApplicationStore);
  });

  beforeEach(() => {
    requestActionsService.getRequestActionsByRequestIdUsingGET.mockReturnValue(of([]));
    requestItemsService.getItemsByRequestUsingGET.mockReturnValue(
      of({
        items: [
          {
            requestType: 'PERMIT_ISSUANCE',
            taskType: 'PERMIT_ISSUANCE_WAIT_FOR_AMENDS',
            taskId: 1,
          },
          {
            requestType: 'PERMIT_ISSUANCE',
            taskType: 'PERMIT_ISSUANCE_APPLICATION_AMENDS_SUBMIT',
            taskId: 237,
          },
        ],
        totalItems: 2,
      }),
    );
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  const createComponent = () => {
    fixture = TestBed.createComponent(ReviewComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  describe('permit issuance task', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState({
        ...mockReviewState,
        isRequestTask: true,
        daysRemaining: 13,
        requestId: '1',
        requestTaskType: 'PERMIT_ISSUANCE_APPLICATION_REVIEW',
        allowedRequestTaskActions: ['PERMIT_ISSUANCE_SAVE_APPLICATION_REVIEW'],
      });
    });

    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display review groups', () => {
      expect(page.reviewGroups.map((group) => group.textContent.trim())).toEqual([
        'Permit type undecided',
        'Installation details undecided',
        'Fuels and equipment undecided',
        'Define monitoring approaches undecided',
        'Calculation approach undecided',
        'Measurement approach undecided',
        'Fall-back approach undecided',
        'Nitrous oxide (N2O) approach undecided',
        'Perfluorocarbons PFC approach undecided',
        'Inherent CO2 undecided',
        'Transferred CO2 undecided',
        'Uncertainty analysis undecided',
        'Management procedures undecided',
        'Monitoring methodology undecided',
        'Additional information undecided',
        'Confidentiality undecided',
        'Overall decision undecided',
      ]);
    });

    it('should show related tasks', () => {
      expect(page.relatedTasks).toEqual(['Permit determination']);
    });

    it('should show all headings', () => {
      expect(page.heading.trim()).toContain('GHGE permit determination');
      expect(page.headings).toEqual(['Permit details', 'Decision', 'Related tasks', 'Timeline', 'Related actions']);
    });

    it('should display related action links', () => {
      expect(page.relatedActionsLinks.map((el) => [el.href, el.textContent])).toEqual([
        ['http://localhost/tasks/237/change-assignee', 'Reassign task'],
      ]);
    });

    it('should display heading info', () => {
      expect(page.headingInfo.map((el) => el.textContent.trim())).toEqual([
        'Assigned to: John Doe',
        'Days Remaining: 13',
      ]);
    });
  });

  describe('permit variation task', () => {
    beforeEach(() => {
      store.setState({
        ...mockReviewState,
        isVariation: true,
        daysRemaining: 13,
        requestId: '1',
        requestTaskType: 'PERMIT_VARIATION_APPLICATION_REVIEW',
        allowedRequestTaskActions: ['PERMIT_VARIATION_SAVE_APPLICATION_REVIEW'],
        payloadType: 'PERMIT_VARIATION_APPLICATION_REVIEW_PAYLOAD',
        permitVariationDetailsReviewCompleted: false,
      });
    });

    beforeEach(createComponent);

    it('should show display variation details section', () => {
      expect(page.reviewGroups.map((group) => group.textContent.trim())).toEqual([
        'About the variation undecided',
        'Permit type undecided',
        'Installation details undecided',
        'Fuels and equipment undecided',
        'Define monitoring approaches undecided',
        'Calculation approach undecided',
        'Measurement approach undecided',
        'Fall-back approach undecided',
        'Nitrous oxide (N2O) approach undecided',
        'Perfluorocarbons PFC approach undecided',
        'Inherent CO2 undecided',
        'Transferred CO2 undecided',
        'Uncertainty analysis undecided',
        'Management procedures undecided',
        'Monitoring methodology undecided',
        'Additional information undecided',
        'Confidentiality undecided',
        'Overall decision undecided',
      ]);
    });
  });
});
