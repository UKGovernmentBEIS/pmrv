import { HttpErrorResponse, HttpEvent, HttpEventType, HttpResponse, HttpStatusCode } from '@angular/common/http';
import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { first, mergeMapTo, Subject, throwError } from 'rxjs';

import { GovukComponentsModule } from 'govuk-components';

import { FileUuidDTO } from 'pmrv-api';

import { BasePage } from '../../../testing';
import { FileUploadListComponent } from '../file-upload-list/file-upload-list.component';
import { FileInputComponent } from './file-input.component';
import { FileUploadService } from './file-upload.service';
import { FileValidators } from './file-validators';

describe('FileInputComponent', () => {
  let component: FileInputComponent;
  let fixture: ComponentFixture<TestComponent>;
  let hostComponent: TestComponent;
  let page: Page;
  let control: FormControl;

  @Component({
    template: `
      <form [formGroup]="form">
        <app-file-input formControlName="file" [downloadUrl]="getDownloadUrl"></app-file-input>
      </form>
    `,
  })
  class TestComponent {
    form = new FormGroup({ file: new FormControl({ file: new File(['abc'], 'uploaded-file.txt'), uuid: '1234' }) });
    getDownloadUrl = jest.fn((uuid: string) => `/download/${uuid}`);
  }

  class Page extends BasePage<TestComponent> {
    get fileText() {
      return this.query<HTMLDivElement>('.moj-multi-file-upload__message');
    }

    get downloadLink() {
      return this.query<HTMLAnchorElement>('.govuk-link');
    }

    get delete() {
      return this.query<HTMLButtonElement>('.moj-multi-file-upload__delete');
    }

    get file() {
      return this.query<HTMLInputElement>('input').files.item(0);
    }

    set file(file: File) {
      this.setInputValue('input', file ?? []);
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, GovukComponentsModule, RouterTestingModule],
      declarations: [FileInputComponent, TestComponent, FileUploadListComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    component = fixture.debugElement.query(By.directive(FileInputComponent)).componentInstance;
    hostComponent = fixture.componentInstance;
    page = new Page(fixture);
    control = hostComponent.form.get('file') as FormControl;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display current value', () => {
    expect(page.fileText.textContent.trim()).toEqual('uploaded-file.txt');
  });

  it('should replace the uploaded file', () => {
    page.file = new File(['test content'], 'New file');
    fixture.detectChanges();

    expect(page.fileText.textContent.trim()).toEqual('New file');
    expect(page.downloadLink.textContent.trim()).toEqual('New file');
  });

  it('should validate a file against max size', () => {
    const uploadSubject = new Subject<HttpEvent<FileUuidDTO>>();

    expect(control.touched).toBeFalsy();

    control.setValidators(FileValidators.maxFileSize(1));
    control.setAsyncValidators(TestBed.inject(FileUploadService).upload(() => uploadSubject));
    const file = new File(['test content'], 'Big file');
    jest.spyOn(file, 'size', 'get').mockReturnValue(1024 * 1024 * 1024);
    page.file = file;
    fixture.detectChanges();

    expect(control.touched).toBeTruthy();
    expect(page.fileText.textContent).toEqual('Big file must be smaller than 1MB');
    expect(control.invalid).toBeTruthy();
    expect(control.errors).toEqual({ 'maxFileSize-0': 'Big file must be smaller than 1MB' });
  });

  it('should display upload progress', () => {
    const uploadSubject = new Subject<HttpEvent<FileUuidDTO>>();

    control.setAsyncValidators(TestBed.inject(FileUploadService).upload(() => uploadSubject));

    control.setValue([{ file: new File(['test content'], 'Existing file'), uuid: 'abcdA' }]);
    page.file = new File(['test content'], 'New file');
    uploadSubject.next({ type: HttpEventType.UploadProgress, loaded: 5, total: 15 });
    fixture.detectChanges();
    expect(page.fileText.textContent.trim()).toEqual('New file 33%');

    uploadSubject.next({ type: HttpEventType.UploadProgress, loaded: 10, total: 15 });
    fixture.detectChanges();

    expect(page.fileText.textContent).toEqual('New file 67%');

    uploadSubject.next(new HttpResponse({ status: HttpStatusCode.Ok, body: { uuid: 'abcd' } }));
    fixture.detectChanges();

    expect(page.fileText.textContent.trim()).toEqual('New file has been uploaded');
    expect(page.downloadLink.textContent.trim()).toEqual('New file');
  });

  it('should display upload errors', () => {
    const uploadSubject = new Subject<HttpEvent<FileUuidDTO>>();

    control.setAsyncValidators(
      TestBed.inject(FileUploadService).upload(() =>
        uploadSubject.pipe(
          first(),
          mergeMapTo(
            throwError(
              () =>
                new HttpErrorResponse({
                  status: HttpStatusCode.BadRequest,
                  error: { message: 'should not be txt' },
                }),
            ),
          ),
        ),
      ),
    );
    page.file = new File(['test content'], 'New file');
    uploadSubject.next({ type: HttpEventType.UploadProgress, loaded: 10, total: 15 });
    fixture.detectChanges();

    expect(control.invalid).toBeTruthy();
    expect(control.errors).toEqual({ upload: 'New file should not be txt' });
    expect(page.fileText.textContent.trim()).toEqual('New file should not be txt');
  });

  it('should delete a file', () => {
    page.delete.click();
    fixture.detectChanges();

    expect(page.fileText).toBeFalsy();
    expect(hostComponent.form.value.file).toBeNull();
    expect(page.file).toBeNull();

    page.file = new File(['content'], 'file.txt');
    fixture.detectChanges();

    page.delete.click();
    fixture.detectChanges();

    expect(page.fileText).toBeFalsy();
    expect(hostComponent.form.value.file).toBeNull();
    expect(page.file).toBeNull();
  });

  it('should retain the old file when the user cancels adding a new one', () => {
    const file = new File(['test content'], 'new-file.txt');
    page.file = file;
    fixture.detectChanges();

    page.file = null;
    fixture.detectChanges();

    expect(control.value).toEqual({ file, uuid: null, dimensions: null });
  });
});
