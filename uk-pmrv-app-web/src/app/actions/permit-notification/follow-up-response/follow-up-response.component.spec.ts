import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { PermitNotificationSharedModule } from '@shared/components/permit-notification/permit-notification-shared.module';
import { SharedModule } from '@shared/shared.module';

import { ActionSharedModule } from '../../shared/action-shared-module';
import { FollowUpResponseComponent } from './follow-up-response.component';

describe('FollowUpResponseComponent', () => {
  let component: FollowUpResponseComponent;
  let fixture: ComponentFixture<FollowUpResponseComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [FollowUpResponseComponent],
      imports: [ActionSharedModule, SharedModule, RouterTestingModule, PermitNotificationSharedModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FollowUpResponseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
