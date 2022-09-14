import { Component, Input } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { Observable, of } from 'rxjs';

import { ItemDTO, RequestActionInfoDTO, RequestActionsService, RequestItemsService } from 'pmrv-api';

import { MockType } from '../../../testing';
import { SharedModule } from '../../shared/shared.module';
import { PermitApplicationStore } from '../store/permit-application.store';
import { mockState } from '../testing/mock-state';
import { PermitTaskListContainerComponent } from './permit-task-list-container.component';

describe('PermitTaskListContainerComponent', () => {
  let store: PermitApplicationStore;
  let component: PermitTaskListContainerComponent;
  let fixture: ComponentFixture<PermitTaskListContainerComponent>;
  let hostElement: HTMLElement;

  const requestItemsService: MockType<RequestItemsService> = {
    getItemsByRequestUsingGET: jest.fn().mockReturnValue(
      of({
        items: [
          {
            requestType: 'PERMIT_ISSUANCE',
            taskType: 'PERMIT_ISSUANCE_WAIT_FOR_AMENDS',
            taskId: 100,
          },
          {
            requestType: 'PERMIT_ISSUANCE',
            taskType: 'PERMIT_ISSUANCE_APPLICATION_REVIEW',
            taskId: mockState.requestTaskId,
          },
        ],
        totalItems: 2,
      }),
    ),
  };

  const requestActionsService: MockType<RequestActionsService> = {
    getRequestActionsByRequestIdUsingGET: jest.fn().mockReturnValue(
      of([
        {
          id: 1,
          creationDate: '2020-08-25 10:36:15.189643',
          submitter: 'Operator',
          type: 'INSTALLATION_ACCOUNT_OPENING_APPLICATION_SUBMITTED',
        },
        {
          id: 2,
          creationDate: '2020-08-26 10:36:15.189643',
          submitter: 'Regulator',
          type: 'INSTALLATION_ACCOUNT_OPENING_ACCEPTED',
        },
      ]),
    ),
  };

  @Component({
    selector: 'app-permit-task-list',
    template: `<div id="relatedTasks">{{ (relatedTasks$ | async).length }}</div>
      <div id="actions">{{ (actions$ | async).length }}</div>`,
  })
  class MockPermitTaskListComponent {
    @Input() actions$: Observable<RequestActionInfoDTO[]>;
    @Input() relatedTasks$: Observable<ItemDTO[]>;
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(PermitTaskListContainerComponent);
    component = fixture.componentInstance;
    hostElement = fixture.nativeElement;
    fixture.detectChanges();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [PermitTaskListContainerComponent, MockPermitTaskListComponent],
      imports: [RouterTestingModule, SharedModule],
      providers: [
        { provide: RequestItemsService, useValue: requestItemsService },
        { provide: RequestActionsService, useValue: requestActionsService },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(PermitApplicationStore);
    store.setState({
      ...mockState,
      requestTaskType: 'PERMIT_ISSUANCE_APPLICATION_REVIEW',
      requestId: '1',
      permitType: 'GHGE',
    });

    createComponent();
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should pass input variables, call services, and filter related tasks', () => {
    expect(component).toBeTruthy();
    expect(hostElement.querySelector('#actions').textContent).toEqual('2');
    expect(hostElement.querySelector('#relatedTasks').textContent).toEqual('1');
    expect(requestItemsService.getItemsByRequestUsingGET).toHaveBeenCalledTimes(1);
    expect(requestActionsService.getRequestActionsByRequestIdUsingGET).toHaveBeenCalledTimes(1);
  });
});
