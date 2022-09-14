import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormControl, FormGroup } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { EmissionPointDetailsTemplateComponent } from '@shared/components/emission-points/emission-point-details/emission-point-details-template.component';
import { SharedModule } from '@shared/shared.module';

import { BasePage } from '../../../../../testing';

describe('EmissionPointDetailsTemplateComponent', () => {
  let component: EmissionPointDetailsTemplateComponent;
  let hostComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let page: Page;

  @Component({
    template: `
      <app-emission-point-details-template
        [form]="formGroup"
        [isEditing]="isEditing"
      ></app-emission-point-details-template>
    `,
  })
  class TestComponent {
    isEditing = true;
    formGroup = new FormGroup({
      id: new FormControl(234),
      reference: new FormControl('EP1'),
      description: new FormControl('ep description'),
    });
    onSubmit: (form: FormGroup) => any | jest.SpyInstance<void, [FormGroup]>;
  }

  class Page extends BasePage<TestComponent> {
    get heading() {
        return this.query<HTMLElement>('app-page-heading h1.govuk-heading-l');
    }

    get inputForm() {
        return this.query<HTMLFormElement>('form');
    }

    get reference() {
        return this.inputForm.querySelector<HTMLInputElement>('#reference');
    }
  
    get description() {
        return this.inputForm.querySelector<HTMLInputElement>('#description');
    }

    get submitButton(): HTMLButtonElement {
        return this.query('button[type="submit"]');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule],
      declarations: [TestComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    page = new Page(fixture);
    hostComponent = fixture.componentInstance;
    component = fixture.debugElement.query(By.directive(EmissionPointDetailsTemplateComponent)).componentInstance;
    hostComponent.onSubmit = jest.fn();
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the nested form', () => {
    expect(page.heading.textContent.trim()).toEqual('Edit emission point');
    expect(page.submitButton).toBeTruthy();
    expect(page.reference.value).toEqual('EP1');
    expect(page.description.value).toEqual('ep description');
  });
});