import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, MockType } from '../../../../testing';
import { SharedModule } from '../../../shared/shared.module';
import { PermitSurrenderStore } from '../../store/permit-surrender.store';
import { mockTaskState } from '../../testing/mock-state';
import { DeterminationComponent } from './determination.component';

describe('DeterminationComponent', () => {
  let component: DeterminationComponent;
  let fixture: ComponentFixture<DeterminationComponent>;
  let store: PermitSurrenderStore;
  let page: Page;
  let router: Router;

  const tasksService: MockType<TasksService> = {
    processRequestTaskActionUsingPOST: jest.fn().mockReturnValue(of(null)),
  };

  const route = new ActivatedRouteStub({ taskId: mockTaskState.requestTaskId }, null, null);

  class Page extends BasePage<DeterminationComponent> {
    get buttons() {
      return this.queryAll<HTMLLIElement>('button');
    }

    get grantButton() {
      return this.buttons.filter((el) => el.innerHTML.trim() === 'Grant')[0];
    }

    get rejectButton() {
      return this.buttons.filter((el) => el.innerHTML.trim() === 'Reject')[0];
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(DeterminationComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [DeterminationComponent],
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

  it('should not show grant button when decision is not accepted', () => {
    store.setState({ ...mockTaskState, reviewDecision: { type: 'REJECTED', notes: 'notes' } });
    createComponent();
    expect(page.grantButton).toBeUndefined();
  });

  it('should show grant button and submit grant action when decision is accepted', () => {
    store.setState({
      ...mockTaskState,
      reviewDecision: { type: 'ACCEPTED', notes: 'notes' },
      reviewDetermination: undefined,
    });
    createComponent();
    const navigateSpy = jest.spyOn(router, 'navigate');
    expect(page.grantButton).toBeTruthy();

    page.grantButton.click();
    fixture.detectChanges();

    expect(navigateSpy).toHaveBeenCalledTimes(1);
    expect(navigateSpy).toHaveBeenCalledWith(['grant/reason'], {
      relativeTo: route,
    });

    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith({
      requestTaskActionType: 'PERMIT_SURRENDER_SAVE_REVIEW_DETERMINATION',
      requestTaskId: mockTaskState.requestTaskId,
      requestTaskActionPayload: {
        payloadType: 'PERMIT_SURRENDER_SAVE_REVIEW_DETERMINATION_PAYLOAD',
        reviewDetermination: { type: 'GRANTED' },
        reviewDeterminationCompleted: false,
      },
    });
  });

  it('should show reject button and submit reject action when decision is rejected', () => {
    store.setState({
      ...mockTaskState,
      reviewDecision: { type: 'REJECTED', notes: 'notes' },
      reviewDetermination: undefined,
    });
    createComponent();
    const navigateSpy = jest.spyOn(router, 'navigate');
    expect(page.rejectButton).toBeTruthy();

    page.rejectButton.click();
    fixture.detectChanges();

    expect(navigateSpy).toHaveBeenCalledTimes(1);
    expect(navigateSpy).toHaveBeenCalledWith(['reject/reason'], {
      relativeTo: route,
    });

    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith({
      requestTaskActionType: 'PERMIT_SURRENDER_SAVE_REVIEW_DETERMINATION',
      requestTaskId: mockTaskState.requestTaskId,
      requestTaskActionPayload: {
        payloadType: 'PERMIT_SURRENDER_SAVE_REVIEW_DETERMINATION_PAYLOAD',
        reviewDetermination: { type: 'REJECTED' },
        reviewDeterminationCompleted: false,
      },
    });
  });
});
