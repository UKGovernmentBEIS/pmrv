import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '../shared.module';
import { AssignmentConfirmationComponent } from './assignment-confirmation.component';

describe('AssignmentConfirmationComponent', () => {
  let component: AssignmentConfirmationComponent;
  let fixture: ComponentFixture<TestComponent>;
  let hostComponent: TestComponent;

  @Component({ template: '<app-assignment-confirmation [user]="user"></app-assignment-confirmation>' })
  class TestComponent {
    user: string;
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule],
      declarations: [TestComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    hostComponent = fixture.componentInstance;
    component = fixture.debugElement.query(By.directive(AssignmentConfirmationComponent)).componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the unassigned message if user not provided', () => {
    expect(
      (fixture.nativeElement as HTMLElement).querySelector<HTMLHeadingElement>('.govuk-panel__title').innerHTML.trim(),
    ).toEqual('This task has been unassigned');
    expect(
      (fixture.nativeElement as HTMLElement).querySelector<HTMLDivElement>('.govuk-panel__body').innerHTML.trim(),
    ).toEqual('');
    expect(
      (fixture.nativeElement as HTMLElement).querySelector<HTMLParagraphElement>('.govuk-body').innerHTML.trim(),
    ).toEqual('The task will appear in the unassigned tab of your dashboard');
  });

  it('should render the assignee if provided', () => {
    hostComponent.user = 'Test User';
    fixture.detectChanges();

    expect(
      (fixture.nativeElement as HTMLElement).querySelector<HTMLHeadingElement>('.govuk-panel__title').innerHTML.trim(),
    ).toEqual('The task has been reassigned to');
    expect(
      (fixture.nativeElement as HTMLElement).querySelector<HTMLDivElement>('.govuk-panel__body').innerHTML.trim(),
    ).toEqual('Test User');
    expect(
      (fixture.nativeElement as HTMLElement).querySelector<HTMLParagraphElement>('.govuk-body').innerHTML.trim(),
    ).toEqual('The task will appear in the dashboard of the person to whom it has been assigned to');
  });
});
