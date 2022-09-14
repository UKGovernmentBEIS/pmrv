import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormControl, FormGroup } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { ApproachesAddTemplateComponent } from '@shared/components/approaches/approaches-add/approaches-add-template.component';
import { SharedModule } from '@shared/shared.module';

describe('ApproachesAddTemplateComponent', () => {
  let component: ApproachesAddTemplateComponent;
  let hostComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let element: HTMLElement;

  @Component({
    template: `
      <app-approaches-add-template
        (formSubmit)="onSubmit()"
        [monitoringApproaches]="monitoringApproaches"
        [form]="form"
      ></app-approaches-add-template>
    `,
  })
  class TestComponent {
    form = new FormGroup({
      monitoringApproaches: new FormControl(null),
    });
    monitoringApproaches = { CALCULATION: 'CALCULATION' };
    onSubmit: () => any | jest.SpyInstance<void>;
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
    component = fixture.debugElement.query(By.directive(ApproachesAddTemplateComponent)).componentInstance;
    hostComponent.onSubmit = jest.fn();
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the rest approaches', () => {
    expect(element.querySelectorAll<HTMLInputElement>('.govuk-checkboxes__input').length).toEqual(6);
  });
});
