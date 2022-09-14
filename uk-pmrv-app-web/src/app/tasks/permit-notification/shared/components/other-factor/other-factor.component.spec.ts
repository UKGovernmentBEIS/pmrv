import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ControlContainer, FormBuilder, FormGroup, FormGroupName } from '@angular/forms';
import { By } from '@angular/platform-browser';

import { OtherFactor } from 'pmrv-api';

import { BasePage } from '../../../../../../testing';
import { SharedModule } from '../../../../../shared/shared.module';
import { OtherFactorComponent } from './other-factor.component';

describe('OtherFactorComponent', () => {
  let component: OtherFactorComponent;
  let fixture: ComponentFixture<TestComponent>;
  let hostComponent: TestComponent;
  let page: Page;

  @Component({
    template: `
      <form [formGroup]="form">
        <app-other-factor formGroupName="notification" [today]="today"></app-other-factor>
      </form>
    `,
    changeDetection: ChangeDetectionStrategy.OnPush,
  })
  class TestComponent {
    today = new Date();
    form = new FormGroup({
      notification: new FormBuilder().group(OtherFactorComponent.controlsFactory(null)),
    });
  }

  const notification: OtherFactor = {
    description: 'description',
    reportingType: 'EXCEEDED_THRESHOLD_STATED_GHGE_PERMIT',
    type: 'NON_SIGNIFICANT_CHANGE',
  };

  class Page extends BasePage<TestComponent> {
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
    get typeRadios() {
      return this.queryAll<HTMLInputElement>('input[name$="reportingType"]');
    }
    get textAreas() {
      return this.queryAll<HTMLTextAreaElement>('textarea');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TestComponent, OtherFactorComponent],
      imports: [SharedModule],
    })
      .overrideComponent(OtherFactorComponent, {
        set: { providers: [{ provide: ControlContainer, useExisting: FormGroupName }] },
      })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    hostComponent = fixture.componentInstance;
    component = fixture.debugElement.query(By.directive(OtherFactorComponent)).componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render form labels', () => {
    expect(page.labels.length).toEqual(17);
    expect(page.typeRadios.length).toEqual(4);
    expect(page.textAreas.length).toEqual(1);
  });

  it('should update values on setValue', () => {
    page.typeRadios[2].click();
    hostComponent.form.get('notification.description').setValue(notification.description);
    fixture.detectChanges();

    expect(page.typeRadios[0].checked).toBe(false);
    expect(page.typeRadios[1].checked).toBe(false);
    expect(page.typeRadios[2].checked).toBe(true);
    expect(page.typeRadios[3].checked).toBe(false);
    expect(page.textAreas.map((input) => input.value)).toEqual([notification.description]);
  });

  it('should apply field validations', () => {
    hostComponent.form.markAllAsTouched();
    fixture.detectChanges();

    expect(hostComponent.form.invalid).toBeTruthy();

    const description = hostComponent.form.get('notification.description');
    expect(description.errors).toEqual({ required: 'Enter details' });
    hostComponent.form.get('notification.description').setValue('a'.repeat(10001));
    fixture.detectChanges();
    expect(description.errors).toEqual({
      maxlength: 'Enter up to 10000 characters',
    });

    const reportingType = hostComponent.form.get('notification.reportingType');
    expect(reportingType.errors).toEqual({ required: 'Select one' });

    page.typeRadios[0].click();
    fixture.detectChanges();
    const startDateGHGE = hostComponent.form.get(
      'notification.startDateOfNonCompliance_EXCEEDED_THRESHOLD_STATED_GHGE_PERMIT',
    );
    expect(startDateGHGE.errors).toEqual({ required: 'Enter a date' });

    page.typeRadios[1].click();
    fixture.detectChanges();
    const startDateHSE = hostComponent.form.get(
      'notification.startDateOfNonCompliance_EXCEEDED_THRESHOLD_STATED_HSE_PERMIT',
    );
    expect(startDateHSE.errors).toEqual({ required: 'Enter a date' });

    const nowDay = new Date();
    page.typeRadios[3].click();
    fixture.detectChanges();
    page.endDateYear = nowDay.getFullYear() - 1 + '';
    page.endDateMonth = '1';
    page.endDateDay = '1';
    fixture.detectChanges();
    const endDateOfNonCompliance = hostComponent.form.get('notification.endDateOfNonCompliance');
    expect(endDateOfNonCompliance.errors).toBeTruthy();
    expect(endDateOfNonCompliance.errors['minDate']).toBeTruthy();
  });
});
