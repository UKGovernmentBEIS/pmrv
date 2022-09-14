import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { GovukComponentsModule } from 'govuk-components';

import { OperatorUsersRegistrationService } from 'pmrv-api';

import { SharedModule } from '../../shared/shared.module';
import { VerificationSentComponent } from '../verification-sent/verification-sent.component';
import { EmailComponent } from './email.component';

describe('EmailComponent', () => {
  let component: EmailComponent;
  let fixture: ComponentFixture<TestComponent>;

  @Component({ template: '<app-email></app-email>' })
  class TestComponent {}

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GovukComponentsModule, SharedModule, ReactiveFormsModule, RouterTestingModule],
      declarations: [TestComponent, EmailComponent, VerificationSentComponent],
      providers: [
        {
          provide: OperatorUsersRegistrationService,
          useValue: { sendVerificationEmailUsingPOST: jest.fn((email) => of(email)) },
        },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    component = fixture.debugElement.query(By.directive(EmailComponent)).componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should submit only valid emails', () => {
    const element: HTMLElement = fixture.nativeElement;
    const emailInput = fixture.debugElement.query(By.css('input'));
    const button = element.querySelector<HTMLButtonElement>('button');
    const getVerificationSent = () => fixture.debugElement.query(By.directive(VerificationSentComponent));

    const setValueAndSubmit = (value) => {
      emailInput.triggerEventHandler('input', { target: { value } });
      button.click();
      fixture.detectChanges();
    };

    setValueAndSubmit('');
    setValueAndSubmit('asd');
    setValueAndSubmit('a'.repeat(300));
    expect(getVerificationSent());

    setValueAndSubmit('test@example.com');
    expect(getVerificationSent());
  });
});
