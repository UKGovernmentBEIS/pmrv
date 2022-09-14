import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { KeycloakService } from 'keycloak-angular';

import { ActivatedRouteStub, BasePage } from '../../../../testing';
import { SharedModule } from '../../../shared/shared.module';
import { TaskSharedModule } from '../../shared/task-shared-module';
import { CommonTasksStore } from '../../store/common-tasks.store';
import { FollowUpComponent } from './follow-up.component';

describe('FollowUpComponent', () => {
  let component: FollowUpComponent;
  let fixture: ComponentFixture<FollowUpComponent>;
  let page: Page;
  let store: CommonTasksStore;

  class Page extends BasePage<FollowUpComponent> {
    get sections(): HTMLUListElement[] {
      return Array.from(this.queryAll<HTMLUListElement>('ul > li'));
    }
    get heading(): string {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }
  }

  const route = new ActivatedRouteStub({ taskId: 63 }, null, { pageTitle: 'Follow up response to a notification' });

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [FollowUpComponent],
      providers: [KeycloakService, { provide: ActivatedRoute, useValue: route }],
      imports: [SharedModule, RouterTestingModule, TaskSharedModule],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonTasksStore);
    jest.spyOn(store, 'storeInitialized$', 'get').mockReturnValue(of(true));
    jest.spyOn(store, 'requestTaskItem$', 'get').mockReturnValue(
      of({
        requestTask: {
          id: 63,
          type: 'PERMIT_NOTIFICATION_FOLLOW_UP',
          payload: {
            payloadType: 'PERMIT_NOTIFICATION_FOLLOW_UP_PAYLOAD',
            followUpRequest: 'i need a response from the operator',
            followUpResponseExpirationDate: '2022-05-20',
            followUpAttachments: {},
          },
        },
        allowedRequestTaskActions: [],
      }),
    );

    fixture = TestBed.createComponent(FollowUpComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render follow up tasklist content', () => {
    expect(page.heading).toEqual('Follow up response to a notification');
    expect(page.sections.map((el) => el.textContent.trim())).toEqual([
      'Follow up response not started',
      'Submit cannot start yet',
    ]);
  });
});
