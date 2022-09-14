import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';

import { AddAnotherDirective } from './add-another.directive';

describe('AddAnotherDirective', () => {
  let directive: AddAnotherDirective;
  let fixture: ComponentFixture<TestComponent>;
  let element: HTMLElement;

  @Component({
    template: `
      <div>
        <h1 #ref>Heading</h1>
        <fieldset *ngFor="let fieldset of fieldsets; index as i">
          <legend>Legend</legend>
          <button type="button" appAddAnother [heading]="ref" (click)="fieldsets.splice(i, 1)">Remove</button>
        </fieldset>

        <button type="button" (click)="fieldsets.push(null)" id="add">Add another</button>
      </div>
    `,
  })
  class TestComponent {
    fieldsets = Array(1);
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AddAnotherDirective, TestComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    element = fixture.nativeElement;
    fixture.detectChanges();
    directive = fixture.debugElement.query(By.directive(AddAnotherDirective)).componentInstance;
  });

  it('should create an instance', () => {
    expect(directive).toBeTruthy();
  });

  it('should add moj classes to the button, fieldset, legend, and the heading', () => {
    const addButton = element.querySelector<HTMLButtonElement>('button#add');
    addButton.click();
    fixture.detectChanges();

    expect(element.querySelector('h1.moj-add-another__heading')).toBeTruthy();
    expect(element.querySelectorAll('button.moj-add-another__remove-button')).toHaveLength(2);
    expect(element.querySelectorAll('fieldset.moj-add-another__item')).toHaveLength(2);
    expect(element.querySelectorAll('legend.moj-add-another__title')).toHaveLength(2);
  });

  it('should focus the heading when the fieldset is removed', () => {
    const removeButton = element.querySelector<HTMLButtonElement>('fieldset > button');
    removeButton.click();
    fixture.detectChanges();

    expect(element.querySelectorAll('fieldset')).toHaveLength(0);
    expect(document.activeElement).toBe(element.querySelector('h1'));
  });
});
