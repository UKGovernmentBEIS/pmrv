import { HttpErrorResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of, throwError } from 'rxjs';

import { RequestTaskActionPayload, TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../../testing';
import { RdeModule } from '../../rde.module';
import { RdeStore } from '../../store/rde.store';
import { AnswersComponent } from './answers.component';

describe('AnswersComponent', () => {
  let page: Page;
  let store: RdeStore;
  let component: AnswersComponent;
  let fixture: ComponentFixture<AnswersComponent>;
  let hostElement: HTMLElement;
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
      imports: [RdeModule, RouterTestingModule],
      providers: [
        { provide: ActivatedRoute, useValue: activatedRouteStub },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(RdeStore);
    router = TestBed.inject(Router);

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

  it('should submit task status', () => {
    tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
    const navigateSpy = jest.spyOn(router, 'navigate');

    page.confirmButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith({
      requestTaskActionType: 'RDE_SUBMIT',
      requestTaskId: 279,
      requestTaskActionPayload: {
        payloadType: 'RDE_SUBMIT_PAYLOAD',
        rdePayload: {
          extensionDate: '2026-10-10',
          deadline: '2026-10-08',
          operators: ['user1'],
          signatory: 'user2',
        },
      } as RequestTaskActionPayload,
    });
    expect(navigateSpy).toHaveBeenCalledWith(['../confirmation'], {
      relativeTo: TestBed.inject(ActivatedRoute),
    });
  });

  it('should display error message when template notification error happens', () => {
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
