import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ControlContainer, FormBuilder, FormGroup, FormGroupName } from '@angular/forms';
import { By } from '@angular/platform-browser';

import { NonSignificantChange } from 'pmrv-api';

import { BasePage } from '../../../../../../testing';
import { SharedModule } from '../../../../../shared/shared.module';
import { NonSignificantChangeComponent } from './non-significant-change.component';

describe('NonSignificantChangeComponent', () => {
  let component: NonSignificantChangeComponent;
  let fixture: ComponentFixture<TestComponent>;
  let hostComponent: TestComponent;
  let page: Page;

  @Component({
    template: `
      <form [formGroup]="form">
        <app-non-significant-change formGroupName="notification"></app-non-significant-change>
      </form>
    `,
    changeDetection: ChangeDetectionStrategy.OnPush,
  })
  class TestComponent {
    form = new FormGroup({
      notification: new FormBuilder().group(NonSignificantChangeComponent.controlsFactory(null)),
    });
  }

  const notification: NonSignificantChange = {
    description: 'description',
    relatedChanges: ['MONITORING_METHODOLOGY_PLAN'],
    type: 'NON_SIGNIFICANT_CHANGE',
  };

  class Page extends BasePage<TestComponent> {
    get labels() {
      return this.queryAll<HTMLLabelElement>('label');
    }
    get relatedChanges() {
      return this.queryAll<HTMLInputElement>('.govuk-checkboxes__input');
    }
    get textAreas() {
      return this.queryAll<HTMLTextAreaElement>('textarea');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule],
      declarations: [TestComponent, NonSignificantChangeComponent],
    })
      .overrideComponent(NonSignificantChangeComponent, {
        set: { providers: [{ provide: ControlContainer, useExisting: FormGroupName }] },
      })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    hostComponent = fixture.componentInstance;
    component = fixture.debugElement.query(By.directive(NonSignificantChangeComponent)).componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render form labels', () => {
    expect(page.labels.map((label) => label.textContent.trim())).toEqual([
      'Monitoring plan',
      'Monitoring methodology plan (MMP)',
      'Describe the change',
    ]);
  });

  it('should update values on setValue', () => {
    expect(page.relatedChanges.length).toEqual(2);
    page.relatedChanges.forEach((item) => expect(item.checked).toBe(false));

    page.relatedChanges[0].click();
    hostComponent.form.get('notification.description').setValue(notification.description);
    fixture.detectChanges();

    expect(page.relatedChanges[0].checked).toBe(true);
    expect(page.relatedChanges[1].checked).toBe(false);
    expect(page.textAreas.map((input) => input.value)).toEqual([notification.description]);
  });

  it('should apply field validations', () => {
    hostComponent.form.markAllAsTouched();
    fixture.detectChanges();

    expect(hostComponent.form.invalid).toBeTruthy();

    const description = hostComponent.form.get('notification.description');
    expect(description.errors).toEqual({ required: 'Enter description for the change' });
    hostComponent.form.get('notification.description').setValue('a'.repeat(10001));
    fixture.detectChanges();
    expect(description.errors).toEqual({
      maxlength: 'Enter up to 10000 characters',
    });

    const plan = hostComponent.form.get('notification.relatedChanges');
    expect(plan.errors).toEqual({ required: 'Select one or more' });
  });
});
