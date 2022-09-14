import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, MockType } from '../../../../testing';
import { PermitSurrenderModule } from '../../permit-surrender.module';
import { PermitSurrenderStore } from '../../store/permit-surrender.store';
import { mockTaskState } from '../../testing/mock-state';
import { SupportDocumentsComponent } from './support-documents.component';

describe('SupportDocumentsComponent', () => {
  let component: SupportDocumentsComponent;
  let fixture: ComponentFixture<SupportDocumentsComponent>;
  let store: PermitSurrenderStore;
  let page: Page;

  const tasksService: MockType<TasksService> = {
    processRequestTaskActionUsingPOST: jest.fn().mockReturnValue(of(null)),
  };

  class Page extends BasePage<SupportDocumentsComponent> {
    get documentsExistRadios() {
      return this.queryAll<HTMLInputElement>('input[name$="documentsExist"]');
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
    fixture = TestBed.createComponent(SupportDocumentsComponent);
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
        justification: 'justify',
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

  it('should render form when surrender exists', () => {
    store.setState({
      ...store.getState(),
      isEditable: true,
      permitSurrender: {
        stopDate: '2012-12-21',
        justification: 'justify',
        documentsExist: false,
        documents: [],
      },
      sectionsCompleted: {
        SURRENDER_APPLY: true,
      },
    });

    createComponent();

    expect(page.documentsExistRadios[0].checked).toBeFalsy();
    expect(page.documentsExistRadios[1].checked).toBeTruthy();
  });

  it('should validate upon submitting', () => {
    createComponent();

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeTruthy();
    expect(page.errors.map((error) => error.textContent.trim())).toEqual(['Select Yes or No']);
    expect(tasksService.processRequestTaskActionUsingPOST).not.toHaveBeenCalled();
  });

  it('should submit form', () => {
    createComponent();

    page.documentsExistRadios[0].click();
    fixture.detectChanges();

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
          documentsExist: true,
          documents: [],
        },
        sectionsCompleted: {
          SURRENDER_APPLY: false,
        },
      },
    });
  });
});
