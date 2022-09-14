import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { KeycloakService } from 'keycloak-angular';

import { BasePage } from '../../../../testing';
import { SharedModule } from '../../../shared/shared.module';
import { TaskSharedModule } from '../../shared/task-shared-module';
import { CommonTasksStore } from '../../store/common-tasks.store';
import { PermitNotificationService } from '../core/permit-notification.service';
import { PermitNotificationModule } from '../permit-notification.module';
import { ReviewComponent } from './review.component';

describe('ReviewComponent', () => {
  let component: ReviewComponent;
  let fixture: ComponentFixture<ReviewComponent>;
  let page: Page;
  let store: CommonTasksStore;
  let permitNotificationService: PermitNotificationService;

  class Page extends BasePage<ReviewComponent> {
    get sections(): HTMLUListElement[] {
      return Array.from(this.queryAll<HTMLUListElement>('ul > li'));
    }
    get heading(): string {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [KeycloakService],
      imports: [PermitNotificationModule, SharedModule, RouterTestingModule, TaskSharedModule],
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
          type: 'PERMIT_NOTIFICATION_APPLICATION_REVIEW',
        },
        allowedRequestTaskActions: [],
      }),
    );

    jest.spyOn(permitNotificationService, 'getPayload').mockReturnValue(
      of({
        payloadType: 'PERMIT_NOTIFICATION_APPLICATION_REVIEW_PAYLOAD',
        reviewDecision: {
          type: 'ACCEPTED',
          officialNotice: 'Laborum officiis eiu',
          followUp: {
            followUpResponseRequired: true,
            followUpRequest: 'xgdfg',
            followUpResponseExpirationDate: '2033-01-01',
          },
          notes: 'dfgfg',
        },
        permitNotification: {
          type: 'OTHER_FACTOR',
          description: 'description',
          reportingType: 'RENOUNCE_FREE_ALLOCATIONS',
        },
      }),
    );

    fixture = TestBed.createComponent(ReviewComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the review content', () => {
    expect(page.heading).toEqual('Review notification of a change');
    expect(page.sections.map((el) => el.textContent.trim())).toEqual(['Details of the Change accepted']);
  });
});
