import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';

import { FieldsetDirective } from './fieldset.directive';
import { FieldsetHintDirective } from './fieldset-hint.directive';

describe('FieldsetDirective', () => {
  let directive: FieldsetDirective;
  let fixture: ComponentFixture<TestComponent>;
  let element: HTMLElement;

  @Component({
    template: `
      <fieldset govukFieldset>
        <legend>Legend</legend>
        <span govukFieldsetHint>Hint</span>
      </fieldset>
      <fieldset govukFieldset>
        <legend>Legend</legend>
      </fieldset>
    `,
  })
  class TestComponent {}

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [FieldsetDirective, FieldsetHintDirective, TestComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    directive = fixture.debugElement.query(By.directive(FieldsetDirective)).componentInstance;
    element = fixture.nativeElement;
    fixture.detectChanges();
  });

  it('should create an instance', () => {
    expect(directive).toBeTruthy();
  });

  it('should set id and aria attributes', () => {
    const fieldset = element.querySelector('fieldset');
    expect(fieldset.id).toEqual('fieldset');
    expect(fieldset.getAttribute('aria-describedby')).toEqual('fieldset-hint');
    expect(element.querySelector('span').id).toEqual('fieldset-hint');
  });

  it('should set the fieldset class', () => {
    expect(element.querySelector('fieldset').classList.contains('govuk-fieldset')).toBeTruthy();
  });

  it('should not set described by attribute if there is no hint', () => {
    const fieldset = element.querySelectorAll('fieldset')[1];
    expect(fieldset.getAttribute('aria-describedby')).toBeFalsy();
  });
});
