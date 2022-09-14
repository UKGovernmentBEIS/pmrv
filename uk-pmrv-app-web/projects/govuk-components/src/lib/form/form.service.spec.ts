import { Component } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { FormArray, FormControl, FormGroup, FormsModule, ReactiveFormsModule } from '@angular/forms';

import { GovukComponentsModule } from '../govuk-components.module';
import { FormService } from './form.service';

describe('FormService', () => {
  let service: FormService;

  @Component({
    template: `
      <div [formGroup]="formGroup">
        <div govuk-text-input formControlName="test"></div>
        <div formArrayName="list">
          <div govuk-text-input *ngFor="let control of list.controls; let i = index" [formControlName]="i"></div>
        </div>
      </div>

      <form #ngForm>
        <div govuk-text-input [(ngModel)]="model" name="someField"></div>
      </form>
    `,
  })
  class TestComponent {
    formGroup = new FormGroup({
      test: new FormControl(),
      list: new FormArray([new FormControl()]),
    });
    model: string;

    get list(): FormArray {
      return this.formGroup.get('list') as FormArray;
    }
  }

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, FormsModule, GovukComponentsModule],
      declarations: [TestComponent],
    });
    service = TestBed.inject(FormService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should create an id from the control path', () => {
    const fixture = TestBed.createComponent(TestComponent);
    fixture.detectChanges();
    const element: HTMLElement = fixture.nativeElement;

    expect(element.querySelector('#test')).toBeTruthy();
    expect(element.querySelector('#list\\.0')).toBeTruthy();
    expect(element.querySelector('#someField')).toBeTruthy();
  });
});
