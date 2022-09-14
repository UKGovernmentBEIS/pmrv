import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { ActivatedRouteStub, BasePage } from '@testing';

import { PermitSurrenderModule } from '../permit-surrender.module';
import { PermitSurrenderStore } from '../store/permit-surrender.store';
import { mockTaskState } from '../testing/mock-state';
import { PermitSurrenderTaskListComponent } from './permit-surrender-task-list.component';

describe('PermitSurrenderTaskListComponent', () => {
  let component: PermitSurrenderTaskListComponent;
  let fixture: ComponentFixture<PermitSurrenderTaskListComponent>;
  let store: PermitSurrenderStore;
  let page: Page;
  let route: ActivatedRouteStub;

  class Page extends BasePage<PermitSurrenderTaskListComponent> {
    get relatedActionsLinks() {
      return this.queryAll<HTMLLinkElement>('aside li a');
    }

    get sections(): HTMLUListElement[] {
      return Array.from(this.queryAll<HTMLUListElement>('ul > li.app-task-list__item'));
    }

    get sectionAnchorNames(): HTMLAnchorElement[] {
      return this.sections.map((section) => section.querySelector<HTMLAnchorElement>('a'));
    }

    get sectionStatuses(): HTMLElement[] {
      return this.sections.map((section) => section.querySelector<HTMLElement>('.app-task-list__tag'));
    }

    get headingInfo() {
      return this.queryAll<HTMLParagraphElement>('.govuk-heading-xl p');
    }
  }

  const mockActivatedRouteParams = {
    taskId: mockTaskState.requestTaskId,
  };

  beforeEach(async () => {
    route = new ActivatedRouteStub(mockActivatedRouteParams, null, null, null, null);
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, PermitSurrenderModule],
      providers: [{ provide: ActivatedRoute, useValue: route }],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(PermitSurrenderStore);
    store.setState(mockTaskState);
  });

  afterEach(() => jest.clearAllMocks());

  beforeEach(() => {
    fixture = TestBed.createComponent(PermitSurrenderTaskListComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the sections when surrender is not initiated yet', () => {
    store.setState({
      ...store.getState(),
      permitSurrender: null,
      sectionsCompleted: {},
    });
    fixture.detectChanges();

    expect(page.sectionAnchorNames.map((section) => section.textContent)).toEqual([
      'Surrender permit request',
      'Submit application',
    ]);

    expect(page.sectionStatuses.map((section) => section.textContent.trim())).toEqual([
      'not started',
      'cannot start yet',
    ]);
  });

  it('should render the sections when surrender apply action is in progress', () => {
    store.setState({
      ...store.getState(),
      permitSurrender: {
        stopDate: '2012-12-12',
        justification: 'justify',
        documentsExist: undefined,
        documents: [],
      },
      sectionsCompleted: {
        SURRENDER_APPLY: false,
      },
    });
    fixture.detectChanges();

    expect(page.sectionStatuses.map((section) => section.textContent.trim())).toEqual([
      'in progress',
      'cannot start yet',
    ]);
  });

  it('should render the sections when surrender apply action is completed', () => {
    store.setState({
      ...store.getState(),
      permitSurrender: {
        stopDate: '2012-12-12',
        justification: 'justify',
        documentsExist: true,
        documents: ['2c30c8bf-3d5e-474d-98a0-123a87eb60dd'],
      },
      sectionsCompleted: {
        SURRENDER_APPLY: true,
      },
    });
    fixture.detectChanges();

    expect(page.sectionStatuses.map((section) => section.textContent.trim())).toEqual(['completed', 'not started']);
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
