import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage, MockType } from '@testing';

import { RequestActionsService } from 'pmrv-api';

import { SharedPermitSurrenderModule } from '../shared/shared-permit-surrender.module';
import { PermitSurrenderStore } from '../store/permit-surrender.store';
import { CessationComponent } from './cessation.component';
import { mockTaskState } from './testing/mock-state';

describe('CessationComponent', () => {
  let component: CessationComponent;
  let fixture: ComponentFixture<CessationComponent>;
  let store: PermitSurrenderStore;
  let page: Page;

  let requestActionsService: MockType<RequestActionsService>;
  let route: ActivatedRouteStub;

  class Page extends BasePage<CessationComponent> {
    get relatedActionsLinks() {
      return this.queryAll<HTMLLinkElement>('aside li a');
    }
    
    get sections() {
      return Array.from(this.queryAll<HTMLLIElement>('li[app-task-item]'));
    }

    get sectionLinks() {
      return this.sections.map((section) => section.querySelector('a'));
    }

    get sectionStatuses() {
      return this.sections.map((section) => section.querySelector('.app-task-list__tag'));
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
    fixture = TestBed.createComponent(CessationComponent);
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
      declarations: [CessationComponent],
      imports: [RouterTestingModule, SharedModule, SharedPermitSurrenderModule],
      providers: [
        { provide: RequestActionsService, useValue: requestActionsService },
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(PermitSurrenderStore);
  });

  afterEach(() => jest.clearAllMocks());

  it('should create', () => {
    store.setState(mockTaskState);
    createComponent();
    expect(component).toBeTruthy();
  });

  it('should display not started section when cessation not started yet', () => {
    store.setState({ ...mockTaskState, cessation: null, cessationCompleted: undefined });
    createComponent();
    expect(page.sectionLinks.map((section) => section.textContent.trim())).toEqual([
      'Confirm cessation of regulated activities',
    ]);

    expect(page.sectionStatuses.map((section) => section.textContent.trim())).toEqual(['not started']);
  });

  it('should display in progress section when cessation exists but not completed', () => {
    store.setState({ ...mockTaskState, cessationCompleted: false });
    createComponent();
    expect(page.sectionLinks.map((section) => section.textContent.trim())).toEqual([
      'Confirm cessation of regulated activities',
    ]);

    expect(page.sectionStatuses.map((section) => section.textContent.trim())).toEqual(['in progress']);
  });

  it('should display completed section when cessation exists and completed', () => {
    store.setState({ ...mockTaskState, cessationCompleted: true });
    createComponent();
    expect(page.sectionLinks.map((section) => section.textContent.trim())).toEqual([
      'Confirm cessation of regulated activities',
    ]);

    expect(page.sectionStatuses.map((section) => section.textContent.trim())).toEqual(['completed']);
  });

  it('should display timeline items', () => {
    store.setState(mockTaskState);
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
      'Assigned to: John Doe',
    ]);
  });
});
