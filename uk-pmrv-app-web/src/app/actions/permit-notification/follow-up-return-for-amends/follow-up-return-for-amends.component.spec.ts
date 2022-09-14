import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { PermitNotificationSharedModule } from '@shared/components/permit-notification/permit-notification-shared.module';

import { ActionSharedModule } from '../../shared/action-shared-module';
import { FollowUpReturnForAmendsComponent } from './follow-up-return-for-amends.component';

describe('FollowUpReturnForAmendsComponent', () => {
  let component: FollowUpReturnForAmendsComponent;
  let fixture: ComponentFixture<FollowUpReturnForAmendsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [FollowUpReturnForAmendsComponent],
      imports: [ActionSharedModule, PermitNotificationSharedModule, RouterTestingModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FollowUpReturnForAmendsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
