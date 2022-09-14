import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { KeycloakService } from 'keycloak-angular';

import { BasePage } from '../../../../testing';
import { SharedModule } from '../../../shared/shared.module';
import { TaskSharedModule } from '../../shared/task-shared-module';
import { CommonTasksStore } from '../../store/common-tasks.store';
import { PermitNotificationModule } from '../permit-notification.module';
import { SubmitContainerComponent } from './submit-container.component';

describe('SubmitComponent', () => {
  let component: SubmitContainerComponent;
  let fixture: ComponentFixture<SubmitContainerComponent>;
  let page: Page;
  let store: CommonTasksStore;

  class Page extends BasePage<SubmitContainerComponent> {
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
      imports: [SharedModule, RouterTestingModule, TaskSharedModule, PermitNotificationModule],
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
          type: 'PERMIT_NOTIFICATION_APPLICATION_SUBMIT',
        },
        allowedRequestTaskActions: [],
      }),
    );

    fixture = TestBed.createComponent(SubmitContainerComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the review content', () => {
    expect(page.heading).toEqual('Notify the regulator of a change');
    expect(page.sections.map((el) => el.textContent.trim())).toEqual([
      'Details of the Change not started',
      'Submit cannot start yet',
    ]);
  });
});
