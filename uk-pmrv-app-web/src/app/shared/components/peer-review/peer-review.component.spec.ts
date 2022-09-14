import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { TasksAssignmentService, TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, MockType } from '../../../../testing';
import { PermitApplicationStore } from '../../../permit-application/store/permit-application.store';
import { mockReviewStateBuild as permitMockState } from '../../../permit-application/testing/mock-state';
import { PermitRevocationStore } from '../../../permit-revocation/store/permit-revocation-store';
import { mockTaskState as revocationMockState } from '../../../permit-revocation/testing/mock-state';
import { PermitSurrenderStore } from '../../../permit-surrender/store/permit-surrender.store';
import { mockTaskState as surrenderMockState } from '../../../permit-surrender/testing/mock-state';
import { SharedModule } from '../../shared.module';
import { StoreResolver } from '../../store-resolver/store-resolver';
import { PeerReviewComponent } from './peer-review.component';

describe('PeerReviewComponent', () => {
  let page: Page;
  let component: PeerReviewComponent;
  let fixture: ComponentFixture<PeerReviewComponent>;
  let router: Router;

  let route: ActivatedRouteStub;
  let tasksAssignmentService: Partial<jest.Mocked<TasksAssignmentService>>;
  let tasksService: MockType<TasksService>;
  let storeResolver: MockType<StoreResolver>;
  let permitStore: PermitApplicationStore;
  let surrenderStore: PermitSurrenderStore;
  let revocationStore: PermitRevocationStore;
  class Page extends BasePage<PeerReviewComponent> {
    set assignees(value: string) {
      this.setInputValue('#assignees', value);
    }

    get assigneesList() {
      return Array.from(this.query<HTMLDivElement>('#assignees').querySelectorAll('option')).map((item) =>
        item.textContent.trim(),
      );
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }

    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get errorSummaryLinks() {
      return Array.from(this.query<HTMLDivElement>('.govuk-error-summary').querySelectorAll('a')).map((item) =>
        item.textContent.trim(),
      );
    }
  }

  const mockAssignees = [
    {
      id: '45b2620b-c859-4296-bb58-e49f180f6137',
      firstName: 'Regulator5',
      lastName: 'User',
    },
    {
      id: 'eaa82cc8-0a7d-4f2d-bcf7-f54f612f59e5',
      firstName: 'newreg1',
      lastName: 'User',
    },
    {
      id: '44c7a770-18b2-40e8-85ee-5c92210618d7',
      firstName: 'newreg2',
      lastName: 'User',
    },
  ];

  beforeEach(async () => {
    tasksAssignmentService = {
      getCandidateAssigneesByTaskTypeUsingGET: jest.fn().mockReturnValue(of(mockAssignees)),
    };

    tasksService = {
      processRequestTaskActionUsingPOST: jest.fn().mockReturnValue(of({})),
    };

    storeResolver = {
      getStore: jest.fn().mockReturnValue(of('permit-application')),
    };

    route = new ActivatedRouteStub({ taskId: '237', index: '0' });

    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule],
      providers: [
        { provide: TasksAssignmentService, useValue: tasksAssignmentService },
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksService, useValue: tasksService },
        { provide: StoreResolver, useValue: storeResolver },
      ],
    }).compileComponents();
    permitStore = TestBed.inject(PermitApplicationStore);
    surrenderStore = TestBed.inject(PermitSurrenderStore);
    revocationStore = TestBed.inject(PermitRevocationStore);
  });

  describe('for permit application', () => {
    beforeEach(() => {
      const mockReviewState = permitMockState();
      router = TestBed.inject(Router);
      jest.spyOn(router, 'url', 'get').mockReturnValue('/permit-application/237/review/peer-review');
      permitStore.setState({
        ...mockReviewState,
        allowedRequestTaskActions: ['PERMIT_ISSUANCE_REQUEST_PEER_REVIEW'],
      });
      fixture = TestBed.createComponent(PeerReviewComponent);
      component = fixture.componentInstance;
      page = new Page(fixture);
      fixture.detectChanges();
    });
    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should get assignees and submit a valid form', () => {
      expect(page.errorSummary).toBeFalsy();

      page.submitButton.click();
      fixture.detectChanges();
      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryLinks).toEqual(['Select a peer reviewer']);

      expect(tasksAssignmentService.getCandidateAssigneesByTaskTypeUsingGET).toHaveBeenCalledTimes(1);
      expect(page.assigneesList).toEqual(['Regulator5 User', 'newreg1 User', 'newreg2 User']);

      page.assignees = `0: ${mockAssignees[0].id}`;
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();

      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith({
        requestTaskActionType: 'PERMIT_ISSUANCE_REQUEST_PEER_REVIEW',
        requestTaskId: 237,
        requestTaskActionPayload: {
          payloadType: 'PERMIT_ISSUANCE_REQUEST_PEER_REVIEW_PAYLOAD',
          peerReviewer: '45b2620b-c859-4296-bb58-e49f180f6137',
        },
      });
    });
  });

  describe('for permit surrender', () => {
    beforeEach(() => {
      const mockReviewState = surrenderMockState;
      router = TestBed.inject(Router);
      jest.spyOn(router, 'url', 'get').mockReturnValue('/permit-surrender/237/review/peer-review');
      surrenderStore.setState({
        ...mockReviewState,
        allowedRequestTaskActions: ['PERMIT_SURRENDER_REQUEST_PEER_REVIEW'],
      });
      fixture = TestBed.createComponent(PeerReviewComponent);
      component = fixture.componentInstance;
      page = new Page(fixture);
      fixture.detectChanges();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should get assignees and submit a valid form', () => {
      expect(page.errorSummary).toBeFalsy();

      page.submitButton.click();
      fixture.detectChanges();
      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryLinks).toEqual(['Select a peer reviewer']);

      expect(tasksAssignmentService.getCandidateAssigneesByTaskTypeUsingGET).toHaveBeenCalledTimes(1);
      expect(page.assigneesList).toEqual(['Regulator5 User', 'newreg1 User', 'newreg2 User']);

      page.assignees = `0: ${mockAssignees[0].id}`;
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();

      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith({
        requestTaskActionType: 'PERMIT_SURRENDER_REQUEST_PEER_REVIEW',
        requestTaskId: 237,
        requestTaskActionPayload: {
          payloadType: 'PERMIT_SURRENDER_REQUEST_PEER_REVIEW_PAYLOAD',
          peerReviewer: '45b2620b-c859-4296-bb58-e49f180f6137',
        },
      });
    });
  });

  describe('for permit revocation', () => {
    beforeEach(() => {
      const mockReviewState = revocationMockState;
      router = TestBed.inject(Router);
      jest.spyOn(router, 'url', 'get').mockReturnValue('/permit-revocation/237/review/peer-review');
      revocationStore.setState({
        ...mockReviewState,
        allowedRequestTaskActions: ['PERMIT_REVOCATION_REQUEST_PEER_REVIEW'],
      });
      fixture = TestBed.createComponent(PeerReviewComponent);
      component = fixture.componentInstance;
      page = new Page(fixture);
      fixture.detectChanges();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should get assignees and submit a valid form', () => {
      expect(page.errorSummary).toBeFalsy();

      page.submitButton.click();
      fixture.detectChanges();
      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryLinks).toEqual(['Select a peer reviewer']);

      expect(tasksAssignmentService.getCandidateAssigneesByTaskTypeUsingGET).toHaveBeenCalledTimes(1);
      expect(page.assigneesList).toEqual(['Regulator5 User', 'newreg1 User', 'newreg2 User']);

      page.assignees = `0: ${mockAssignees[0].id}`;
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();

      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith({
        requestTaskActionType: 'PERMIT_REVOCATION_REQUEST_PEER_REVIEW',
        requestTaskId: 237,
        requestTaskActionPayload: {
          payloadType: 'PERMIT_REVOCATION_REQUEST_PEER_REVIEW_PAYLOAD',
          peerReviewer: '45b2620b-c859-4296-bb58-e49f180f6137',
        },
      });
    });
  });
});
