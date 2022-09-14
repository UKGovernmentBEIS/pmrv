import { HttpResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { RequestTaskAttachmentsHandlingService, TasksService } from 'pmrv-api';

import { ActivatedRouteStub, asyncData, BasePage, mockClass, MockType } from '../../../../testing';
import { PermitSurrenderModule } from '../../permit-surrender.module';
import { PermitSurrenderStore } from '../../store/permit-surrender.store';
import { mockTaskState } from '../../testing/mock-state';
import { UploadDocumentsComponent } from './upload-documents.component';

describe('UploadDocumentsComponent', () => {
  let component: UploadDocumentsComponent;
  let fixture: ComponentFixture<UploadDocumentsComponent>;
  let store: PermitSurrenderStore;
  let page: Page;

  const tasksService: MockType<TasksService> = {
    processRequestTaskActionUsingPOST: jest.fn().mockReturnValue(of(null)),
  };

  const attachmentService = mockClass(RequestTaskAttachmentsHandlingService);

  class Page extends BasePage<UploadDocumentsComponent> {
    set fileValue(value: File[]) {
      this.setInputValue('input[type="file"]', value);
    }

    get filesText() {
      return this.queryAll<HTMLSpanElement>('.moj-multi-file-upload__filename');
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }

    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get errorSummaryErrorList() {
      return Array.from(this.query<HTMLDivElement>('.govuk-error-summary').querySelectorAll('a')).map((anchor) =>
        anchor.textContent.trim(),
      );
    }
  }

  const route = new ActivatedRouteStub({ taskId: mockTaskState.requestTaskId }, null, {
    statusKey: 'SURRENDER_APPLY',
  });

  const createComponent = () => {
    fixture = TestBed.createComponent(UploadDocumentsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, PermitSurrenderModule],
      providers: [
        { provide: TasksService, useValue: tasksService },
        { provide: RequestTaskAttachmentsHandlingService, useValue: attachmentService },
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
        documentsExist: true,
        documents: [],
      },
      sectionsCompleted: {},
    });
  });

  it('should create', () => {
    createComponent();
    expect(component).toBeTruthy();
  });

  it('should upload files', async () => {
    createComponent();
    attachmentService.uploadRequestTaskAttachmentUsingPOST.mockReturnValue(
      asyncData<any>(new HttpResponse({ body: { uuid: 'e227ea8a-778b-4208-9545-e108ea66c114' } })),
    );

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeTruthy();
    expect(page.errorSummaryErrorList).toEqual(['Select a file']);

    page.fileValue = [new File(['file'], 'file.txt')];
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
          documents: ['e227ea8a-778b-4208-9545-e108ea66c114'],
        },
        sectionsCompleted: {
          SURRENDER_APPLY: false,
        },
      },
    });
  });
});
