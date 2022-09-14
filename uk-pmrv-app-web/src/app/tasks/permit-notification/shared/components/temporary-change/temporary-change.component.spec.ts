import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ControlContainer, FormBuilder, FormGroup, FormGroupName } from '@angular/forms';
import { By } from '@angular/platform-browser';

import { TemporaryChange } from 'pmrv-api';

import { BasePage } from '../../../../../../testing';
import { SharedModule } from '../../../../../shared/shared.module';
import { TemporaryChangeComponent } from './temporary-change.component';

describe('TemporaryChangeComponent', () => {
  let component: TemporaryChangeComponent;
  let fixture: ComponentFixture<TestComponent>;
  let hostComponent: TestComponent;
  let page: Page;

  @Component({
    template: `
      <form [formGroup]="form">
        <app-temporary-change formGroupName="notification" [today]="today"></app-temporary-change>
      </form>
    `,
    changeDetection: ChangeDetectionStrategy.OnPush,
  })
  class TestComponent {
    today = new Date();
    form = new FormGroup({
      notification: new FormBuilder().group(TemporaryChangeComponent.controlsFactory(null)),
    });
  }

  const notification: TemporaryChange = {
    description: 'description',
    justification: 'justification',
    type: 'TEMPORARY_CHANGE',
  };

  class Page extends BasePage<TestComponent> {
    set startDateDay(value: string) {
      this.setInputValue('#notification.startDateOfNonCompliance-day', value);
    }
    set startDateMonth(value: string) {
      this.setInputValue('#notification.startDateOfNonCompliance-month', value);
    }
    set startDateYear(value: string) {
      this.setInputValue('#notification.startDateOfNonCompliance-year', value);
    }
    set endDateDay(value: string) {
      this.setInputValue('#notification.endDateOfNonCompliance-day', value);
    }
    set endDateMonth(value: string) {
      this.setInputValue('#notification.endDateOfNonCompliance-month', value);
    }
    set endDateYear(value: string) {
      this.setInputValue('#notification.endDateOfNonCompliance-year', value);
    }

    get labels() {
      return this.queryAll<HTMLLabelElement>('label');
    }
    get inputs() {
      return this.queryAll<HTMLInputElement>('input');
    }
    get textAreas() {
      return this.queryAll<HTMLTextAreaElement>('textarea');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule],
      declarations: [TestComponent, TemporaryChangeComponent],
    })
      .overrideComponent(TemporaryChangeComponent, {
        set: { providers: [{ provide: ControlContainer, useExisting: FormGroupName }] },
      })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    hostComponent = fixture.componentInstance;
    component = fixture.debugElement.query(By.directive(TemporaryChangeComponent)).componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render form labels', () => {
    expect(page.labels.map((label) => label.textContent.trim())).toEqual([
      'Describe the temporary change',
      'Justification for not submitting a permit variation',
      'Day',
      'Month',
      'Year',
      'Day',
      'Month',
      'Year',
    ]);
  });

  it('should update values on setValue', () => {
    const nowDay = new Date();
    const futureDate = new Date();
    futureDate.setFullYear(nowDay.getFullYear() + 1);

    hostComponent.form.get('notification.description').setValue(notification.description);
    hostComponent.form.get('notification.justification').setValue(notification.justification);
    hostComponent.form.get('notification.startDateOfNonCompliance').setValue(nowDay);
    hostComponent.form.get('notification.endDateOfNonCompliance').setValue(futureDate);
    fixture.detectChanges();

    expect(page.inputs.map((input) => input.value)).toEqual([
      nowDay.getDate().toString(),
      (nowDay.getMonth() + 1).toString(),
      nowDay.getFullYear().toString(),
      futureDate.getDate().toString(),
      (futureDate.getMonth() + 1).toString(),
      futureDate.getFullYear().toString(),
    ]);

    expect(page.textAreas.map((input) => input.value)).toEqual([notification.description, notification.justification]);
  });

  it('should apply field validations', () => {
    const nowDay = new Date();

    hostComponent.form.markAllAsTouched();
    fixture.detectChanges();

    expect(hostComponent.form.invalid).toBeTruthy();

    const description = hostComponent.form.get('notification.description');
    expect(description.errors).toEqual({ required: 'Enter a description for the temporary change' });
    hostComponent.form.get('notification.description').setValue('a'.repeat(10001));
    fixture.detectChanges();
    expect(description.errors).toEqual({
      maxlength: 'Enter up to 10000 characters',
    });

    const justification = hostComponent.form.get('notification.justification');
    expect(justification.errors).toEqual({ required: 'Enter a justification for not submitting a permit variation' });
    hostComponent.form.get('notification.justification').setValue('a'.repeat(10001));
    fixture.detectChanges();
    expect(justification.errors).toEqual({
      maxlength: 'Enter up to 10000 characters',
    });

    const startDate = hostComponent.form.get('notification.startDateOfNonCompliance');
    expect(startDate.errors).toEqual({ required: 'Enter a date' });

    const endDate = hostComponent.form.get('notification.endDateOfNonCompliance');
    expect(endDate.errors).toEqual({ required: 'Enter the end date of the temporary change' });

    page.endDateYear = nowDay.getFullYear() - 1 + '';
    page.endDateMonth = '1';
    page.endDateDay = '1';
    fixture.detectChanges();
    const endDateOfNonCompliance = hostComponent.form.get('notification.endDateOfNonCompliance');
    expect(endDateOfNonCompliance.errors).toBeTruthy();
    expect(endDateOfNonCompliance.errors['minDate']).toBeTruthy();
  });
});
