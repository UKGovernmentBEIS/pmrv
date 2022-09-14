import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { KeycloakService } from 'keycloak-angular';

import { SharedModule } from '../../../../../shared/shared.module';
import { CommonTasksStore } from '../../../../store/common-tasks.store';
import { FollowUpReviewNotifyOperatorComponent } from './notify-operator.component';

describe('FollowUpReviewNotifyOperatorComponent', () => {
  let component: FollowUpReviewNotifyOperatorComponent;
  let fixture: ComponentFixture<FollowUpReviewNotifyOperatorComponent>;
  let store: CommonTasksStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule],
      declarations: [FollowUpReviewNotifyOperatorComponent],
      providers: [KeycloakService],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonTasksStore);
    store.setState({
      ...store.getState(),
      storeInitialized: true,
      isEditable: true,
      requestTaskItem: {
        requestInfo: {
          accountId: 1,
        },
      },
    });

    fixture = TestBed.createComponent(FollowUpReviewNotifyOperatorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
