import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';

import { SelectComponent } from './select.component';

describe('SelectComponent', () => {
  @Component({
    template: `
      <div
        govuk-select
        [options]="[
          { text: 'First', value: 1 },
          { text: 'Second', value: 2 }
        ]"
        [formControl]="control"
      >
        <option [ngValue]="3">Third</option>
      </div>
    `,
  })
  class TestComponent {
    control = new FormControl();
  }

  let component: SelectComponent;
  let hostComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let element: HTMLElement;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule],
      declarations: [SelectComponent, TestComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    hostComponent = fixture.componentInstance;
    component = fixture.debugElement.query(By.directive(SelectComponent)).componentInstance;
    element = fixture.nativeElement;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the options', () => {
    const options = element.querySelectorAll('option');
    expect(options).toHaveLength(3);
    expect(options[0].textContent.trim()).toEqual('First');
    expect(options[1].textContent.trim()).toEqual('Second');
    expect(options[2].textContent.trim()).toEqual('Third');
  });

  it('should disable the select', () => {
    hostComponent.control.disable();
    fixture.detectChanges();

    const select = element.querySelector<HTMLSelectElement>('select');

    expect(select.disabled).toBeTruthy();
  });

  it('should assign value', () => {
    hostComponent.control.patchValue(1);
    fixture.detectChanges();

    const select = element.querySelector<HTMLSelectElement>('select');
    expect(select.value).toEqual('0: 1');
  });

  it('emit value', () => {
    const select = element.querySelector<HTMLSelectElement>('select');
    select.value = select.options[0].value;
    select.dispatchEvent(new Event('change'));
    fixture.detectChanges();

    expect(hostComponent.control.value).toEqual(1);
  });
});
