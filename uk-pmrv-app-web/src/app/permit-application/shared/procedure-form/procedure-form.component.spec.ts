import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ControlContainer, FormBuilder, FormGroup, FormGroupName } from '@angular/forms';
import { By } from '@angular/platform-browser';

import { ProcedureForm } from 'pmrv-api';

import { BasePage } from '../../../../testing';
import { SharedModule } from '../../../shared/shared.module';
import { ProcedureFormComponent } from './procedure-form.component';

describe('ProcedureFormComponent', () => {
  let component: ProcedureFormComponent;
  let fixture: ComponentFixture<TestComponent>;
  let hostComponent: TestComponent;
  let page: Page;

  @Component({
    template: `
      <form [formGroup]="form">
        <app-procedure-form formGroupName="procedureForm"></app-procedure-form>
      </form>
    `,
  })
  class TestComponent {
    form = new FormGroup({
      procedureForm: new FormBuilder().group(ProcedureFormComponent.controlsFactory(null)),
    });
  }

  const procedure: ProcedureForm = {
    appliedStandards: 'appliedStandards2',
    diagramReference: 'diagramReference2',
    itSystemUsed: 'itSystemUsed2',
    locationOfRecords: 'locationOfRecords2',
    procedureDescription: 'procedureDescription2',
    procedureDocumentName: 'procedureDocumentName2',
    procedureReference: 'procedureReference2',
    responsibleDepartmentOrRole: 'responsibleDepartmentOrRole2',
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
      declarations: [TestComponent, ProcedureFormComponent],
    })
      .overrideComponent(ProcedureFormComponent, {
        set: { providers: [{ provide: ControlContainer, useExisting: FormGroupName }] },
      })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    hostComponent = fixture.componentInstance;
    component = fixture.debugElement.query(By.directive(ProcedureFormComponent)).componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render procedure form labels', () => {
    expect(page.labels.map((label) => label.textContent.trim())).toEqual([
      'Procedure description',
      'Name of the procedure document',
      'Procedure reference',
      'Diagram reference (optional)',
      'Department or role that’s responsible for the procedure and the data generated',
      'Location of records',
      'IT system used (optional)',
      'European or other standards applied (optional)',
    ]);
  });

  it('should update values on setValue', () => {
    hostComponent.form.get('procedureForm.procedureDescription').setValue(procedure.procedureDescription);
    hostComponent.form.get('procedureForm.procedureDocumentName').setValue(procedure.procedureDocumentName);
    hostComponent.form.get('procedureForm.procedureReference').setValue(procedure.procedureReference);
    hostComponent.form.get('procedureForm.diagramReference').setValue(procedure.diagramReference);
    hostComponent.form.get('procedureForm.responsibleDepartmentOrRole').setValue(procedure.responsibleDepartmentOrRole);
    hostComponent.form.get('procedureForm.locationOfRecords').setValue(procedure.locationOfRecords);
    hostComponent.form.get('procedureForm.itSystemUsed').setValue(procedure.itSystemUsed);
    hostComponent.form.get('procedureForm.appliedStandards').setValue(procedure.appliedStandards);
    fixture.detectChanges();

    expect(page.inputs.map((input) => input.value)).toEqual([
      procedure.procedureDocumentName,
      procedure.procedureReference,
      procedure.diagramReference,
      procedure.responsibleDepartmentOrRole,
      procedure.locationOfRecords,
      procedure.itSystemUsed,
      procedure.appliedStandards,
    ]);
    expect(page.textAreas.map((input) => input.value)).toEqual([procedure.procedureDescription]);
  });

  it('should apply field validations', () => {
    hostComponent.form.markAllAsTouched();
    fixture.detectChanges();

    expect(hostComponent.form.invalid).toBeTruthy();

    const procedureDescription = hostComponent.form.get('procedureForm.procedureDescription');
    expect(procedureDescription.errors).toEqual({ required: 'Enter a brief description of the procedure' });
    hostComponent.form.get('procedureForm.procedureDescription').setValue('a'.repeat(10001));
    fixture.detectChanges();
    expect(procedureDescription.errors).toEqual({
      maxlength: 'The procedure description should not be more than 10000 characters',
    });

    const procedureDocumentName = hostComponent.form.get('procedureForm.procedureDocumentName');
    expect(procedureDocumentName.errors).toEqual({ required: 'Enter the name of the procedure document' });
    hostComponent.form.get('procedureForm.procedureDocumentName').setValue('a'.repeat(1001));
    fixture.detectChanges();
    expect(procedureDocumentName.errors).toEqual({
      maxlength: 'The procedure document name should not be more than 1000 characters',
    });

    const procedureReference = hostComponent.form.get('procedureForm.procedureReference');
    expect(procedureReference.errors).toEqual({ required: 'Enter a procedure reference' });
    hostComponent.form.get('procedureForm.procedureReference').setValue('a'.repeat(501));
    fixture.detectChanges();
    expect(procedureReference.errors).toEqual({
      maxlength: 'The procedure reference should not be more than 500 characters',
    });

    const diagramReference = hostComponent.form.get('procedureForm.diagramReference');
    expect(diagramReference.errors).toBeFalsy();
    hostComponent.form.get('procedureForm.diagramReference').setValue('a'.repeat(501));
    fixture.detectChanges();
    expect(diagramReference.errors).toEqual({
      maxlength: 'The diagram reference should not be more than 500 characters',
    });

    const responsibleDepartmentOrRole = hostComponent.form.get('procedureForm.responsibleDepartmentOrRole');
    expect(responsibleDepartmentOrRole.errors).toEqual({
      required: 'Enter the name of the department or role responsible',
    });
    hostComponent.form.get('procedureForm.responsibleDepartmentOrRole').setValue('a'.repeat(1001));
    fixture.detectChanges();
    expect(responsibleDepartmentOrRole.errors).toEqual({
      maxlength: 'The name of the department or role responsible should not be more than 1000 characters',
    });

    const locationOfRecords = hostComponent.form.get('procedureForm.locationOfRecords');
    expect(locationOfRecords.errors).toEqual({ required: 'Enter the location of the records' });
    hostComponent.form.get('procedureForm.locationOfRecords').setValue('a'.repeat(2001));
    fixture.detectChanges();
    expect(locationOfRecords.errors).toEqual({
      maxlength: 'The location of the records should not be more than 2000 characters',
    });

    const itSystemUsed = hostComponent.form.get('procedureForm.itSystemUsed');
    expect(itSystemUsed.errors).toBeFalsy();
    hostComponent.form.get('procedureForm.itSystemUsed').setValue('a'.repeat(501));
    fixture.detectChanges();
    expect(itSystemUsed.errors).toEqual({
      maxlength: 'The IT system used should not be more than 500 characters',
    });

    const appliedStandards = hostComponent.form.get('procedureForm.appliedStandards');
    expect(appliedStandards.errors).toBeFalsy();
    hostComponent.form.get('procedureForm.appliedStandards').setValue('a'.repeat(2001));
    fixture.detectChanges();
    expect(appliedStandards.errors).toEqual({
      maxlength: 'The applied standards should not be more than 2000 characters',
    });
  });
});
