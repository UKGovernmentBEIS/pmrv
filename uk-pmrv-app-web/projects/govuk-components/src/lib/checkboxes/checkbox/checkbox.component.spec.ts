import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';

import { GovukComponentsModule } from '../../govuk-components.module';

describe('CheckboxComponent', () => {
  let hostComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let element: HTMLElement;

  @Component({
    template: `
      <div govuk-checkboxes [formControl]="control">
        <govuk-checkbox [value]="1" label="First"></govuk-checkbox>
        <govuk-checkbox [value]="2">
          <ng-container govukLabel>Second</ng-container>
          <ng-container govukConditionalContent>
            <p>This should be revealed</p>
            <div govuk-text-input [formControl]="hiddenControl" label="Hidden control"></div>
          </ng-container>
        </govuk-checkbox>
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
    expect(fixture.debugElement.queryAll(By.css('input[type="checkbox"]'))).toHaveLength(2);
  });

  it('should display custom labels', () => {
    expect(Array.from(element.querySelectorAll('.govuk-checkboxes__label')).map((label) => label.textContent)).toEqual([
      'First',
      'Second',
    ]);
  });

  it('should hide and reveal conditional content', () => {
    expect(element.querySelector('.govuk-checkboxes__conditional--hidden')).toBeTruthy();
    expect(hostComponent.hiddenControl.disabled).toBeTruthy();

    element.querySelectorAll<HTMLInputElement>('input[type="checkbox"]')[1].click();
    fixture.detectChanges();

    expect(element.querySelector('.govuk-checkboxes__conditional--hidden')).toBeFalsy();
    expect(hostComponent.hiddenControl.disabled).toBeFalsy();
  });
});
