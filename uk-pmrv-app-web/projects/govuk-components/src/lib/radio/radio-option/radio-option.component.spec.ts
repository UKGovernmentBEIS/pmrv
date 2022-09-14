import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';

import { GovukComponentsModule } from '../../govuk-components.module';

describe('RadioOptionComponent', () => {
  let fixture: ComponentFixture<TestComponent>;
  let hostComponent: TestComponent;
  let element: HTMLElement;

  @Component({
    template: `
      <div govuk-radio [formControl]="control">
        <govuk-radio-option [value]="1" label="First"></govuk-radio-option>
        <govuk-radio-option [value]="2">
          <ng-container govukLabel>Second</ng-container>
          <ng-container govukConditionalContent>
            <p>This should be revealed</p>
            <div govuk-text-input [formControl]="hiddenControl" label="Hidden control"></div>
          </ng-container>
        </govuk-radio-option>
      </div>
    `,
  })
  class TestComponent {
    control = new FormControl();
    hiddenControl = new FormControl();
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, GovukComponentsModule],
      declarations: [TestComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    hostComponent = fixture.componentInstance;
    element = fixture.nativeElement;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(fixture.debugElement.queryAll(By.css('input[type="radio"]'))).toHaveLength(2);
  });

  it('should display custom labels', () => {
    expect(Array.from(element.querySelectorAll('.govuk-radios__label')).map((label) => label.textContent)).toEqual([
      'First',
      'Second',
    ]);
  });

  it('should hide and reveal conditional content', () => {
    expect(element.querySelector('.govuk-radios__conditional--hidden')).toBeTruthy();
    expect(hostComponent.hiddenControl.disabled).toBeTruthy();

    element.querySelectorAll<HTMLInputElement>('input[type="radio"]')[1].click();
    fixture.detectChanges();

    expect(element.querySelector('.govuk-radios__conditional--hidden')).toBeFalsy();
    expect(hostComponent.hiddenControl.disabled).toBeFalsy();
  });
});
