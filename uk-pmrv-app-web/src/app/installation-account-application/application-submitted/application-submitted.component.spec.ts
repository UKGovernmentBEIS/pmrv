import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';

import { GovukComponentsModule } from 'govuk-components';

import { ApplicationSubmittedComponent } from './application-submitted.component';

describe('ApplicationSubmittedComponent', () => {
  let component: ApplicationSubmittedComponent;
  let fixture: ComponentFixture<TestComponent>;

  @Component({
    template: '<app-application-submitted></app-application-submitted>',
  })
  class TestComponent {}

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GovukComponentsModule],
      declarations: [ApplicationSubmittedComponent, TestComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    component = fixture.debugElement.query(By.directive(ApplicationSubmittedComponent)).componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
