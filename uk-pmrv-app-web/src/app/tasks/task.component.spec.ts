import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Routes } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { RouterStubComponent } from '../../testing';
import { SharedModule } from '../shared/shared.module';
import { CancelComponent } from './cancel/cancel.component';
import { ConfirmationComponent } from './cancel/confirmation/confirmation.component';
import { TaskComponent } from './task.component';

const routes: Routes = [
  {
    path: 'tasks',
    component: TaskComponent,
    children: [
      {
        path: ':taskId',
        component: TaskComponent,
        children: [
          {
            path: 'permit-notification',
            component: RouterStubComponent,
          },
        ],
      },
    ],
  },
  {
    path: 'dashboard',
    component: RouterStubComponent,
  },
];

describe('TaskComponent', () => {
  let component: TaskComponent;
  // let router: Router;
  let fixture: ComponentFixture<TaskComponent>;
  // let store: CommonTasksStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TaskComponent, CancelComponent, ConfirmationComponent, RouterStubComponent],
      imports: [SharedModule, RouterTestingModule.withRoutes(routes)],
    }).compileComponents();
    // router = TestBed.inject(Router);
    // store = TestBed.inject(CommonTasksStore);
    fixture = await TestBed.createComponent(TaskComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  // it('should redirect to /dashboard when requesting /tasks', fakeAsync(() => {
  //   const activatedRoute = TestBed.inject(ActivatedRoute);
  //   const navigateSpy = jest.spyOn(router, 'navigate');
  //   //simulate browser navigation which will have empty values for params and url
  //   activatedRoute.params = of({});
  //   activatedRoute.url = of([]);
  //   jest.spyOn(router, 'url', 'get').mockReturnValue('tasks');
  //
  //   router.navigate(['tasks']);
  //
  //   tick();
  //   fixture.detectChanges();
  //
  //   expect(navigateSpy).toHaveBeenCalledTimes(3);
  //   expect(navigateSpy).toHaveBeenLastCalledWith(['/dashboard'], { skipLocationChange: true });
  // }));

  // it('should redirect for a task of the correct type when requesting /tasks/:taskId', fakeAsync(() => {
  //   router.navigate(['tasks', '35']);
  //
  //   jest.spyOn(store, 'requestedTask').mockReturnValue(null);
  //   jest.spyOn(store, 'requestTaskItem$', 'get').mockReturnValue(of(mockRequestItem));
  //   const navigateSpy = jest.spyOn(router, 'navigate');
  //
  //   tick();
  //   fixture.detectChanges();
  //
  //   expect(navigateSpy).toHaveBeenCalledTimes(1);
  //
  //   const calls = navigateSpy.mock.calls;
  //   const lastCall = calls[calls.length - 1][0];
  //   expect(lastCall).toEqual([TaskRoute.permitNotification]);
  // }));
  //
  // it('should throw an error for a task of the wrong type when requesting /tasks/:taskId', fakeAsync(() => {
  //   router.navigate(['tasks', '35']);
  //
  //   jest.spyOn(store, 'requestedTask').mockReturnValue(null);
  //   jest.spyOn(store, 'requestTaskItem$', 'get').mockReturnValue(of(invalidMockRequestItem));
  //   const navigateSpy = jest.spyOn(router, 'navigate');
  //
  //   tick();
  //   fixture.detectChanges();
  //
  //   expect(navigateSpy).toHaveBeenCalledTimes(0);
  // }));
});

// const mockRequestItem: RequestTaskItemDTO = {
//   requestTask: {
//     id: 35,
//     type: 'PERMIT_NOTIFICATION_APPLICATION_SUBMIT',
//     assignable: true,
//     assigneeUserId: 'b7f63bf9-4640-4419-a318-0d5fed06f4ab',
//     assigneeFullName: 'Assignee',
//     startDate: '2022-04-06T19:12:31.722238Z',
//   },
//   allowedRequestTaskActions: [
//     'PERMIT_NOTIFICATION_SAVE_APPLICATION',
//     'PERMIT_NOTIFICATION_UPLOAD_ATTACHMENT',
//     'PERMIT_NOTIFICATION_SUBMIT_APPLICATION',
//     'PERMIT_NOTIFICATION_CANCEL_APPLICATION',
//   ],
//   userAssignCapable: true,
//   requestInfo: { id: 'AEMN1-3', type: 'PERMIT_NOTIFICATION', competentAuthority: 'ENGLAND', accountId: 1 },
// };

// const invalidMockRequestItem: RequestTaskItemDTO = {
//   ...mockRequestItem,
//   requestTask: {
//     ...mockRequestItem.requestTask,
//     type: <any>'RANDOM_ACTION',
//   },
// };
