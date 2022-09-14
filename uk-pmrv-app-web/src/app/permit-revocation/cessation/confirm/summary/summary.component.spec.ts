import { Component, Input } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { PermitCessation, TasksService } from 'pmrv-api';

import { ActivatedRouteStub, MockType } from '../../../../../testing';
import { SharedModule } from '../../../../shared/shared.module';
import { PermitRevocationStore } from '../../../store/permit-revocation-store';
import { mockTaskState } from '../testing/mock-state';
import { SummaryComponent } from './summary.component';

describe('SummaryComponent', () => {
  @Component({
    selector: 'app-revocation-cessation-summary-details',
    template: '<p>Mock cessation summary details</p>',
  })
  class MockCessationSummaryDetailsComponent {
    @Input() cessation: PermitCessation;
    @Input() allowancesSurrenderRequired: boolean;
    @Input() isEditable: boolean;
  }

  let component: SummaryComponent;
  let fixture: ComponentFixture<SummaryComponent>;
  let hostElement: HTMLElement;

  let store: PermitRevocationStore;
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
      declarations: [SummaryComponent, MockCessationSummaryDetailsComponent],
      imports: [RouterTestingModule, SharedModule],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(PermitRevocationStore);
  });

  it('should create', () => {
    store.setState(mockTaskState);
    createComponent();
    expect(component).toBeTruthy();
  });

  it('show grant details', () => {
    store.setState(mockTaskState);
    createComponent();
    expect(hostElement.innerHTML).toContain('Mock cessation summary details');
  });
});
