import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { ActivatedRouteStub, mockClass } from '../../../testing';
import { AuthService } from '../../core/services/auth.service';
import { SharedModule } from '../../shared/shared.module';
import { TimedOutComponent } from './timed-out.component';

describe('TimedOutComponent', () => {
  let component: TimedOutComponent;
  let fixture: ComponentFixture<TimedOutComponent>;
  const authService = mockClass(AuthService);
  const activatedRoute = new ActivatedRouteStub(null, { idle: 30 });

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule],
      declarations: [TimedOutComponent],
      providers: [
        { provide: AuthService, useValue: authService },
        { provide: ActivatedRoute, useValue: activatedRoute },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TimedOutComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should sign in again on button click', () => {
    const loginSpy = jest.spyOn(authService, 'login');
    const button = fixture.nativeElement.querySelector('button');

    button.click();

    expect(loginSpy).toHaveBeenCalledTimes(1);
  });

  it('should show idle time properly', () => {
    const message = fixture.nativeElement.querySelector('.govuk-body').innerHTML;
    expect(message).toContain('We have reset your session because you did not do anything for less than a minute .');
  });
});
