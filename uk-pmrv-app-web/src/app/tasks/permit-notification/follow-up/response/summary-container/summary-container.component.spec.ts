import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { PermitNotificationSharedModule } from '@shared/components/permit-notification/permit-notification-shared.module';
import { SharedModule } from '@shared/shared.module';
import { KeycloakService } from 'keycloak-angular';

import { TaskSharedModule } from '../../../../shared/task-shared-module';
import { SummaryContainerComponent } from './summary-container.component';

describe('SummaryContainerComponent', () => {
  let component: SummaryContainerComponent;
  let fixture: ComponentFixture<SummaryContainerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SummaryContainerComponent],
      imports: [RouterTestingModule, SharedModule, TaskSharedModule, PermitNotificationSharedModule],
      providers: [KeycloakService],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SummaryContainerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
