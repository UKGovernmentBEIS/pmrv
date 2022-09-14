import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { PermitNotificationComponent } from './permit-notification.component';

describe('PermitNotificationContainerComponent', () => {
  let component: PermitNotificationComponent;
  let fixture: ComponentFixture<PermitNotificationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [PermitNotificationComponent],
      imports: [RouterTestingModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PermitNotificationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
