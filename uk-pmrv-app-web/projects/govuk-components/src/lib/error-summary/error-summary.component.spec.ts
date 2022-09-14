import { Component, ViewChild } from '@angular/core';
import { ComponentFixture, fakeAsync, TestBed } from '@angular/core/testing';
import { FormArray, FormControl, FormGroup, FormsModule, NgForm, ReactiveFormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { ErrorMessageComponent } from '../error-message/error-message.component';
import { GovukValidators } from '../error-message/govuk-validators';
import { TextInputComponent } from '../text-input/text-input.component';
import { ErrorSummaryComponent } from './error-summary.component';

describe('ErrorSummaryComponent', () => {
  @Component({
    template: `
      <form *ngIf="!isTemplate" [formGroup]="form">
        <govuk-error-summary [form]="form"></govuk-error-summary>
        <div govuk-text-input inputType="text" formControlName="topLevel"></div>
        <div formGroupName="secondLevelTop">
          <div govuk-text-input inputType="text" formControlName="secondLevelFirst"></div>
          <div govuk-text-input inputType="text" formControlName="secondLevelSecond"></div>
        </div>
        <div formArrayName="secondLevelArrayTop">
          <div [formGroupName]="0">
            <div govuk-text-input inputType="text" formControlName="nestedArrayControl"></div>
          </div>
          <div govuk-text-input inputType="text" [formControlName]="1"></div>
        </div>
      </form>

      <form #templateForm="ngForm" *ngIf="isTemplate">
        <govuk-error-summary [form]="templateForm"></govuk-error-summary>
        <select [(ngModel)]="selectValue" name="someField" required></select>
      </form>
    `,
  })
  class TestComponent {
    @ViewChild('templateForm') public testForm: NgForm;

    form: FormGroup;
    isTemplate = false;
    selectValue: any;
  }

  let component: ErrorSummaryComponent;
  let hostComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;

  const reactiveForm = new FormGroup({
    topLevel: new FormControl(null, GovukValidators.required('Enter topLevel')),
    secondLevelTop: new FormGroup({
      secondLevelFirst: new FormControl(null, GovukValidators.required('Enter secondLevelFirst')),
      secondLevelSecond: new FormControl(null, GovukValidators.required('Enter secondLevelSecond')),
    }),
    secondLevelArrayTop: new FormArray([
      new FormGroup({
        nestedArrayControl: new FormControl(null, GovukValidators.required('Enter nestedArrayControl')),
      }),
      new FormControl(null, GovukValidators.required('Enter arrayControl')),
    ]),
  });

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, ReactiveFormsModule, FormsModule],
      declarations: [ErrorSummaryComponent, TestComponent, TextInputComponent, ErrorMessageComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    hostComponent = fixture.componentInstance;
    hostComponent.form = reactiveForm;
    fixture.detectChanges();
    component = fixture.debugElement.query(By.directive(ErrorSummaryComponent)).componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display reactive form errors', fakeAsync(async () => {
    hostComponent.form = reactiveForm;
    hostComponent.form.markAllAsTouched();
    fixture.detectChanges();
    await fixture.whenStable();
    fixture.detectChanges();

    const hostElement: HTMLElement = fixture.nativeElement;
    const errors = hostElement.querySelectorAll('a');

    expect(errors.length).toEqual(5);
    expect(Array.from(errors).map((error) => error.textContent.trim())).toEqual([
      'Enter topLevel',
      'Enter secondLevelFirst',
      'Enter secondLevelSecond',
      'Enter nestedArrayControl',
      'Enter arrayControl',
    ]);
  }));

  it('should display template form errors', fakeAsync(async () => {
    hostComponent.isTemplate = true;
    fixture.detectChanges();
    await fixture.whenStable();
    hostComponent.testForm.control.markAllAsTouched();
    fixture.detectChanges();

    const hostElement: HTMLElement = fixture.nativeElement;

    expect(hostElement.querySelectorAll<HTMLAnchorElement>('a').length).toEqual(1);
  }));

  it('should prefix the title with Error', fakeAsync(async () => {
    hostComponent.form = reactiveForm;
    hostComponent.form.markAllAsTouched();
    fixture.detectChanges();

    expect(document.title).toContain('Error');
  }));

  it('should focus on error container', fakeAsync(async () => {
    hostComponent.form = reactiveForm;
    hostComponent.form.markAllAsTouched();
    fixture.detectChanges();

    const hostElement: HTMLElement = fixture.nativeElement;
    const errorContainer = hostElement.querySelector<HTMLDivElement>('.govuk-error-summary');
    expect(document.activeElement).toBe(errorContainer);
  }));
});
