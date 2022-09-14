import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';

import { RadioOptionComponent } from './radio-option.component';

describe('RadioOptionComponent', () => {
  let component: RadioOptionComponent;
  let hostComponent: TestComponent;
  let element: HTMLElement;
  let fixture: ComponentFixture<TestComponent>;

  @Component({
    template: `
      <form [formGroup]="testFormGroup">
        <div app-radio-option index="0" value="pinball" formControlName="testControl" [isDisabled]="disable1">
          <ng-container label>
            <span class="govuk-visually-hidden">hidden</span>
          </ng-container>
        </div>
        <div app-radio-option index="15a" value="fantasies" formControlName="testControl" [isDisabled]="disable2"></div>
        <div app-radio-option index="2" value="21" formControlName="testControl" [isDisabled]="disable3"></div>
      </form>
    `,
  })
  class TestComponent {
    testFormGroup = new FormGroup({ testControl: new FormControl('pinball') });
    disable1: boolean;
    disable2: boolean;
    disable3: boolean;
  }

  const getOptions = () => element.querySelectorAll<HTMLInputElement>(`input`);

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule],
      declarations: [TestComponent, RadioOptionComponent],
    });
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    component = fixture.debugElement.query(By.directive(RadioOptionComponent)).componentInstance;
    hostComponent = fixture.componentInstance;
    element = fixture.nativeElement;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should write value', () => {
    const options = getOptions();

    expect(options[0].checked).toBeTruthy();
    expect(options[1].checked).toBeFalsy();
    expect(options[2].checked).toBeFalsy();

    hostComponent.testFormGroup.get('testControl').setValue('fantasies');

    expect(options[0].checked).toBeFalsy();
    expect(options[1].checked).toBeTruthy();
    expect(options[2].checked).toBeFalsy();
  });

  it('should update value', () => {
    expect(hostComponent.testFormGroup.get('testControl').value).toEqual('pinball');

    const options = getOptions();
    options[2].click();

    expect(options[0].checked).toBeFalsy();
    expect(options[1].checked).toBeFalsy();
    expect(options[2].checked).toBeTruthy();
    expect(hostComponent.testFormGroup.get('testControl').value).toEqual('21');

    options[1].click();

    expect(options[0].checked).toBeFalsy();
    expect(options[1].checked).toBeTruthy();
    expect(options[2].checked).toBeFalsy();
    expect(hostComponent.testFormGroup.get('testControl').value).toEqual('fantasies');
  });

  it('should disable an option', () => {
    const options = getOptions();

    expect(options[0].disabled).toBeFalsy();
    expect(options[1].disabled).toBeFalsy();
    expect(options[2].disabled).toBeFalsy();

    hostComponent.disable1 = true;
    fixture.detectChanges();

    expect(options[0].disabled).toBeTruthy();
    expect(options[1].disabled).toBeFalsy();
    expect(options[2].disabled).toBeFalsy();

    hostComponent.disable2 = true;
    fixture.detectChanges();

    expect(options[0].disabled).toBeTruthy();
    expect(options[1].disabled).toBeTruthy();
    expect(options[2].disabled).toBeFalsy();

    hostComponent.disable1 = false;
    hostComponent.disable2 = false;
    hostComponent.disable3 = true;
    fixture.detectChanges();

    expect(options[0].disabled).toBeFalsy();
    expect(options[1].disabled).toBeFalsy();
    expect(options[2].disabled).toBeTruthy();
  });

  it('should render hiden labels', () => {
    const element: HTMLElement = fixture.nativeElement;
    expect(element.querySelector('.govuk-visually-hidden').textContent).toEqual('hidden');
  });
});
