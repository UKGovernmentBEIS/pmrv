import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../../testing';
import { RdeModule } from '../../rde.module';
import { RdeStore } from '../../store/rde.store';
import { AnswersTemplateComponent } from './answers-template.component';

describe('AnswersTemplateComponent', () => {
  let page: Page;
  let store: RdeStore;
  let component: AnswersTemplateComponent;
  let fixture: ComponentFixture<AnswersTemplateComponent>;

  const tasksService = mockClass(TasksService);

  const activatedRouteStub = new ActivatedRouteStub({ taskId: '279' });

  class Page extends BasePage<AnswersTemplateComponent> {
    get answers() {
      return this.queryAll('dd').map((row) => row.textContent.trim());
    }

    get confirmButton() {
      return this.query<HTMLButtonElement>('.govuk-button');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RdeModule, RouterTestingModule],
      providers: [
        { provide: ActivatedRoute, useValue: activatedRouteStub },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(RdeStore);
    store.setState({
      ...store.getState(),
      rdePayload: {
        ...store.getState().rdePayload,
        extensionDate: '2026-10-10',
        deadline: '2026-10-08',
        operators: ['user1'],
        signatory: 'user2',
      },
      usersInfo: {
        user1: {
          name: 'User1',
          roleCode: 'operator',
          contactTypes: ['PRIMARY'],
        },
        user2: {
          name: 'User2',
        },
      },
    });

    fixture = TestBed.createComponent(AnswersTemplateComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the answers', () => {
    expect(page.answers).toEqual(['10 Oct 2026', '8 Oct 2026', 'User1, Operator - Primary contact', 'User2']);
  });
});
