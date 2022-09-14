import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { RequestTaskActionPayload, TasksService } from 'pmrv-api';

import { RdeModule } from '../rde.module';
import { RdeStore } from "../store/rde.store";
import { ForceDecisionComponent } from './force-decision.component';

describe('ForceDecisionComponent', () => {
  let page: Page;
  let component: ForceDecisionComponent;
  let fixture: ComponentFixture<ForceDecisionComponent>;
  let router: Router;
  let store: RdeStore;

  const tasksService = mockClass(TasksService);

  const activatedRouteStub = new ActivatedRouteStub({ taskId: '279' });

  class Page extends BasePage<ForceDecisionComponent> {
    get answers() {
      return this.queryAll<HTMLInputElement>('textarea[id^="pairs"][id$="response"]').map((option) =>
        this.getInputValue(option),
      );
    }

    get headingInfo() {
      return this.queryAll<HTMLParagraphElement>('.govuk-heading-xl p');
    }
    get confirmButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RdeModule, RouterTestingModule, SharedModule],
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
      daysRemaining: 13,
      assignee: {
        assigneeFullName: 'John Doe',
        assigneeUserId: '100',
      },
    });

    router = TestBed.inject(Router);

    fixture = TestBed.createComponent(ForceDecisionComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should submit task', () => {
    tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
    const navigateSpy = jest.spyOn(router, 'navigate');

    component.form.get('decision').setValue('REJECTED');
    component.form.get('evidence').setValue('no evidence');

    page.confirmButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith({
      requestTaskActionType: 'RDE_FORCE_DECISION',
      requestTaskId: 279,
      requestTaskActionPayload: {
        payloadType: 'RDE_FORCE_DECISION_PAYLOAD',
        rdeForceDecisionPayload: {
          decision: 'REJECTED',
          evidence: 'no evidence',
          files: [],
        },
      } as RequestTaskActionPayload,
    });
    expect(navigateSpy).toHaveBeenCalledWith(['../manual-approval-confirmation'], {
      relativeTo: TestBed.inject(ActivatedRoute),
      state: {
        decision: 'REJECTED',
      },
    });
  });

  it('should display heading info', () => {
    expect(page.headingInfo.map((el) => el.textContent.trim())).toEqual([
      'Assigned to: John Doe', 'Days Remaining: 13'
    ]);
  });
});
