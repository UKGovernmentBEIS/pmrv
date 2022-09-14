import { CommonModule } from '@angular/common';
import { Component, NgModule } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { By } from '@angular/platform-browser';

import { changeInputValue } from '../../../../../../src/testing';
import { ErrorMessageComponent } from '../../error-message/error-message.component';
import { FormErrorDirective } from './form-error.directive';

describe('FormErrorDirective', () => {
  @Component({
    template: `
      <div>
        <div class="govuk-form-group">
          <input type="text" [formControl]="groupedControl" govukFormError id="grouped" />
        </div>
        <input type="text" [formControl]="simpleControl" govukFormError id="simple" />
        <select [formControl]="selectControl" govukFormError id="select">
          <option *ngFor="let option of options" [ngValue]="option.value">{{ option.text }}</option>
        </select>
        <textarea [formControl]="textareaControl" govukFormError id="textarea"></textarea>
        <form [formGroup]="formGroup">
          <div class="govuk-form-group">
            <input type="text" formControlName="text" name="text" govukFormError id="groupControl" />
          </div>
          <button type="submit">Submit</button>
        </form>
      </div>
    `,
  })
  class TestComponent {
    groupedControl = new FormControl('', [Validators.minLength(5)]);
    simpleControl = new FormControl('', [Validators.minLength(5)]);
    selectControl = new FormControl('', [Validators.required]);
    textareaControl = new FormControl('', [Validators.required]);
    formGroup = new FormGroup(
      { text: new FormControl(null, { validators: Validators.minLength(5) }) },
      { updateOn: 'submit' },
    );

    options = [
      { text: 'Test1', value: 'aaa' },
      { text: 'Test2', value: 'Check' },
    ];
  }

  @NgModule({
    imports: [CommonModule],
    declarations: [ErrorMessageComponent],
    entryComponents: [ErrorMessageComponent],
  })
  class FormErrorTestModule {}

  let directive: FormErrorDirective;
  let fixture: ComponentFixture<TestComponent>;

  beforeEach(() => {
    fixture = TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, FormErrorTestModule],
      declarations: [FormErrorDirective, TestComponent],
    }).createComponent(TestComponent);

    fixture.detectChanges();
    directive = fixture.debugElement.query(By.directive(FormErrorDirective)).injector.get(FormErrorDirective);
  });

  it('should create an instance', () => {
    expect(directive).toBeTruthy();
  });

  it('should add error class', () => {
    const testElement: HTMLElement = fixture.nativeElement;
    fixture.debugElement.query(By.css('select')).triggerEventHandler('blur', {});
    fixture.detectChanges();

    expect(testElement.querySelector<HTMLSelectElement>('select.govuk-select--error')).toBeTruthy();

    changeInputValue(fixture, '#grouped', 'aaa');
    changeInputValue(fixture, '#simple', 'aaa');
    changeInputValue(fixture, '#select', 'aaa');
    changeInputValue(fixture, '#textarea', 'aaa');
    changeInputValue(fixture, '#groupControl', 'aaa');
    fixture.detectChanges();

    expect(testElement.querySelectorAll<HTMLInputElement>('.govuk-input--error').length).toEqual(2);
    expect(testElement.querySelector('.govuk-form-group--error')).toBeTruthy();

    changeInputValue(fixture, '#grouped', 'check');
    changeInputValue(fixture, '#simple', 'check');

    expect(testElement.querySelectorAll<HTMLInputElement>('.govuk-input-error').length).toEqual(0);
    expect(testElement.querySelector('.govuk-form-group--error')).toBeNull();

    testElement.querySelector<HTMLButtonElement>('button[type="submit"]').click();
    fixture.detectChanges();

    expect(testElement.querySelector('.govuk-form-group--error')).toBeTruthy();
    expect(testElement.querySelector('input[name="text"].govuk-input--error')).toBeTruthy();
  });

  it('should set the id attribute of its host element', () => {
    const testElement: HTMLElement = fixture.nativeElement;

    const [grouped, simple] = Array.from(testElement.querySelectorAll('input'));

    expect(grouped.id).toEqual('grouped');
    expect(simple.id).toEqual('simple');
  });
});
