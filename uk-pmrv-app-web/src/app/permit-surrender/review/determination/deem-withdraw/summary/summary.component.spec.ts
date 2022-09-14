import { Component, Input } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { Observable, of } from 'rxjs';

import { PermitSurrenderReviewDeterminationDeemWithdraw, TasksService } from 'pmrv-api';

import { ActivatedRouteStub, MockType } from '../../../../../../testing';
import { SharedModule } from '../../../../../shared/shared.module';
import { PermitSurrenderStore } from '../../../../store/permit-surrender.store';
import { mockTaskState } from '../../../../testing/mock-state';
import { SummaryComponent } from './summary.component';

describe('SummaryComponent', () => {
  @Component({
    selector: 'app-deem-withdraw-determination-summary-details',
    template: '<p>Mock deem details</p>',
  })
  class MockDeemWithdrawSummaryDetailsComponent {
    @Input() deemWithdrawDetermination$: Observable<PermitSurrenderReviewDeterminationDeemWithdraw>;
  }

  let component: SummaryComponent;
  let fixture: ComponentFixture<SummaryComponent>;
  let hostElement: HTMLElement;

  let store: PermitSurrenderStore;
  const tasksService: MockType<TasksService> = {
    processRequestTaskActionUsingPOST: jest.fn().mockReturnValue(of(null)),
  };

  const route = new ActivatedRouteStub({ taskId: mockTaskState.requestTaskId }, null, null);

  const createComponent = () => {
    fixture = TestBed.createComponent(SummaryComponent);
    component = fixture.componentInstance;
    hostElement = fixture.nativeElement;
    fixture.detectChanges();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SummaryComponent, MockDeemWithdrawSummaryDetailsComponent],
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

  it('should create', () => {
    store.setState({
      ...mockTaskState,
      reviewDetermination: {
        type: 'DEEMED_WITHDRAWN',
        reason: 'deemed reason',
      },
    });
    createComponent();
    expect(component).toBeTruthy();
  });

  it('show deem withdraw details', () => {
    store.setState({
      ...mockTaskState,
      reviewDetermination: {
        type: 'DEEMED_WITHDRAWN',
        reason: 'deemed reason',
      },
    });
    createComponent();
    expect(hostElement.innerHTML).toContain('Mock deem details');
  });
});
