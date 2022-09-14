import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormArray, FormControl, FormGroup } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { ConfidentialityStatementTemplateComponent } from '@shared/components/confidentiality-statement/confidentiality-statement-template.component';
import { SharedModule } from '@shared/shared.module';
import { BasePage, mockClass } from '@testing';

import { GovukValidators } from 'govuk-components';

import { TasksService } from 'pmrv-api';

describe('ConfidentialityStatementTemplateComponent', () => {
  let page: Page;
  let hostComponent: TestComponent;
  let component: ConfidentialityStatementTemplateComponent;
  let fixture: ComponentFixture<TestComponent>;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<TestComponent> {
    get existRadios() {
      return this.queryAll<HTMLInputElement>('input[name$="exist"]');
    }

    get sections() {
      return this.queryAll<HTMLInputElement>('input[name$="section"]');
    }

    get sectionValues() {
      return this.sections.map((input) => input.value);
    }

    set sectionValues(values: string[]) {
      this.sections.forEach((input, index) => this.setInputValue(`#${input.id}`, values[index]));
    }

    get explanations() {
      return this.queryAll<HTMLTextAreaElement>('textarea');
    }

    get explanationValues() {
      return this.explanations.map((input) => input.value);
    }

    set explanationValues(values: string[]) {
      this.explanations.forEach((input, index) => this.setInputValue(`#${input.id}`, values[index]));
    }

    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get errors() {
      return Array.from(this.errorSummary.querySelectorAll('a'));
    }

    get addAnotherButton() {
      const secondaryButtons = this.queryAll<HTMLButtonElement>('button[type="button"]');

      return secondaryButtons[secondaryButtons.length - 1];
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  @Component({
    template: `
      <app-confidentiality-statement-template
        (formSubmit)="onSubmit($event)"
        [form]="formGroup"
        [isEditable]="isEditable"
      ></app-confidentiality-statement-template>
    `,
  })
  class TestComponent {
    isEditable = true;
    formGroup = new FormGroup({
      exist: new FormControl(true, {
        validators: [GovukValidators.required('Select Yes or No')],
        updateOn: 'change',
      }),
      confidentialSections: new FormArray([
        new FormGroup({
          section: new FormControl('section 1'),
          explanation: new FormControl('explanation 1'),
        }),
      ]),
    });
    onSubmit: (form: FormGroup) => any | jest.SpyInstance<void, [FormGroup]>;
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule],
      declarations: [TestComponent],
      providers: [{ provide: TasksService, useValue: tasksService }],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    hostComponent = fixture.componentInstance;
    component = fixture.debugElement.query(By.directive(ConfidentialityStatementTemplateComponent)).componentInstance;
    hostComponent.onSubmit = jest.fn();
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display an non empty form', () => {
    expect(page.existRadios.some((radio) => radio.checked)).toBeTruthy();
    expect(page.sections).toHaveLength(1);
  });

  it('should display the existing data', () => {
    expect(page.existRadios[0].checked).toBeTruthy();
    expect(page.sectionValues).toEqual(['section 1']);
    expect(page.explanationValues).toEqual(['explanation 1']);
  });

  it('should add another section', () => {
    page.addAnotherButton.click();
    fixture.detectChanges();

    expect(page.sections).toHaveLength(2);
    expect(page.explanations).toHaveLength(2);

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeTruthy();
  });
});
