import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ControlContainer, FormBuilder, FormGroup, FormGroupName } from '@angular/forms';
import { By } from '@angular/platform-browser';

import { AppliedStandard } from 'pmrv-api';

import { BasePage } from '../../../../testing';
import { SharedModule } from '../../../shared/shared.module';
import { AppliedStandardFormComponent } from './applied-standard-form.component';

describe('AppliedStandardFormComponent', () => {
  let component: AppliedStandardFormComponent;
  let fixture: ComponentFixture<TestComponent>;
  let hostComponent: TestComponent;
  let page: Page;

  @Component({
    template: `
      <form [formGroup]="form">
        <app-applied-standard-form formGroupName="appliedStandard"></app-applied-standard-form>
      </form>
    `,
  })
  class TestComponent {
    form = new FormGroup({
      appliedStandard: new FormBuilder().group(AppliedStandardFormComponent.controlsFactory(null)),
    });
  }

  const appliedStandard: AppliedStandard = {
    parameter: 'param1',
    appliedStandard: 'standard1',
    deviationFromAppliedStandardExist: true,
    deviationFromAppliedStandardDetails: 'deviation1',
    laboratoryName: 'lab1',
    laboratoryAccredited: false,
    laboratoryAccreditationEvidence: 'labEvidence',
  };

  class Page extends BasePage<TestComponent> {
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
      declarations: [TestComponent, AppliedStandardFormComponent],
    })
      .overrideComponent(AppliedStandardFormComponent, {
        set: { providers: [{ provide: ControlContainer, useExisting: FormGroupName }] },
      })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    hostComponent = fixture.componentInstance;
    component = fixture.debugElement.query(By.directive(AppliedStandardFormComponent)).componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render applied standard form labels', () => {
    expect(page.labels.map((label) => label.textContent.trim())).toEqual([
      'Parameter',
      'What applied standard are you using?',
      'Yes',
      'Provide details',
      'No',
      'Laboratory name',
      'Yes',
      'No',
      'Provide evidence',
    ]);
  });

  it('should update values on setValue', () => {
    hostComponent.form.get('appliedStandard.parameter').setValue(appliedStandard.parameter);
    hostComponent.form.get('appliedStandard.appliedStandard').setValue(appliedStandard.appliedStandard);
    hostComponent.form
      .get('appliedStandard.deviationFromAppliedStandardExist')
      .setValue(appliedStandard.deviationFromAppliedStandardExist);
    hostComponent.form
      .get('appliedStandard.deviationFromAppliedStandardDetails')
      .setValue(appliedStandard.deviationFromAppliedStandardDetails);
    hostComponent.form.get('appliedStandard.laboratoryName').setValue(appliedStandard.laboratoryName);
    hostComponent.form.get('appliedStandard.laboratoryAccredited').setValue(appliedStandard.laboratoryAccredited);
    hostComponent.form
      .get('appliedStandard.laboratoryAccreditationEvidence')
      .setValue(appliedStandard.laboratoryAccreditationEvidence);
    fixture.detectChanges();

    expect(page.inputs.map((input) => input.value)).toEqual([
      appliedStandard.parameter,
      appliedStandard.appliedStandard,
      String(appliedStandard.deviationFromAppliedStandardExist),
      String(appliedStandard.deviationFromAppliedStandardDetails),
      String(false),
      appliedStandard.laboratoryName,
      String(true),
      String(appliedStandard.laboratoryAccredited),
    ]);
    expect(page.textAreas.map((input) => input.value)).toEqual([appliedStandard.laboratoryAccreditationEvidence]);
  });

  it('should apply field validations', () => {
    hostComponent.form.markAllAsTouched();
    fixture.detectChanges();

    expect(hostComponent.form.invalid).toBeTruthy();

    const parameter = hostComponent.form.get('appliedStandard.parameter');
    expect(parameter.errors).toEqual({ required: 'Enter a parameter' });
    hostComponent.form.get('appliedStandard.parameter').setValue('a'.repeat(251));
    fixture.detectChanges();
    expect(parameter.errors).toEqual({
      maxlength: 'Enter up to 250 characters',
    });
  });
});
