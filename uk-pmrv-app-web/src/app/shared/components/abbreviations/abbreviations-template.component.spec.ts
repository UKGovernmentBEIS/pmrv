import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormArray, FormControl, FormGroup } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { AbbreviationsTemplateComponent } from '@shared/components/abbreviations/abbreviations-template.component';
import { SharedModule } from '@shared/shared.module';

import { GovukValidators } from 'govuk-components';

describe('AbbreviationsTemplateComponent', () => {
  let component: AbbreviationsTemplateComponent;
  let hostComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let element: HTMLElement;

  @Component({
    template: `
      <app-abbreviations-template
        (formSubmit)="onSubmit($event)"
        [form]="formGroup"
        [isEditable]="isEditable"
        caption="Additional information"
      ></app-abbreviations-template>
    `,
  })
  class TestComponent {
    isEditable = true;
    formGroup = new FormGroup({
      exist: new FormControl('', {
        validators: [GovukValidators.required('Select Yes or No')],
        updateOn: 'change',
      }),
      abbreviationDefinitions: new FormArray([]),
    });
    onSubmit: (form: FormGroup) => any | jest.SpyInstance<void, [FormGroup]>;
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule],
      declarations: [TestComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    element = fixture.nativeElement;
    hostComponent = fixture.componentInstance;
    component = fixture.debugElement.query(By.directive(AbbreviationsTemplateComponent)).componentInstance;
    hostComponent.onSubmit = jest.fn();
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the nested form', () => {
    expect(element.querySelector('.govuk-caption-l')).toBeTruthy();
    expect(element.querySelector('.govuk-heading-l')).toBeTruthy();
    expect(element.querySelector('button[govukSecondaryButton]')).toBeTruthy();
    expect(element.querySelector('button[type="submit"]')).toBeTruthy();
    expect(element.querySelectorAll('input').length).toEqual(2);
  });

  it('should render errors on submit', () => {
    expect(element.querySelector('.govuk-error-summary')).toBeFalsy();

    element.querySelector<HTMLButtonElement>('button[type="submit"]').click();

    fixture.detectChanges();

    expect(element.querySelector('.govuk-error-summary')).toBeTruthy();
    expect(hostComponent.onSubmit).not.toHaveBeenCalled();
  });

  it('should render the remove button', () => {
    element.querySelectorAll<HTMLOptionElement>('input')[0].click();
    fixture.detectChanges();

    expect(element.querySelectorAll<HTMLButtonElement>('button[govukSecondaryButton]').length).toEqual(1);

    element.querySelector<HTMLButtonElement>('button[govukSecondaryButton]').click();
    fixture.detectChanges();

    expect(element.querySelectorAll<HTMLButtonElement>('button[govukSecondaryButton]').length).toEqual(3);
  });
});
