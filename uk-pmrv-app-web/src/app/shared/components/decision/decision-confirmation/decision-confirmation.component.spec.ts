import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '../../../shared.module';
import { DecisionConfirmationComponent } from './decision-confirmation.component';

describe('DecisionConfirmationComponent', () => {
  let component: DecisionConfirmationComponent;
  let fixture: ComponentFixture<TestComponent>;
  let hostComponent: TestComponent;

  @Component({ template: '<app-decision-confirmation [isAccepted]="isAccepted"></app-decision-confirmation>' })
  class TestComponent {
    isAccepted: boolean;
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
    component = fixture.debugElement.query(By.directive(DecisionConfirmationComponent)).componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the approved message', () => {
    hostComponent.isAccepted = true;
    fixture.detectChanges();

    expect(
      (fixture.nativeElement as HTMLElement).querySelector<HTMLHeadingElement>('.govuk-panel__title').innerHTML.trim(),
    ).toEqual(`You've approved the installation account application`);
    expect(
      (fixture.nativeElement as HTMLElement).querySelector<HTMLParagraphElement>('.govuk-body').innerHTML.trim(),
    ).toEqual('An email will be sent to the relevant user with your decision');
  });

  it('should render the rejected message', () => {
    hostComponent.isAccepted = false;
    fixture.detectChanges();

    expect(
      (fixture.nativeElement as HTMLElement).querySelector<HTMLHeadingElement>('.govuk-panel__title').innerHTML.trim(),
    ).toEqual(`You've rejected the installation account application`);
    expect(
      (fixture.nativeElement as HTMLElement).querySelector<HTMLParagraphElement>('.govuk-body').innerHTML.trim(),
    ).toEqual('An email will be sent to the relevant user with your decision');
  });
});
