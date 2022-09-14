import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormControl, FormGroup } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { EmissionSourceDetailsTemplateComponent } from '@shared/components/emission-sources/emission-source-detail/emission-source-details-template.component';
import { SharedModule } from '@shared/shared.module';

describe('EmissionSourceDetailsTemplateComponent', () => {
  let component: EmissionSourceDetailsTemplateComponent;
  let hostComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let element: HTMLElement;

  @Component({
    template: `
      <app-emission-source-details-template
        [form]="formGroup"
        [isEditing]="isEditing"
        [caption]="'the caption'"
      ></app-emission-source-details-template>
    `,
  })
  class TestComponent {
    isEditing = true;
    formGroup = new FormGroup({
      id: new FormControl(),
      reference: new FormControl(),
      description: new FormControl(),
      otherDescriptionName: new FormControl(),
      type: new FormControl(),
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
    component = fixture.debugElement.query(By.directive(EmissionSourceDetailsTemplateComponent)).componentInstance;
    hostComponent.onSubmit = jest.fn();
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the nested form', () => {
    expect(element.querySelector('.govuk-caption-l')).toBeTruthy();
    expect(element.querySelector('.govuk-heading-l')).toBeTruthy();
    expect(element.querySelector('button[govukbutton]')).toBeTruthy();
    expect(element.querySelector('button[type="submit"]')).toBeTruthy();
    expect(element.querySelectorAll('input').length).toEqual(2);
  });
});
