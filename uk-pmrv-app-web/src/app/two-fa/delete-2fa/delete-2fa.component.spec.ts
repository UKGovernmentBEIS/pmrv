import { HttpErrorResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of, throwError } from 'rxjs';

import { KeycloakService } from 'keycloak-angular';

import { UsersSecuritySetupService } from 'pmrv-api';

import { ActivatedRouteStub, mockClass } from '../../../testing';
import { AuthService } from '../../core/services/auth.service';
import { SharedModule } from '../../shared/shared.module';
import { Delete2faComponent } from './delete-2fa.component';

describe('Delete2faComponent', () => {
  let component: Delete2faComponent;
  let fixture: ComponentFixture<Delete2faComponent>;
  let router: Router;

  const activatedRouteStub = new ActivatedRouteStub(null, { token: 'token' });
  const usersSecuritySetupService = mockClass(UsersSecuritySetupService);
  const authService = mockClass(AuthService);

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule],
      providers: [
        KeycloakService,
        { provide: AuthService, useValue: authService },
        { provide: ActivatedRoute, useValue: activatedRouteStub },
        { provide: UsersSecuritySetupService, useValue: usersSecuritySetupService },
      ],
      declarations: [Delete2faComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(Delete2faComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should logout user after successful 2fa deletion', () => {
    usersSecuritySetupService.deleteOtpCredentialsUsingPATCH.mockReturnValue(of({}));
    const navigateSpy = jest.spyOn(router, 'navigate').mockImplementation();
    component.ngOnInit();

    expect(navigateSpy).not.toHaveBeenCalled();
    expect(authService.logout).toHaveBeenCalledWith('/');
  });

  it('should navigate for link related error', () => {
    usersSecuritySetupService.deleteOtpCredentialsUsingPATCH.mockReturnValue(
      throwError(() => new HttpErrorResponse({ error: { code: 'EMAIL1001' }, status: 400 })),
    );
    const navigateSpy = jest.spyOn(router, 'navigate').mockImplementation();
    component.ngOnInit();

    expect(navigateSpy).toHaveBeenCalledTimes(1);
    expect(navigateSpy).toHaveBeenCalledWith(['2fa', 'invalid-link'], {
      queryParams: { code: 'EMAIL1001' },
    });
  });
});
