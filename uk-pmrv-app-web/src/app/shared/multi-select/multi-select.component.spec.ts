import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';

import { ErrorMessageComponent } from 'govuk-components';

import { BasePage } from '../../../testing';
import { MultiSelectComponent } from './multi-select.component';
import { MultiSelectItemComponent } from './multi-select-item/multi-select-item.component';

describe('MultiSelectComponent', () => {
  @Component({
    template: `
      <form [formGroup]="form">
        <div app-multi-select formControlName="test" label="Test label">
          <div app-multi-select-item itemValue="1" label="Test label 1"></div>
          <div app-multi-select-item itemValue="2" label="Test label 2"></div>
        </div>
      </form>
    `,
  })
  class TestComponent {
    form: FormGroup = new FormGroup({
      test: new FormControl(null),
    });
  }

  let component: MultiSelectComponent;
  let fixture: ComponentFixture<TestComponent>;
  let hostComponent: TestComponent;
  let page: Page;

  class Page extends BasePage<TestComponent> {
    get button() {
      return this.query<HTMLButtonElement>('button');
    }
    get checkboxes() {
      return this.queryAll<HTMLInputElement>('input');
    }
    get labels() {
      return this.queryAll<HTMLLabelElement>('label');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule],
      declarations: [TestComponent, MultiSelectItemComponent, MultiSelectComponent, ErrorMessageComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    hostComponent = fixture.componentInstance;
    component = fixture.debugElement.query(By.directive(MultiSelectComponent)).componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should assign value', () => {
    page.button.click();
    fixture.detectChanges();

    expect(page.checkboxes[0].checked).toBeFalsy();
    expect(page.checkboxes[1].checked).toBeFalsy();
    expect(page.button.textContent.trim()).toEqual('');

    hostComponent.form.get('test').setValue(['2']);
    fixture.detectChanges();

    expect(page.checkboxes[0].checked).toBeFalsy();
    expect(page.checkboxes[1].checked).toBeTruthy();
    expect(page.button.textContent.trim()).toEqual('1 selected');

    hostComponent.form.get('test').setValue(['1', '2']);
    fixture.detectChanges();

    expect(page.checkboxes[0].checked).toBeTruthy();
    expect(page.checkboxes[1].checked).toBeTruthy();
    expect(page.button.textContent.trim()).toEqual('2 selected');
  });

  it('should emit value', () => {
    expect(hostComponent.form.get('test').value).toEqual(null);

    page.button.click();
    fixture.detectChanges();
    page.checkboxes[0].click();

    expect(hostComponent.form.get('test').value).toEqual(['1']);

    page.checkboxes[0].click();
    page.checkboxes[1].click();

    expect(hostComponent.form.get('test').value).toEqual(['2']);

    page.checkboxes[0].click();

    expect(hostComponent.form.get('test').value).toEqual(['1', '2']);
  });

  it('should assign indexes to items', () => {
    page.button.click();
    fixture.detectChanges();

    expect(page.checkboxes.map((checkbox) => checkbox.id)).toEqual(['test-0', 'test-1']);
    expect(page.labels.map((checkbox) => checkbox.htmlFor)).toEqual(['test', 'test-0', 'test-1']);
  });
});
