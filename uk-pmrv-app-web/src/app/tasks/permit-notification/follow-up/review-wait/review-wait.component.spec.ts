import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { KeycloakService } from 'keycloak-angular';

import { ActivatedRouteStub, BasePage } from '../../../../../testing';
import { SharedModule } from '../../../../shared/shared.module';
import { TaskSharedModule } from '../../../shared/task-shared-module';
import { CommonTasksStore } from '../../../store/common-tasks.store';
import { FollowUpReviewWaitComponent } from './review-wait.component';

describe('ReviewWaitComponent', () => {
  let component: FollowUpReviewWaitComponent;
  let fixture: ComponentFixture<FollowUpReviewWaitComponent>;
  let page: Page;
  let store: CommonTasksStore;

  class Page extends BasePage<FollowUpReviewWaitComponent> {
    get content(): string {
      return this.query<HTMLElement>('.govuk-warning-text').textContent.trim();
    }
    get heading(): string {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }
  }

  const route = new ActivatedRouteStub({ taskId: 63 }, null, { pageTitle: 'Follow up response submitted' });

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [FollowUpReviewWaitComponent],
      providers: [KeycloakService, { provide: ActivatedRoute, useValue: route }],
      imports: [SharedModule, RouterTestingModule, TaskSharedModule],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonTasksStore);
    jest.spyOn(store, 'storeInitialized$', 'get').mockReturnValue(of(true));
    jest.spyOn(store, 'requestTaskItem$', 'get').mockReturnValue(
      of({
        id: 1,
        requestTask: {
          id: 1,
          type: 'PERMIT_NOTIFICATION_FOLLOW_UP_WAIT_FOR_REVIEW',
        },
        allowedRequestTaskActions: [],
      }),
    );

    fixture = TestBed.createComponent(FollowUpReviewWaitComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the review content', () => {
    expect(page.heading).toEqual('Follow up response submitted');
    expect(page.content).toEqual('!Warning Waiting for the regulator to make a determination');
  });
});
