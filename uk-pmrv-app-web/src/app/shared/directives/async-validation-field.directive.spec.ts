import { ChangeDetectorRef, Component } from '@angular/core';
import { ComponentFixture, fakeAsync, TestBed } from '@angular/core/testing';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';

import { GovukComponentsModule } from 'govuk-components';

import { AsyncValidationFieldDirective } from './async-validation-field.directive';

describe('AsyncValidationFieldDirective', () => {
  let directive: AsyncValidationFieldDirective;
  let fixture: ComponentFixture<TestComponent>;

  @Component({
    template: '<div govuk-text-input appAsyncValidationField [formControl]="name" label="Name"></div>',
  })
  class TestComponent {
    name = new FormControl();
  }

  beforeEach(() => {
    fixture = TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, GovukComponentsModule],
      declarations: [AsyncValidationFieldDirective, TestComponent],
    }).createComponent(TestComponent);

    fixture.detectChanges();
    directive = fixture.debugElement
      .query(By.directive(AsyncValidationFieldDirective))
      .injector.get(AsyncValidationFieldDirective);
  });

  it('should create an instance', () => {
    expect(directive).toBeTruthy();
  });

  it('should trigger change detector on status change', fakeAsync(() => {
    const cdRef = fixture.debugElement.injector.get(ChangeDetectorRef);
    const cdRefSpy = jest.spyOn(cdRef.constructor.prototype, 'markForCheck');

    fixture.componentInstance.name.markAsPending();
    expect(fixture.componentInstance.name.pending).toBeTruthy();
    expect(cdRefSpy).toHaveBeenCalled();

    fixture.componentInstance.name.setErrors({ invalid: true });
    expect(fixture.componentInstance.name.pending).toBeFalsy();
    expect(fixture.componentInstance.name.invalid).toBeTruthy();

    expect(cdRefSpy).toHaveBeenCalled();
  }));
});
