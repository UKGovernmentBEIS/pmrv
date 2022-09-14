import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormControl, FormGroup } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { SharedModule } from '@shared/shared.module';
import { mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { GovukValidators } from 'govuk-components';

import { RequestTaskAttachmentsHandlingService, TasksService } from 'pmrv-api';

import { AdditionalDocumentsSharedComponent } from './additional-documents.component';

describe('AdditionalDocumentsComponent', () => {
  let existControl: FormControl;
  let documentsControl: FormControl;
  let component: AdditionalDocumentsSharedComponent;
  let hostComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let element: HTMLElement;

  const tasksService = mockClass(TasksService);
  const attachmentService = mockClass(RequestTaskAttachmentsHandlingService);
  const requestTaskFileService = mockClass(RequestTaskFileService);

  @Component({
    template: `
      <app-additional-documents-shared
        [form]="form"
        [isEditable]="true"
        (formSubmit)="onSubmit()"
      ></app-additional-documents-shared>
    `,
  })
  class TestComponent {
    isEditable = true;
    form = new FormGroup({
      exist: new FormControl(undefined, {
        validators: [GovukValidators.required('Select Yes or No')],
        updateOn: 'change',
      }),
      documents: new FormControl([], {
        validators: [GovukValidators.required('Select a file')],
        updateOn: 'change',
      }),
    });
    onSubmit: (form: FormGroup) => any | jest.SpyInstance<void, [FormGroup]>;
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule],
      providers: [
        KeycloakService,
        { provide: TasksService, useValue: tasksService },
        { provide: RequestTaskAttachmentsHandlingService, useValue: attachmentService },
        { provide: RequestTaskFileService, useValue: requestTaskFileService },
      ],
      declarations: [TestComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    element = fixture.nativeElement;
    hostComponent = fixture.componentInstance;
    component = fixture.debugElement.query(By.directive(AdditionalDocumentsSharedComponent)).componentInstance;
    hostComponent.onSubmit = jest.fn();
    fixture.detectChanges();
    documentsControl = component.form.get('documents') as FormControl;
    existControl = component.form.get('exist') as FormControl;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the nested form', () => {
    expect(element.querySelector('.govuk-caption-l')).toBeTruthy();
    expect(element.querySelector('.govuk-heading-l')).toBeTruthy();
    expect(element.querySelector('button[type="submit"]')).toBeTruthy();
    expect(element.querySelectorAll('input').length).toEqual(3);
  });

  it('should render upload files component', () => {
    existControl.setValue(true);
    expect(element.querySelector('app-multiple-file-input')).toBeTruthy();
  });

  it('should not render errors on submit', () => {
    existControl.setValue(true);
    documentsControl.setValue([
      {
        file: new File(['test content'], 'cover.jpg'),
        uuid: '2c30c8bf-3d5e-474d-98a0-123a87eb60dd',
      },
      {
        file: new File(['test content'], 'PublicationAgreement.pdf'),
        uuid: '60fe9548-ac65-492a-b057-60033b0fbbea',
      },
    ]);

    expect(element.querySelector('.govuk-error-summary')).toBeFalsy();

    element.querySelector<HTMLButtonElement>('button[type="submit"]').click();

    fixture.detectChanges();

    expect(element.querySelector('.govuk-error-summary')).toBeFalsy();
    expect(hostComponent.onSubmit).toHaveBeenCalled();
  });

  it('should render errors on submit', () => {
    expect(element.querySelector('.govuk-error-summary')).toBeFalsy();

    element.querySelector<HTMLButtonElement>('button[type="submit"]').click();

    fixture.detectChanges();

    expect(element.querySelector('.govuk-error-summary')).toBeTruthy();
    expect(hostComponent.onSubmit).not.toHaveBeenCalled();
  });
});
