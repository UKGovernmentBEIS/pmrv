import { Component, Input } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { Observable, of } from 'rxjs';

import { PermitSurrenderReviewDeterminationGrant, TasksService } from 'pmrv-api';

import { ActivatedRouteStub, MockType } from '../../../../../../testing';
import { SharedModule } from '../../../../../shared/shared.module';
import { PermitSurrenderStore } from '../../../../store/permit-surrender.store';
import { mockTaskState } from '../../../../testing/mock-state';
import { SummaryComponent } from './summary.component';

describe('SummaryComponent', () => {
  @Component({
    selector: 'app-grant-determination-summary-details',
    template: '<p>Mock grant details</p>',
  })
  class MockGrantSummaryDetailsComponent {
    @Input() grantDetermination$: Observable<PermitSurrenderReviewDeterminationGrant>;
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
      declarations: [SummaryComponent, MockGrantSummaryDetailsComponent],
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
    store.setState(mockTaskState);
    createComponent();
    expect(component).toBeTruthy();
  });

  it('show grant details', () => {
    store.setState(mockTaskState);
    createComponent();
    expect(hostElement.innerHTML).toContain('Mock grant details');
  });
});
