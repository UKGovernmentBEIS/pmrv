import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { RequestActionsService, RequestItemsService, RequestsService } from 'pmrv-api';

import { ActivatedRouteSnapshotStub, BasePage, mockClass } from '../../../../testing';
import { SharedModule } from '../../../shared/shared.module';
import { WorkflowItemComponent } from './workflow-item.component';

describe('WorkflowItemComponent', () => {
  let component: WorkflowItemComponent;
  let activatedRoute: ActivatedRouteSnapshotStub;
  let fixture: ComponentFixture<WorkflowItemComponent>;
  let page: Page;

  const requestsService = mockClass(RequestsService);
  const requestItemsService = mockClass(RequestItemsService);
  const requestActionsService = mockClass(RequestActionsService);

  class Page extends BasePage<WorkflowItemComponent> {
    get heading() {
      return this.query<HTMLElement>('app-page-heading h1.govuk-heading-xl');
    }
    get tasks() {
      return this.queryAll<HTMLElement>('app-related-tasks h3');
    }
    get timeline() {
      return this.queryAll<HTMLElement>('app-timeline-item h3');
    }
  }

  const createComponent = async () => {
    fixture = TestBed.createComponent(WorkflowItemComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    activatedRoute = new ActivatedRouteSnapshotStub({ 'request-id': '1' });
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule],
      declarations: [WorkflowItemComponent],
      providers: [
        { provide: ActivatedRouteSnapshotStub, useValue: activatedRoute },
        { provide: RequestsService, useValue: requestsService },
        { provide: RequestItemsService, useValue: requestItemsService },
        { provide: RequestActionsService, useValue: requestActionsService },
      ],
    }).compileComponents();
  });

  describe('display all info', () => {
    beforeEach(() => {
      requestsService.getRequestDetailsByIdUsingGET.mockReturnValue(
        of({
          id: '1',
          requestType: 'PERMIT_ISSUANCE',
          requestStatus: 'IN_PROGRESS',
          creationDate: '22-2-2022',
        }),
      );

      requestItemsService.getItemsByRequestUsingGET.mockReturnValue(
        of({
          items: [
            {
              taskId: 1,
              requestType: 'INSTALLATION_ACCOUNT_OPENING',
              taskType: 'INSTALLATION_ACCOUNT_OPENING_ARCHIVE',
            },
            {
              taskId: 2,
              requestType: 'INSTALLATION_ACCOUNT_OPENING',
              taskType: 'ACCOUNT_USERS_SETUP',
            },
          ],
        }),
      );

      requestActionsService.getRequestActionsByRequestIdUsingGET.mockReturnValue(
        of([
          {
            id: 1,
            creationDate: '2020-08-25 10:36:15.189643',
            submitter: 'Operator',
            type: 'INSTALLATION_ACCOUNT_OPENING_APPLICATION_SUBMITTED',
          },
          {
            id: 2,
            creationDate: '2020-08-26 10:36:15.189643',
            submitter: 'Regulator',
            type: 'INSTALLATION_ACCOUNT_OPENING_ACCEPTED',
          },
        ]),
      );
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display request details', () => {
      expect(page.heading.textContent.trim()).toEqual('Permit application IN PROGRESS');
    });

    it('should display tasks to complete', () => {
      expect(page.tasks).toBeTruthy();
      expect(page.tasks.map((el) => el.textContent)).toEqual(['Application', 'Set up']);
    });

    it('should display timeline', () => {
      expect(page.timeline).toBeTruthy();
      expect(page.timeline.map((el) => el.textContent)).toEqual([
        'The regulator accepted the installation account application',
        'Original application',
      ]);
    });
  });

  describe('display only request details', () => {
    beforeEach(() => {
      requestsService.getRequestDetailsByIdUsingGET.mockReturnValue(
        of({
          id: '1',
          requestType: 'PERMIT_ISSUANCE',
          requestStatus: 'IN_PROGRESS',
          creationDate: '22-2-2022',
        }),
      );

      requestItemsService.getItemsByRequestUsingGET.mockReturnValue(of({}));

      requestActionsService.getRequestActionsByRequestIdUsingGET.mockReturnValue(of([]));
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display request details', () => {
      expect(page.heading.textContent.trim()).toEqual('Permit application IN PROGRESS');
    });

    it('should not display any task to complete', () => {
      expect(page.tasks).toBeTruthy();
      expect(page.tasks.map((el) => el.textContent)).toEqual([]);
    });

    it('should not display timeline', () => {
      expect(page.timeline).toBeTruthy();
      expect(page.timeline.map((el) => el.textContent)).toEqual([]);
    });
  });
});
