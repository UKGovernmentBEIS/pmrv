import { HttpErrorResponse, HttpEvent, HttpEventType, HttpResponse, HttpStatusCode } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { FormControl } from '@angular/forms';

import { merge, Observable } from 'rxjs';
import { TestScheduler } from 'rxjs/testing';

import { MessageValidationErrors } from 'govuk-components';

import { FileUuidDTO } from 'pmrv-api';

import { testSchedulerFactory } from '../../../testing/marble-helpers';
import { FileUploadService } from './file-upload.service';

describe('FileUploadService', () => {
  let service: FileUploadService;
  let testScheduler: TestScheduler;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FileUploadService);
    testScheduler = testSchedulerFactory();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should emit validation error for a single file upload', () => {
    testScheduler.run(({ cold, expectObservable }) => {
      const input$ = cold<HttpEvent<FileUuidDTO>>('--a-b--c|', {
        a: { type: HttpEventType.UploadProgress, loaded: 5, total: 15 },
        b: { type: HttpEventType.UploadProgress, loaded: 10, total: 15 },
        c: new HttpResponse({ status: HttpStatusCode.Ok, body: { uuid: 'abcd' } }),
      });
      const file = new File(['some content'], 'file.txt');

      expectObservable(
        service.upload(() => input$)(new FormControl({ file })) as Observable<MessageValidationErrors>,
      ).toBe('-------c|', { c: null });
      expectObservable(service.uploadProgress$).toBe('--a-b--c', {
        a: { file, progress: 5 / 15 },
        b: { file, progress: 10 / 15 },
        c: { file, progress: 1, uuid: 'abcd' },
      });
    });
  });

  it('should emit validation error for multiple files upload', () => {
    testScheduler.run(({ cold, expectObservable }) => {
      const fileA = new File(['some content'], 'file.txt');
      const fileB = new File(['some other content'], 'file2.txt');
      const fileC = new File(['some uploaded content'], 'file3.txt');
      const control = new FormControl([{ file: fileA }, { file: fileB }, { file: fileC, uuid: 'abcd' }]);
      const upload$ = jest.fn(() =>
        cold<HttpEvent<FileUuidDTO>>('--a---b------c|', {
          a: { type: HttpEventType.UploadProgress, loaded: 5, total: 15 },
          b: { type: HttpEventType.UploadProgress, loaded: 10, total: 15 },
          c: new HttpResponse({ status: HttpStatusCode.Ok, body: { uuid: 'abcd' } }),
        }),
      );

      expectObservable(service.uploadMany(upload$)(control) as Observable<MessageValidationErrors>).toBe(
        '--------------(c|)',
        { c: null },
      );
      expectObservable(service.uploadProgress$).toBe('--(aa)(bb)---(cc)', {
        a: { file: fileA, progress: 5 / 15 },
        b: { file: fileB, progress: 10 / 15 },
        c: { file: fileA, progress: 1, uuid: 'abcd' },
      });
    });
  });

  it('should be valid when no file is attached', () => {
    testScheduler.run(({ expectObservable, flush }) => {
      const control = new FormControl(null);
      const request = jest.fn();

      expectObservable(service.upload(request)(control) as Observable<MessageValidationErrors>).toBe('(c|)', {
        c: null,
      });
      expectObservable(service.uploadProgress$).toBe('--');
      flush();
      expect(request).not.toHaveBeenCalled();
    });
  });

  it('should be valid when no files are attached', () => {
    testScheduler.run(({ expectObservable, flush }) => {
      const control = new FormControl([]);
      const request = jest.fn();

      expectObservable(service.uploadMany(request)(control) as Observable<MessageValidationErrors>).toBe('(c|)', {
        c: null,
      });
      expectObservable(service.uploadProgress$).toBe('----');
      flush();
      expect(request).not.toHaveBeenCalled();
    });
  });

  it('should emit file upload events from various validations', () => {
    testScheduler.run(({ cold, expectObservable }) => {
      const controlA = new FormControl({ file: new File(['some content'], 'single-file.txt') });
      const controlB = new FormControl([
        { file: new File(['first content'], 'first-file.txt') },
        { file: new File(['second content'], 'second-file.txt') },
      ]);
      const error = new HttpErrorResponse({
        status: HttpStatusCode.BadRequest,
        statusText: 'Bad request',
        error: { message: 'File upload failed' },
      });
      const upload$ = jest.fn((file: File) =>
        file.name === 'second-file.txt'
          ? cold<HttpEvent<FileUuidDTO>>(
              '--a----#',
              { a: { type: HttpEventType.UploadProgress, loaded: 5, total: 15 } },
              error,
            )
          : cold<HttpEvent<FileUuidDTO>>('--a----b------c|', {
              a: { type: HttpEventType.UploadProgress, loaded: 5, total: 15 },
              b: { type: HttpEventType.UploadProgress, loaded: 10, total: 15 },
              c: new HttpResponse({ status: HttpStatusCode.Ok, body: { uuid: 'abcd' } }),
            }),
      );

      expectObservable(
        merge(
          service.upload(upload$)(controlA),
          service.uploadMany(upload$)(controlB),
        ) as Observable<MessageValidationErrors>,
      ).toBe('--------------a(b|)', { a: null, b: { 'upload-1': `second-file.txt ${error.error.message}` } });

      expectObservable(service.uploadProgress$).toBe('--(abc)(def)--(gh)--', {
        a: { file: controlA.value.file, progress: 5 / 15 },
        b: { file: controlB.value[0].file, progress: 5 / 15 },
        c: { file: controlB.value[1].file, progress: 5 / 15 },
        d: { file: controlA.value.file, progress: 10 / 15 },
        e: { file: controlB.value[0].file, progress: 10 / 15 },
        f: {
          file: controlB.value[1].file,
          progress: null,
          errors: { upload: `second-file.txt ${error.error.message}` },
        },
        g: { file: controlA.value.file, progress: 1, uuid: 'abcd' },
        h: { file: controlB.value[0].file, progress: 1, uuid: 'abcd' },
      });
    });
  });
});
