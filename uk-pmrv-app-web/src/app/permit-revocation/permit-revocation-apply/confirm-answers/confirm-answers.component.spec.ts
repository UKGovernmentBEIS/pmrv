import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { CessationModule } from '@permit-revocation/cessation/cessation.module';
import { PermitRevocationModule } from '@permit-revocation/permit-revocation.module';
import { PermitRevocationStore } from '@permit-revocation/store/permit-revocation-store';
import { mockTaskState } from '@permit-revocation/testing/mock-state';
import moment from 'moment';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, MockType } from '../../../../testing';
import { ConfirmAnswersComponent } from './confirm-answers.component';

describe('ConfirmAnswersComponent', () => {
  let component: ConfirmAnswersComponent;
  let fixture: ComponentFixture<ConfirmAnswersComponent>;
  let store: PermitRevocationStore;
  let router: Router;
  let page: Page;

  class Page extends BasePage<ConfirmAnswersComponent> {
    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="button"]');
    }
  }

  const route = new ActivatedRouteStub({ taskId: mockTaskState.requestTaskId }, null, {
    statusKey: 'REVOCATION_APPLY',
    keys: ['feeDate', 'effectiveDate'],
    skipValidators: true,
  });

  const tasksService: MockType<TasksService> = {
    processRequestTaskActionUsingPOST: jest.fn().mockReturnValue(of(null)),
  };

  const createComponent = () => {
    fixture = TestBed.createComponent(ConfirmAnswersComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, PermitRevocationModule, CessationModule],
      providers: [
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();
    store = TestBed.inject(PermitRevocationStore);
    router = TestBed.inject(Router);
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ConfirmAnswersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  afterEach(() => jest.clearAllMocks());

  beforeEach(() => {
    store.setState({
      ...store.getState(),
      requestTaskId: mockTaskState.requestTaskId,
      isEditable: true,
      permitRevocation: {
        reason: 'Because i have to',
        activitiesStopped: true,
        stoppedDate: '2022-04-14',
        effectiveDate: moment().add(29, 'd').utc(true).format('YYYY-MM-DD'),
        surrenderRequired: false,
        annualEmissionsReportRequired: true,
        annualEmissionsReportDate: '2022-04-14',
        feeCharged: false,
      },
      sectionsCompleted: { REVOCATION_APPLY: false },
    });
  });

  beforeEach(createComponent);

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should submit', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    page.submitButton.click();
    fixture.detectChanges();
    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith({
      requestTaskActionType: 'PERMIT_REVOCATION_SAVE_APPLICATION',
      requestTaskId: mockTaskState.requestTaskId,
      requestTaskActionPayload: {
        payloadType: 'PERMIT_REVOCATION_SAVE_APPLICATION_PAYLOAD',
        permitRevocation: {
          reason: 'Because i have to',
          activitiesStopped: true,
          stoppedDate: '2022-04-14',
          effectiveDate: moment().add(29, 'd').utc(true).format('YYYY-MM-DD'),
          surrenderRequired: false,
          annualEmissionsReportRequired: true,
          annualEmissionsReportDate: '2022-04-14',
          feeCharged: false,
        },
        sectionsCompleted: {
          REVOCATION_APPLY: true,
        },
      },
    });
    expect(navigateSpy).toHaveBeenCalledWith(['..', 'summary'], { relativeTo: route, state: { notification: true } });
  });
});
