import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { KeycloakService } from 'keycloak-angular';

import { BasePage } from '../../../../testing';
import { SharedModule } from '../../../shared/shared.module';
import { TaskSharedModule } from '../../shared/task-shared-module';
import { CommonTasksStore } from '../../store/common-tasks.store';
import { ReviewWaitComponent } from './review-wait.component';

describe('ReviewWaitComponent', () => {
  let component: ReviewWaitComponent;
  let fixture: ComponentFixture<ReviewWaitComponent>;
  let page: Page;
  let store: CommonTasksStore;

  class Page extends BasePage<ReviewWaitComponent> {
    get content(): string {
      return this.query<HTMLElement>('.govuk-warning-text').textContent.trim();
    }
    get heading(): string {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ReviewWaitComponent],
      providers: [KeycloakService],
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
          type: 'PERMIT_NOTIFICATION_WAIT_FOR_REVIEW',
        },
        allowedRequestTaskActions: [],
      }),
    );

    fixture = TestBed.createComponent(ReviewWaitComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the review content', () => {
    expect(page.heading).toEqual('Notification application');
    expect(page.content).toEqual('!Warning Waiting for the regulator to make a determination');
  });
});
