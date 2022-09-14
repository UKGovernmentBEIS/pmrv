import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { KeycloakService } from 'keycloak-angular';

import { SharedModule } from '../../../shared/shared.module';
import { TaskSharedModule } from '../../shared/task-shared-module';
import { PermitNotificationModule } from '../permit-notification.module';
import { PeerReviewComponent } from './peer-review.component';

describe('PeerReviewComponent', () => {
  let component: PeerReviewComponent;
  let fixture: ComponentFixture<PeerReviewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [KeycloakService],
      imports: [PermitNotificationModule, SharedModule, RouterTestingModule, TaskSharedModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PeerReviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
