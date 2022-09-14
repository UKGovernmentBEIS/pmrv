import { HttpResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormControl } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { RequestTaskAttachmentsHandlingService, TasksService } from 'pmrv-api';

import { asyncData, BasePage, mockClass } from '../../../testing';
import { PermitApplicationModule } from '../permit-application.module';
import { PermitApplicationStore } from '../store/permit-application.store';
import { monitoringRolesPayload } from '../testing/mock-permit-apply-action';
import { setStoreTask } from '../testing/set-store-task';
import { MonitoringRolesComponent } from './monitoring-roles.component';

describe('MonitoringRolesComponent', () => {
  let component: MonitoringRolesComponent;
  let fixture: ComponentFixture<MonitoringRolesComponent>;
  let page: Page;
  const tasksService = mockClass(TasksService);
  const attachmentService = mockClass(RequestTaskAttachmentsHandlingService);
  let store: PermitApplicationStore;
  let control: FormControl;
  let router: Router;
  let activatedRoute: ActivatedRoute;

  class Page extends BasePage<MonitoringRolesComponent> {
    get roles() {
      return this.queryAll<HTMLFieldSetElement>('fieldset');
    }
    get jobTitles() {
      return this.queryAll<HTMLInputElement>('input[id^="monitoringRoles"][id$="jobTitle"]');
    }
    get files() {
      return (
        this.queryAll<HTMLSpanElement>('.moj-multi-file-upload__filename') ??
        this.queryAll<HTMLSpanElement>('.moj-multi-file-upload__success')
      );
    }
    get filesText() {
      return this.queryAll<HTMLDivElement>('.moj-multi-file-upload__message');
    }
    set fileValue(value: File[]) {
      this.setInputValue('input[type="file"]', value);
    }
    get fileDeleteButton() {
      return this.queryAll<HTMLButtonElement>('.moj-multi-file-upload__delete');
    }
    set jobTitleValues(values: string[]) {
      values.forEach((value, index) => this.setInputValue(`#monitoringRoles.${index}.jobTitle`, value));
    }
    set mainDutyValues(values: string[]) {
      values.forEach((value, index) => this.setInputValue(`#monitoringRoles.${index}.mainDuties`, value));
    }
    get addAnotherButton() {
      return this.query<HTMLButtonElement>('button.govuk-button--secondary:not(.moj-add-another__remove-button)');
    }
    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }
    get errors() {
      return this.queryAll<HTMLLIElement>('.govuk-error-summary__list > li');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(MonitoringRolesComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    control = component.form.get('organisationCharts') as FormControl;
    fixture.detectChanges();
  };

  beforeEach(async () => {
    tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
    attachmentService.uploadRequestTaskAttachmentUsingPOST.mockReturnValue(
      asyncData<any>(new HttpResponse({ body: { uuid: '60fe9548-ac65-492a-b057-60033b0fbbed' } })),
    );

    await TestBed.configureTestingModule({
      imports: [PermitApplicationModule, RouterTestingModule],
      providers: [
        { provide: TasksService, useValue: tasksService },
        { provide: RequestTaskAttachmentsHandlingService, useValue: attachmentService },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(PermitApplicationStore);
    store.setState({
      ...store.getState(),
      requestTaskId: 1,
      permitAttachments: { [monitoringRolesPayload.organisationCharts[0]]: 'some-file.txt' },
      isEditable: true,
    });
    setStoreTask('monitoringReporting', monitoringRolesPayload, [true]);

    fixture = TestBed.createComponent(MonitoringRolesComponent);
    component = fixture.componentInstance;
    control = component.form.get('organisationCharts') as FormControl;
    page = new Page(fixture);
    fixture.detectChanges();

    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display an empty form if no task has no value', () => {
    setStoreTask('monitoringReporting', undefined, [false]);
    createComponent();

    expect(page.roles).toHaveLength(1);
    expect(page.files).toHaveLength(0);
  });

  it('should require all fields to be populated', () => {
    page.jobTitleValues = ['', ''];
    page.mainDutyValues = ['', ''];
    fixture.detectChanges();

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeTruthy();
    expect(page.errors.map((error) => error.textContent.trim())).toEqual([
      'Enter a job title',
      'Enter the main duties',
      'Enter a job title',
      'Enter the main duties',
    ]);
    expect(tasksService.processRequestTaskActionUsingPOST).not.toHaveBeenCalled();
  });

  it('should populate with the existing data', () => {
    expect(page.jobTitles.map((role) => role.value)).toEqual(['Test job', 'Check job']);
    expect(page.files.map((span) => span.textContent)).toEqual(['some-file.txt']);
  });

  it('should add another role', () => {
    page.addAnotherButton.click();
    fixture.detectChanges();

    expect(page.roles).toHaveLength(3);
  });

  it('should validate, upload new files and submit the data', async () => {
    const navigateSpy = jest.spyOn(router, 'navigate');

    control.setValue([{ file: new File(['some content'], 'some-file.txt'), uuid: 'abc' }]);
    page.fileValue = [new File(['some content'], 'new-file.txt')];
    fixture.detectChanges();

    await fixture.whenStable();
    fixture.detectChanges();

    expect(page.filesText.map((row) => row.textContent.trim())).toEqual([
      'some-file.txt',
      'new-file.txt has been uploaded',
    ]);
    expect(page.fileDeleteButton.length).toEqual(2);

    page.submitButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith({
      requestTaskActionType: 'PERMIT_ISSUANCE_SAVE_APPLICATION',
      requestTaskId: 1,
      requestTaskActionPayload: {
        permitSectionsCompleted: { monitoringReporting: [true] },
        payloadType: 'PERMIT_ISSUANCE_SAVE_APPLICATION_PAYLOAD',
        permit: {
          ...store.permit,
          monitoringReporting: {
            ...monitoringRolesPayload,
            organisationCharts: ['abc', '60fe9548-ac65-492a-b057-60033b0fbbed'],
          },
        },
      },
    });

    expect(TestBed.inject(PermitApplicationStore).getValue().permitAttachments).toMatchObject({
      '60fe9548-ac65-492a-b057-60033b0fbbed': 'new-file.txt',
      abc: 'some-file.txt',
    });

    expect(navigateSpy).toHaveBeenCalledWith(['summary'], {
      relativeTo: activatedRoute,
      state: { notification: true },
    });
  });

  it('should submit the form', () => {
    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errors).toHaveLength(0);
    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith({
      requestTaskActionPayload: {
        permit: {
          ...store.permit,
          monitoringReporting: monitoringRolesPayload,
        },
        permitSectionsCompleted: { monitoringReporting: [true] },
        payloadType: 'PERMIT_ISSUANCE_SAVE_APPLICATION_PAYLOAD',
      },
      requestTaskActionType: 'PERMIT_ISSUANCE_SAVE_APPLICATION',
      requestTaskId: 1,
    });
    expect(store.getState().permitAttachments).toEqual({
      [monitoringRolesPayload.organisationCharts[0]]: 'some-file.txt',
    });
  });

  it('should submit a form without a file', () => {
    page.fileDeleteButton[0].click();
    fixture.detectChanges();
    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errors).toHaveLength(0);
    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith({
      requestTaskActionPayload: {
        permit: {
          ...store.permit,
          monitoringReporting: {
            ...monitoringRolesPayload,
            organisationCharts: [],
          },
        },
        permitSectionsCompleted: { monitoringReporting: [true] },
        payloadType: 'PERMIT_ISSUANCE_SAVE_APPLICATION_PAYLOAD',
      },
      requestTaskActionType: 'PERMIT_ISSUANCE_SAVE_APPLICATION',
      requestTaskId: 1,
    });
  });
});
