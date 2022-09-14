import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { PermitNotificationSharedModule } from '@shared/components/permit-notification/permit-notification-shared.module';
import { KeycloakService } from 'keycloak-angular';

import { BasePage } from '../../../../testing';
import { SharedModule } from '../../../shared/shared.module';
import { ActionSharedModule } from '../../shared/action-shared-module';
import { CommonActionsStore } from '../../store/common-actions.store';
import { PermitNotificationModule } from '../permit-notification.module';
import { SubmittedComponent } from './submitted.component';

describe('SubmittedComponent', () => {
  let component: SubmittedComponent;
  let fixture: ComponentFixture<SubmittedComponent>;
  let page: Page;
  let activatedRouteSpy;
  let store: CommonActionsStore;

  class Page extends BasePage<SubmittedComponent> {
    get sections(): HTMLUListElement[] {
      return Array.from(this.queryAll<HTMLUListElement>('ul > li'));
    }

    get heading(): string {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }
  }

  beforeEach(async () => {
    activatedRouteSpy = {
      data: of({ pageTitle: 'Notify the regulator of a change' }),
    };
    await TestBed.configureTestingModule({
      providers: [KeycloakService, { provide: ActivatedRoute, useValue: activatedRouteSpy }],
      imports: [
        SharedModule,
        RouterTestingModule,
        ActionSharedModule,
        PermitNotificationModule,
        PermitNotificationSharedModule,
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonActionsStore);
    jest.spyOn(store, 'storeInitialized$', 'get').mockReturnValue(of(true));
    jest.spyOn(store, 'requestAction$', 'get').mockReturnValue(
      of({
        type: 'PERMIT_NOTIFICATION_APPLICATION_SUBMITTED',
        id: 1,
        payload: {
          payloadType: 'PERMIT_NOTIFICATION_APPLICATION_SUBMITTED_PAYLOAD',
          submitter: '123',
        },
      }),
    );

    fixture = TestBed.createComponent(SubmittedComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the review content', () => {
    expect(page.heading).toEqual('Notify the regulator of a change');
  });
});
