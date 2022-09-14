import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage, MockType } from '@testing';

import { TasksService } from 'pmrv-api';

import { RfiStore } from "../store/rfi.store";
import { WaitComponent } from './wait.component';

describe('WaitComponent', () => {
  let page: Page;
  let component: WaitComponent;
  let fixture: ComponentFixture<WaitComponent>;
  let route: ActivatedRouteStub;
  let tasksService: MockType<TasksService>;
  let store: RfiStore;

  class Page extends BasePage<WaitComponent> {
    get headingInfo() {
      return this.queryAll<HTMLParagraphElement>('.govuk-heading-xl p');
    }
    get relatedActionsLinks() {
      return this.queryAll<HTMLLinkElement>('aside li a');
    }
  }

  beforeEach(async () => {
    tasksService = {
      processRequestTaskActionUsingPOST: jest.fn().mockReturnValue(of({})),
    };
    route = new ActivatedRouteStub({ taskId: '237' });

    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule],
      declarations: [WaitComponent],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();

    await TestBed.configureTestingModule({
      declarations: [WaitComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(RfiStore);
    store.setState({
      ...store.getState(),
      assignable: false,
      daysRemaining: 13,
      assignee: {
        assigneeFullName: 'John Doe',
        assigneeUserId: '100',
      },
      allowedRequestTaskActions: ['RFI_CANCEL']
    });

    fixture = TestBed.createComponent(WaitComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display related action links', () => {
    expect(page.relatedActionsLinks.map((el) => [el.href, el.textContent])).toEqual([
      ['http://localhost/rfi/237/cancel-verify', 'Cancel request']
    ]);
  });

  it('should display heading info', () => {
    expect(page.headingInfo.map((el) => el.textContent.trim())).toEqual([
      'Assigned to: John Doe', 'Days Remaining: 13'
    ]);
  });
});
