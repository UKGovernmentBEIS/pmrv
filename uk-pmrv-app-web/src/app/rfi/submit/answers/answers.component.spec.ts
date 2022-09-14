import { HttpErrorResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of, throwError } from 'rxjs';

import { RequestTaskActionPayload, TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../../testing';
import { RfiModule } from '../../rfi.module';
import { RfiStore } from '../../store/rfi.store';
import { AnswersComponent } from './answers.component';

describe('AnswersComponent', () => {
  let page: Page;
  let store: RfiStore;
  let component: AnswersComponent;
  let hostElement: HTMLElement;
  let fixture: ComponentFixture<AnswersComponent>;
  let router: Router;

  const tasksService = mockClass(TasksService);

  const activatedRouteStub = new ActivatedRouteStub({ taskId: '279' });

  class Page extends BasePage<AnswersComponent> {
    get answers() {
      return this.queryAll('dd').map((row) => row.textContent.trim());
    }

    get confirmButton() {
      return this.query<HTMLButtonElement>('.govuk-button');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RfiModule, RouterTestingModule],
      providers: [
        { provide: ActivatedRoute, useValue: activatedRouteStub },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(RfiStore);
    router = TestBed.inject(Router);
    store.setState({
      ...store.getState(),
      rfiSubmitPayload: {
        questions: ['who', 'what'],
        deadline: '2023-01-01',
        operators: ['operator'],
        signatory: 'signatory',
        files: [],
      },
      usersInfo: {
        operator: { name: 'operator' },
        signatory: { name: 'signatory' },
      },
    });
    fixture = TestBed.createComponent(AnswersComponent);
    component = fixture.componentInstance;
    hostElement = fixture.nativeElement;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the answers', () => {
    expect(page.answers).toEqual(['who', 'what', '1 Jan 2023', 'operator', 'signatory']);
  });

  it('should submit task status', () => {
    tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
    const navigateSpy = jest.spyOn(router, 'navigate');

    page.confirmButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith({
      requestTaskActionType: 'RFI_SUBMIT',
      requestTaskId: 279,
      requestTaskActionPayload: {
        payloadType: 'RFI_SUBMIT_PAYLOAD',
        rfiSubmitPayload: {
          questions: ['who', 'what'],
          files: [],
          deadline: '2023-01-01',
          operators: ['operator'],
          signatory: 'signatory',
        },
      } as RequestTaskActionPayload,
    });
    expect(navigateSpy).toHaveBeenCalledWith(['../submit-confirmation'], {
      relativeTo: TestBed.inject(ActivatedRoute),
    });
  });

  it('should display error message when template notification error returns from api', () => {
    jest
      .spyOn(tasksService, 'processRequestTaskActionUsingPOST')
      .mockReturnValue(
        throwError(
          () => new HttpErrorResponse({ error: { code: 'NOTIF1002', data: ['doctemplate.docx'] }, status: 400 }),
        ),
      );

    page.confirmButton.click();
    fixture.detectChanges();

    expect(hostElement.querySelector('h2').textContent.trim()).toEqual(
      'Sorry, there was a problem when evaluating the document template: doctemplate.docx',
    );
  });
});
