import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage, MockType } from '@testing';

import { RequestActionsService } from 'pmrv-api';

import { SharedPermitSurrenderModule } from '../../shared/shared-permit-surrender.module';
import { PermitSurrenderStore } from '../../store/permit-surrender.store';
import { mockTaskState } from '../../testing/mock-state';
import { WaitReviewComponent } from './wait-review.component';

describe('WaitReviewComponent', () => {
  let component: WaitReviewComponent;
  let fixture: ComponentFixture<WaitReviewComponent>;
  let hostElement: HTMLElement;
  let store: PermitSurrenderStore;
  let page: Page;
  
  let requestActionsService: MockType<RequestActionsService>;
  let route: ActivatedRouteStub;

  class Page extends BasePage<WaitReviewComponent> {
    get relatedActionsLinks() {
      return this.queryAll<HTMLLinkElement>('aside li a');
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
    fixture = TestBed.createComponent(WaitReviewComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    hostElement = fixture.nativeElement;
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
      declarations: [WaitReviewComponent],
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

  it('should show info', () => {
    createComponent();
    expect(hostElement.textContent).toContain('Waiting for the regulator to make a determination');
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
