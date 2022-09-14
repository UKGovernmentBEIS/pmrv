import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, MockType } from '../../../testing';
import { PermitSurrenderModule } from '../permit-surrender.module';
import { PermitSurrenderState } from '../store/permit-surrender.state';
import { PermitSurrenderStore } from '../store/permit-surrender.store';
import { mockTaskState } from '../testing/mock-state';
import { SubmitComponent } from './submit.component';

describe('SubmitComponent', () => {
  let component: SubmitComponent;
  let fixture: ComponentFixture<SubmitComponent>;
  let store: PermitSurrenderStore;
  let page: Page;

  const tasksService: MockType<TasksService> = {
    processRequestTaskActionUsingPOST: jest.fn().mockReturnValue(of(null)),
  };

  class Page extends BasePage<SubmitComponent> {
    get paragraphInfo() {
      return this.queryAll<HTMLParagraphElement>('p.govuk-body');
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('.govuk-button');
    }
  }

  const route = new ActivatedRouteStub({ taskId: mockTaskState.requestTaskId }, null, {
    statusKey: 'SURRENDER_SUBMIT',
  });

  const initialState: PermitSurrenderState = {
    ...mockTaskState,
    requestTaskId: mockTaskState.requestTaskId,
    isEditable: true,
    permitSurrender: {
      stopDate: '2012-12-21',
      justification: 'justify',
      documentsExist: true,
      documents: ['e227ea8a-778b-4208-9545-e108ea66c114'],
    },
    permitSurrenderAttachments: { 'e227ea8a-778b-4208-9545-e108ea66c114': 'hello.txt' },
    sectionsCompleted: { SURRENDER_APPLY: false },
    competentAuthority: 'ENGLAND',
  };

  const createComponent = () => {
    fixture = TestBed.createComponent(SubmitComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, PermitSurrenderModule],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();

    store = TestBed.inject(PermitSurrenderStore);
  });

  afterEach(() => jest.clearAllMocks());

  beforeEach(() => {
    store.setState(initialState);
  });

  it('should create', () => {
    createComponent();
    expect(component).toBeTruthy();
  });

  it('should not allow to submit when apply in progress', () => {
    createComponent();
    expect(page.submitButton).toBeNull();
    expect(page.paragraphInfo[0].textContent).toEqual(
      'All tasks must be completed before you can submit your application.',
    );
  });

  it('should allow to submit when apply is complete', () => {
    store.setState({
      ...store.getState(),
      sectionsCompleted: { SURRENDER_APPLY: true },
    });

    createComponent();
    expect(page.submitButton).toBeTruthy();
    expect(page.paragraphInfo[0].textContent).toEqual('Now send your application');
  });

  it('should submit when submit button is clicked', () => {
    store.setState({
      ...store.getState(),
      sectionsCompleted: { SURRENDER_APPLY: true },
    });

    createComponent();

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.paragraphInfo.map((p) => p.textContent)).toEqual([
      'We’ve sent your application to Environment Agency',
      'The regulator will make a decision and respond within 2 calendar months.',
    ]);

    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith({
      requestTaskActionType: 'PERMIT_SURRENDER_SUBMIT_APPLICATION',
      requestTaskId: mockTaskState.requestTaskId,
      requestTaskActionPayload: {
        payloadType: 'EMPTY_PAYLOAD',
      },
    });
  });
});
