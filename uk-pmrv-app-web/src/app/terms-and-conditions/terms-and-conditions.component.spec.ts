import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { Component } from '@angular/core';
import { ComponentFixture, inject, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { BehaviorSubject, of } from 'rxjs';

import { KeycloakProfile } from 'keycloak-js';

import { ApplicationUserDTO, TermsDTO, UsersService, UserStatusDTO } from 'pmrv-api';

import { buttonClick } from '../../testing';
import { AuthService } from '../core/services/auth.service';
import { LandingPageComponent } from '../landing-page/landing-page.component';
import { SharedModule } from '../shared/shared.module';
import { TermsAndConditionsComponent } from './terms-and-conditions.component';

describe('TermsAndConditionsComponent', () => {
  let component: TermsAndConditionsComponent;
  let fixture: ComponentFixture<TestComponent>;
  let httpTestingController: HttpTestingController;
  const authService: Partial<jest.Mocked<AuthService>> = {
    isLoggedIn: new BehaviorSubject<boolean>(true),
    userProfile: new BehaviorSubject<KeycloakProfile>({ firstName: 'Gimli', lastName: 'Gloin' }),
    userStatus: new BehaviorSubject<UserStatusDTO>({
      loginStatus: 'ENABLED',
      roleType: 'REGULATOR',
      userId: 'opTestId',
    }),
    terms: new BehaviorSubject<TermsDTO>({ url: '/test', version: 1 }),
    loadUser: jest.fn(() => of({} as ApplicationUserDTO)),
  };

  @Component({
    template: '<app-terms-and-conditions></app-terms-and-conditions>',
  })
  class TestComponent {}

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([
          {
            path: '',
            component: LandingPageComponent,
          },
        ]),
        HttpClientTestingModule,
        SharedModule,
      ],
      providers: [UsersService, { provide: AuthService, useValue: authService }],
      declarations: [TermsAndConditionsComponent, TestComponent, LandingPageComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    component = fixture.debugElement.query(By.directive(TermsAndConditionsComponent)).componentInstance;
    fixture.detectChanges();
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpTestingController.verify());

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have as title Accept terms and conditions', () => {
    const compiled = fixture.debugElement.nativeElement;
    expect(compiled.querySelector('h1').textContent).toEqual('Terms and conditions');
  });

  it('should contain a p tag with body', () => {
    const compiled = fixture.debugElement.nativeElement;
    expect(compiled.querySelectorAll('p')[0].textContent.trim()).toEqual(
      'You must agree to the terms and conditions of the UK ETS reporting service.',
    );
  });

  it('should enable button when checkbox is checked', () => {
    const compiled = fixture.debugElement.nativeElement;
    const checkbox = fixture.debugElement.queryAll(By.css('input'));
    checkbox[0].nativeElement.click();
    fixture.detectChanges();
    expect(compiled.querySelector('button').disabled).toBeFalsy();
  });

  it('should post if user accepts terms', inject([Router], (router: Router) => {
    const navigateSpy = jest.spyOn(router, 'navigate').mockImplementation();
    const checkbox = fixture.debugElement.queryAll(By.css('input'));

    buttonClick(fixture);
    fixture.detectChanges();

    const compiled = fixture.debugElement.nativeElement;
    const errorSummary = compiled.querySelector('govuk-error-message').textContent.trim();
    expect(errorSummary).toEqual('Error: You should accept terms and conditions to proceed');

    checkbox[0].nativeElement.click();
    fixture.detectChanges();

    buttonClick(fixture);
    fixture.detectChanges();

    const request = httpTestingController.expectOne('http://localhost:8080/api/v1.0/users/terms-and-conditions');
    expect(request.request.method).toEqual('PATCH');

    request.flush(200);
    expect(navigateSpy).toHaveBeenCalledWith(['']);
  }));
});
