import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { PermitNotificationSharedModule } from '@shared/components/permit-notification/permit-notification-shared.module';
import { SharedModule } from '@shared/shared.module';
import { KeycloakService } from 'keycloak-angular';

import { ActivatedRouteStub, BasePage } from '../../../../../../testing';
import { TaskSharedModule } from '../../../../shared/task-shared-module';
import { CommonTasksStore } from '../../../../store/common-tasks.store';
import { WaitComponent } from './wait.component';

describe('WaitComponent', () => {
  let component: WaitComponent;
  let fixture: ComponentFixture<WaitComponent>;
  let page: Page;
  let store: CommonTasksStore;

  class Page extends BasePage<WaitComponent> {
    get heading(): string {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }
  }

  const route = new ActivatedRouteStub({ taskId: 63 }, null, {
    pageTitle: 'Awaiting follow up response to notification',
  });

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [WaitComponent],
      providers: [KeycloakService, { provide: ActivatedRoute, useValue: route }],
      imports: [SharedModule, RouterTestingModule, TaskSharedModule, PermitNotificationSharedModule],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonTasksStore);
    jest.spyOn(store, 'storeInitialized$', 'get').mockReturnValue(of(true));
    jest.spyOn(store, 'requestTaskItem$', 'get').mockReturnValue(
      of({
        requestTask: {
          id: 63,
          type: 'PERMIT_NOTIFICATION_WAIT_FOR_FOLLOW_UP',
          payload: {
            payloadType: 'PERMIT_NOTIFICATION_WAIT_FOR_FOLLOW_UP_PAYLOAD',
            followUpRequest: 'i need a response from the operator',
            followUpResponseExpirationDate: '2022-05-20',
            followUpAttachments: {},
          },
        },
        allowedRequestTaskActions: [],
      }),
    );

    fixture = TestBed.createComponent(WaitComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render awaiting for follow up tasklist content', () => {
    expect(page.heading).toEqual('Awaiting follow up response to notification');
  });
});
