import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { PermitNotificationSharedModule } from '@shared/components/permit-notification/permit-notification-shared.module';
import { SharedModule } from '@shared/shared.module';

import { ActionSharedModule } from '../../shared/action-shared-module';
import { ReviewDecisionComponent } from './review-decision.component';

describe('ReviewDecisionComponent', () => {
  let component: ReviewDecisionComponent;
  let fixture: ComponentFixture<ReviewDecisionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ReviewDecisionComponent],
      imports: [ActionSharedModule, SharedModule, RouterTestingModule, PermitNotificationSharedModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ReviewDecisionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
