import { HttpResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormControl } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { asyncData, BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { RequestTaskAttachmentsHandlingService, TasksService } from 'pmrv-api';

import { PermitApplicationModule } from '../permit-application.module';
import { PermitApplicationStore } from '../store/permit-application.store';
import { mockPostBuild, mockState } from '../testing/mock-state';
import { AdditionalDocumentsComponent } from './additional-documents.component';

describe('AdditionalDocumentsComponent', () => {
  let component: AdditionalDocumentsComponent;
  let fixture: ComponentFixture<AdditionalDocumentsComponent>;
  let page: Page;
  let router: Router;
  let activatedRoute: ActivatedRoute;
  let control: FormControl;
  let store: PermitApplicationStore;

  const tasksService = mockClass(TasksService);
  const attachmentService = mockClass(RequestTaskAttachmentsHandlingService);

  class Page extends BasePage<AdditionalDocumentsComponent> {
    get existRadios() {
      return this.queryAll<HTMLInputElement>('input[name$="exist"]');
    }

    get filesHeader() {
      return this.queryAll<HTMLElement>('h2');
    }

    get files() {
      return (
        this.queryAll<HTMLSpanElement>('.moj-multi-file-upload__filename') ??
        this.queryAll<HTMLSpanElement>('.moj-multi-file-upload__success')
      );
    }

    set fileValue(value: File[]) {
      this.setInputValue('input[type="file"]', value);
    }

    get fileInput() {
      return this.query<HTMLInputElement>('input[type="file"]');
    }

    get filesText() {
      return this.queryAll<HTMLDivElement>('.moj-multi-file-upload__message');
    }

    get fileDeleteButtons() {
      return this.queryAll<HTMLButtonElement>('.moj-multi-file-upload__delete');
    }

    get fileList() {
      return this.query<HTMLDivElement>('.moj-multi-file__uploaded-files');
    }

    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get errors() {
      return Array.from(this.errorSummary.querySelectorAll('a'));
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  beforeEach(async () => {
    tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
    attachmentService.uploadRequestTaskAttachmentUsingPOST.mockReturnValue(
      asyncData<any>(new HttpResponse({ body: { uuid: '60fe9548-ac65-492a-b057-60033b0fbbed' } })),
    );

    await TestBed.configureTestingModule({
      imports: [PermitApplicationModule, RouterTestingModule],
      providers: [
        KeycloakService,
        { provide: TasksService, useValue: tasksService },
        { provide: RequestTaskAttachmentsHandlingService, useValue: attachmentService },
      ],
    }).compileComponents();
    store = TestBed.inject(PermitApplicationStore);
    store.setState(mockState);
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AdditionalDocumentsComponent);
    component = fixture.componentInstance;
    control = component.form.get('documents') as FormControl;
    page = new Page(fixture);
    fixture.detectChanges();
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display an non empty form', () => {
    expect(page.existRadios.some((radio) => radio.checked)).toBeTruthy();
    expect(page.files).toHaveLength(2);
    expect(page.fileList.classList).not.toContain('moj-hidden');
  });

  it('should not display the file input', () => {
    page.existRadios[0].click();
    fixture.detectChanges();
    page.existRadios[1].click();
    fixture.detectChanges();

    expect(page.fileInput.disabled).toBeTruthy();
  });

  it('should validate, upload new files and submit the data', async () => {
    const navigateSpy = jest.spyOn(router, 'navigate');

    page.existRadios[0].click();
    fixture.detectChanges();
    control.setValue([{ file: new File(['test content'], 'cover.jpg'), uuid: '2c30c8bf-3d5e-474d-98a0-123a87eb60dd' }]);
    page.fileValue = [new File(['test content'], 'new-file.txt')];
    fixture.detectChanges();

    await fixture.whenStable();
    fixture.detectChanges();

    expect(page.filesText.map((row) => row.textContent.trim())).toEqual([
      'cover.jpg',
      'new-file.txt has been uploaded',
    ]);

    expect(page.fileDeleteButtons.length).toEqual(2);

    page.submitButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
      mockPostBuild(
        {
          additionalDocuments: {
            exist: true,
            documents: ['2c30c8bf-3d5e-474d-98a0-123a87eb60dd', '60fe9548-ac65-492a-b057-60033b0fbbed'],
          },
        },
        { additionalDocuments: [true] },
      ),
    );
    expect(TestBed.inject(PermitApplicationStore).getValue().permitAttachments).toMatchObject({
      '60fe9548-ac65-492a-b057-60033b0fbbed': 'new-file.txt',
    });
    expect(navigateSpy).toHaveBeenCalledWith(['summary'], {
      relativeTo: activatedRoute,
      state: { notification: true },
    });
  });

  it('should display the file input with no existing files', () => {
    control.setValue(null);
    page.existRadios[0].click();
    fixture.detectChanges();

    expect(page.filesHeader[0].classList).not.toContain('govuk-visually-hidden');
    expect(page.fileDeleteButtons.length).toEqual(0);
    expect(page.filesHeader[0].textContent).toEqual('Files added');
    expect(page.fileInput.disabled).toBeFalsy();
    expect(page.fileList.classList).toContain('moj-hidden');
  });

  it('should require all fields to be populated', () => {
    control.setValue(null);
    component.form.get('exist').setValue(null);
    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeTruthy();
    expect(page.errors.map((error) => error.textContent.trim())).toEqual(['Select Yes or No']);

    page.existRadios[0].click();
    fixture.detectChanges();
    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeTruthy();
    expect(page.errors.map((error) => error.textContent.trim())).toEqual(['Select a file']);
  });
});
