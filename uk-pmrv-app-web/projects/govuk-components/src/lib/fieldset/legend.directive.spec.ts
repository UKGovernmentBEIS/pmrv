import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';

import { FieldsetDirective } from './fieldset.directive';
import { LegendDirective } from './legend.directive';

describe('LegendDirective', () => {
  let directive: LegendDirective;
  let fixture: ComponentFixture<TestComponent>;
  let element: HTMLElement;

  @Component({
    template: `
      <fieldset govukFieldset>
        <legend govukLegend>Legend</legend>
      </fieldset>
      <fieldset govukFieldset>
        <legend govukLegend size="heading">
          <h1>Legend</h1>
        </legend>
      </fieldset>
    `,
  })
  class TestComponent {}

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [FieldsetDirective, LegendDirective, TestComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    directive = fixture.debugElement.query(By.directive(LegendDirective)).componentInstance;
    element = fixture.nativeElement;
    fixture.detectChanges();
  });

  it('should create an instance', () => {
    expect(directive).toBeTruthy();
  });

  it('should have the fieldset class', () => {
    expect(element.querySelector('legend').classList.contains('govuk-fieldset__legend')).toBeTruthy();
    expect(element.querySelector('legend').classList.contains('govuk-fieldset__legend--m')).toBeTruthy();
  });

  it('should assign the heading class to headings on heading size', () => {
    expect(element.querySelectorAll('legend')[1].classList.contains('govuk-fieldset__legend--l')).toBeTruthy();
    expect(element.querySelector('h1').classList.contains('govuk-fieldset__heading')).toBeTruthy();
  });
});
