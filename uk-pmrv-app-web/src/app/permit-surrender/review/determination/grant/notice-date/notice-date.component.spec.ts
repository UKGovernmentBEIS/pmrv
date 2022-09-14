import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, MockType } from '../../../../../../testing';
import { SharedModule } from '../../../../../shared/shared.module';
import { PermitSurrenderStore } from '../../../../store/permit-surrender.store';
import { mockTaskState } from '../../../../testing/mock-state';
import { NoticeDateComponent } from './notice-date.component';

describe('NoticeDateComponent', () => {
  let component: NoticeDateComponent;
  let fixture: ComponentFixture<NoticeDateComponent>;
  let store: PermitSurrenderStore;
  let page: Page;
  let router: Router;

  const tasksService: MockType<TasksService> = {
    processRequestTaskActionUsingPOST: jest.fn().mockReturnValue(of(null)),
  };

  const route = new ActivatedRouteStub({ taskId: mockTaskState.requestTaskId }, null, null);

  class Page extends BasePage<NoticeDateComponent> {
    get noticeDateDay() {
      return this.getInputValue('#noticeDate-day');
    }
    set noticeDateDay(value: string) {
      this.setInputValue('#noticeDate-day', value);
    }

    get noticeDateMonth() {
      return this.getInputValue('#noticeDate-month');
    }
    set noticeDateMonth(value: string) {
      this.setInputValue('#noticeDate-month', value);
    }

    get noticeDateYear() {
      return this.getInputValue('#noticeDate-year');
    }
    set noticeDateYear(value: string) {
      this.setInputValue('#noticeDate-year', value);
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

  const createComponent = () => {
    fixture = TestBed.createComponent(NoticeDateComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [NoticeDateComponent],
      imports: [RouterTestingModule, SharedModule],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(PermitSurrenderStore);
  });

  afterEach(() => jest.clearAllMocks());

  it('should create', () => {
    store.setState(mockTaskState);
    createComponent();
    expect(component).toBeTruthy();
  });

  it('should render form when stop date exists', () => {
    store.setState(mockTaskState);

    createComponent();
    fixture.detectChanges();

    expect(page.noticeDateDay.trim()).toEqual('13');
    expect(page.noticeDateMonth.trim()).toEqual('12');
    expect(page.noticeDateYear.trim()).toEqual('2030');
  });

  it('should validate date upon submitting', () => {
    store.setState(mockTaskState);
    createComponent();

    const tommorow = new Date(new Date().setDate(new Date().getDate() + 1));
    page.noticeDateDay = `${tommorow.getDate()}`;
    page.noticeDateMonth = `${tommorow.getMonth() + 1}`;
    page.noticeDateYear = `${tommorow.getFullYear()}`;

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeTruthy();
    expect(tasksService.processRequestTaskActionUsingPOST).not.toHaveBeenCalled();
  });

  it('should submit upon valid date', () => {
    store.setState(mockTaskState);
    createComponent();

    const navigateSpy = jest.spyOn(router, 'navigate');

    page.noticeDateDay = '24';
    page.noticeDateMonth = '12';
    page.noticeDateYear = `2030`;

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeFalsy();
    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);

    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith({
      requestTaskActionType: 'PERMIT_SURRENDER_SAVE_REVIEW_DETERMINATION',
      requestTaskId: mockTaskState.requestTaskId,
      requestTaskActionPayload: {
        payloadType: 'PERMIT_SURRENDER_SAVE_REVIEW_DETERMINATION_PAYLOAD',
        reviewDetermination: {
          ...mockTaskState.reviewDetermination,
          noticeDate: new Date('2030-12-24'),
        },
        reviewDeterminationCompleted: false,
      },
    });

    expect(navigateSpy).toHaveBeenCalledWith(['../report'], { relativeTo: route });
  });
});
