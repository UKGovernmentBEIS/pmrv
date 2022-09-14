import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { KeycloakService } from 'keycloak-angular';

import { PermitNotificationFollowUpApplicationReviewRequestTaskPayload } from 'pmrv-api';

import { ActivatedRouteStub, BasePage } from '../../../../../testing';
import { TaskSharedModule } from '../../../shared/task-shared-module';
import { CommonTasksStore } from '../../../store/common-tasks.store';
import { PermitNotificationService } from '../../core/permit-notification.service';
import { FollowUpReviewComponent } from './review.component';

describe('ReviewComponent', () => {
  let component: FollowUpReviewComponent;
  let fixture: ComponentFixture<FollowUpReviewComponent>;
  let page: Page;
  let store: CommonTasksStore;
  let permitNotificationService: PermitNotificationService;

  class Page extends BasePage<FollowUpReviewComponent> {
    get sections(): HTMLUListElement[] {
      return Array.from(this.queryAll<HTMLUListElement>('ul > li'));
    }
    get heading(): string {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }
  }

  const route = new ActivatedRouteStub({ taskId: 63 }, null, {
    pageTitle: 'Review follow up response to a notification',
  });

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [FollowUpReviewComponent],
      providers: [KeycloakService, { provide: ActivatedRoute, useValue: route }],
      imports: [SharedModule, RouterTestingModule, TaskSharedModule],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonTasksStore);
    permitNotificationService = TestBed.inject(PermitNotificationService);

    jest.spyOn(store, 'storeInitialized$', 'get').mockReturnValue(of(true));
    jest.spyOn(store, 'requestTaskItem$', 'get').mockReturnValue(
      of({
        id: 1,
        requestTask: {
          id: 1,
          type: 'PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_REVIEW',
        },
        allowedRequestTaskActions: [],
      }),
    );

    jest.spyOn(permitNotificationService, 'getPayload').mockReturnValue(
      of({
        payloadType: 'PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_REVIEW_PAYLOAD',
        reviewDecision: {
          type: 'ACCEPTED',
          notes: 'dfgfg',
        },
      } as PermitNotificationFollowUpApplicationReviewRequestTaskPayload),
    );

    fixture = TestBed.createComponent(FollowUpReviewComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the review content', () => {
    expect(page.heading).toEqual('Review follow up response to a notification');
    expect(page.sections.map((el) => el.textContent.trim())).toEqual(['Operator follow up response accepted']);
  });
});
