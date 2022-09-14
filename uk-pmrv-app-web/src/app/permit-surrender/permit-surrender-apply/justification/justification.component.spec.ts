import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, MockType } from '../../../../testing';
import { PermitSurrenderModule } from '../../permit-surrender.module';
import { PermitSurrenderStore } from '../../store/permit-surrender.store';
import { mockTaskState } from '../../testing/mock-state';
import { JustificationComponent } from './justification.component';

describe('JustificationComponent', () => {
  let component: JustificationComponent;
  let fixture: ComponentFixture<JustificationComponent>;
  let store: PermitSurrenderStore;
  let page: Page;

  const tasksService: MockType<TasksService> = {
    processRequestTaskActionUsingPOST: jest.fn().mockReturnValue(of(null)),
  };

  class Page extends BasePage<JustificationComponent> {
    get justification() {
      return this.getInputValue('#justification');
    }

    set justification(value: string) {
      this.setInputValue('#justification', value);
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }

    get errorSummary() {
      return this.query<HTMLDivElement>('govuk-error-summary');
    }

    get errors() {
      return this.queryAll<HTMLLIElement>('ul.govuk-error-summary__list > li');
    }
  }

  const route = new ActivatedRouteStub({ taskId: mockTaskState.requestTaskId }, null, {
    statusKey: 'SURRENDER_APPLY',
  });

  const createComponent = () => {
    fixture = TestBed.createComponent(JustificationComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, PermitSurrenderModule],
      providers: [
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(PermitSurrenderStore);
  });

  afterEach(() => jest.clearAllMocks());

  beforeEach(() => {
    store.setState({
      ...store.getState(),
      requestTaskId: mockTaskState.requestTaskId,
      isEditable: true,
      permitSurrender: {
        stopDate: '2012-12-21',
        justification: undefined,
        documentsExist: undefined,
        documents: [],
      },
      sectionsCompleted: {},
    });
  });

  it('should create', () => {
    createComponent();
    expect(component).toBeTruthy();
  });

  it('should render empty form when surrender is empty', () => {
    createComponent();
    expect(page.justification).toBeFalsy();
  });

  it('should render form when surrender exists', () => {
    store.setState({
      ...store.getState(),
      isEditable: true,
      permitSurrender: {
        stopDate: '2012-12-21',
        justification: 'justify',
        documentsExist: true,
        documents: ['2c30c8bf-3d5e-474d-98a0-123a87eb60dd'],
      },
      sectionsCompleted: {
        SURRENDER_APPLY: true,
      },
    });

    createComponent();

    expect(page.justification).toEqual('justify');
  });

  it('should validate justification upon submitting', () => {
    createComponent();

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeTruthy();
    expect(page.errors.map((error) => error.textContent.trim())).toEqual([
      'Enter a reason for why activities have stopped',
    ]);
    expect(tasksService.processRequestTaskActionUsingPOST).not.toHaveBeenCalled();
  });

  it('should submit upon valid justification', () => {
    createComponent();

    page.justification = 'justify';

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeFalsy();
    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith({
      requestTaskActionType: 'PERMIT_SURRENDER_SAVE_APPLICATION',
      requestTaskId: mockTaskState.requestTaskId,
      requestTaskActionPayload: {
        payloadType: 'PERMIT_SURRENDER_SAVE_APPLICATION_PAYLOAD',
        permitSurrender: {
          stopDate: '2012-12-21',
          justification: 'justify',
          documentsExist: undefined,
          documents: [],
        },
        sectionsCompleted: {
          SURRENDER_APPLY: false,
        },
      },
    });
  });
});
