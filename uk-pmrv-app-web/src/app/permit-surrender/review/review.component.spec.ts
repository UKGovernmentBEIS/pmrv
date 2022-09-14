import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage, MockType } from '@testing';

import { RequestActionsService } from 'pmrv-api';

import { SharedPermitSurrenderModule } from '../shared/shared-permit-surrender.module';
import { PermitSurrenderStore } from '../store/permit-surrender.store';
import { mockTaskState } from '../testing/mock-state';
import { ReviewComponent } from './review.component';
import { ReviewSectionStatusPipe } from './review-section-status.pipe';

describe('ReviewComponent', () => {
  let component: ReviewComponent;
  let fixture: ComponentFixture<ReviewComponent>;
  let store: PermitSurrenderStore;
  let page: Page;

  let requestActionsService: MockType<RequestActionsService>;
  let route: ActivatedRouteStub;

  class Page extends BasePage<ReviewComponent> {
    get relatedActionsLinks() {
      return this.queryAll<HTMLLinkElement>('aside li a');
    }

    get reviewSections() {
      return Array.from(this.queryAll<HTMLLIElement>('li[app-task-item]'));
    }

    get reviewSectionLinks() {
      return this.reviewSections.map((section) => section.querySelector('a'));
    }

    get reviewSectionStatuses() {
      return this.reviewSections.map((section) => section.querySelector('.app-task-list__tag'));
    }

    get timelineItems() {
      return Array.from(this.queryAll<HTMLElement>('app-timeline-item'));
    }

    get headingInfo() {
      return this.queryAll<HTMLParagraphElement>('.govuk-heading-xl p');
    }
  }

  const mockActivatedRouteParams = {
    taskId: mockTaskState.requestTaskId,
  };

  const createComponent = () => {
    fixture = TestBed.createComponent(ReviewComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  };

  beforeEach(async () => {
    route = new ActivatedRouteStub(mockActivatedRouteParams, null, null, null, null);
    requestActionsService = {
      getRequestActionsByRequestIdUsingGET: jest.fn().mockReturnValue(
        of([
          [
            {
              id: 1,
              type: 'PERMIT_SURRENDER_APPLICATION_SUBMITTED',
              creationDate: '2022-01-22T13:34:52.944920Z',
            },
          ],
        ]),
      ),
    };

    await TestBed.configureTestingModule({
      declarations: [ReviewComponent, ReviewSectionStatusPipe],
      imports: [RouterTestingModule, SharedModule, SharedPermitSurrenderModule],
      providers: [
        { provide: RequestActionsService, useValue: requestActionsService },
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(PermitSurrenderStore);
    store.setState(mockTaskState);
  });

  afterEach(() => jest.clearAllMocks());

  it('should create', () => {
    createComponent();
    expect(component).toBeTruthy();
  });

  it('should display review sections', () => {
    createComponent();
    expect(page.reviewSectionLinks.map((section) => section.textContent.trim())).toEqual([
      'Review surrender request',
      'Determination',
    ]);

    expect(page.reviewSectionStatuses.map((section) => section.textContent.trim())).toEqual(['accepted', 'granted']);
  });

  it('should display statuses when decided rejected', () => {
    store.setState({
      ...store.getState(),
      reviewDecision: {
        type: 'REJECTED',
        notes: 'notes',
      },
      reviewDetermination: {
        type: 'REJECTED',
        reason: '',
      } as any,
    });
    createComponent();
    expect(page.reviewSectionStatuses.map((section) => section.textContent.trim())).toEqual(['rejected', 'rejected']);
  });

  it('should display timeline items', () => {
    createComponent();
    expect(page.timelineItems.length).toEqual(1);
  });

  it('should display related action links', () => {
    expect(page.relatedActionsLinks.map((el) => [el.href, el.textContent])).toEqual([
      ['http://localhost/tasks/237/change-assignee', 'Reassign task']
    ]);
  });

  it('should show heading info', () => {
    expect(page.headingInfo.map((el) => el.textContent.trim())).toEqual([
      'Assigned to: John Doe', 'Days Remaining: 58'
    ]);
  });
});
