import { HttpErrorResponse, HttpEvent, HttpEventType } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AbstractControl, AsyncValidatorFn } from '@angular/forms';

import {
  catchError,
  defaultIfEmpty,
  filter,
  forkJoin,
  iif,
  map,
  mapTo,
  Observable,
  of,
  shareReplay,
  Subject,
  tap,
} from 'rxjs';

import { MessageValidationErrors } from 'govuk-components';

import { FileUuidDTO } from 'pmrv-api';

import { FileUploadEvent } from './file-upload-event';
import { FileValidators } from './file-validators';

export type FileUploadRequest<T = FileUuidDTO> = (file: File) => Observable<HttpEvent<T>>;

@Injectable({ providedIn: 'root' })
export class FileUploadService {
  private readonly uploadProgressSubject = new Subject<FileUploadEvent>();
  readonly uploadProgress$ = this.uploadProgressSubject.asObservable();

  upload(request: FileUploadRequest): AsyncValidatorFn {
    const requestUpload = this.requestUpload(request);

    return ({ value }: AbstractControl) => {
      const file = (value as FileUploadEvent)?.file;

      return !file || this.isFileAlreadyUploaded(value) ? of(null) : requestUpload(file);
    };
  }

  uploadMany(request: FileUploadRequest): AsyncValidatorFn {
    const requestUpload = this.requestUpload(request);

    // Cache files that already have been attempted to be uploaded, to avoid replays of errored or in progress files
    const attempts = new WeakMap<File, Observable<MessageValidationErrors>>();

    return ({ value }: AbstractControl) =>
      forkJoin(
        ((value ?? []) as FileUploadEvent[]).map((fileEvent) => {
          if (!attempts.has(fileEvent.file)) {
            attempts.set(
              fileEvent.file,
              iif(
                () =>
                  !this.isFileAlreadyUploaded(fileEvent) &&
                  !this.isFileEmpty(fileEvent) &&
                  !this.isFileLarge(fileEvent),
                requestUpload(fileEvent.file),
                of(null),
              ).pipe(shareReplay({ bufferSize: 1, refCount: false })),
            );
          }

          return attempts.get(fileEvent.file);
        }),
      ).pipe(map(FileValidators.concatenateErrors), defaultIfEmpty(null));
  }

  private requestUpload(request: FileUploadRequest): (file: File) => Observable<MessageValidationErrors | null> {
    return (file: File) =>
      request(file).pipe(
        filter((event) => [HttpEventType.UploadProgress, HttpEventType.Response].includes(event.type)),
        tap({
          next: (event) => {
            switch (event.type) {
              case HttpEventType.UploadProgress:
                this.uploadProgressSubject.next({ progress: event.loaded / event.total, file });
                break;
              case HttpEventType.Response:
                this.uploadProgressSubject.next({ progress: 1, file, uuid: event.body.uuid });
                break;
            }
          },
          error: (error: HttpErrorResponse) =>
            this.uploadProgressSubject.next({
              progress: null,
              file,
              errors: this.createValidationError(file, error),
            }),
        }),
        filter((event) => event.type === HttpEventType.Response),
        mapTo(null),
        catchError((error: HttpErrorResponse) => of(this.createValidationError(file, error))),
      );
  }

  private isFileAlreadyUploaded({ file, uuid }: FileUploadEvent): boolean {
    return !(file instanceof File) || !!uuid;
  }

  private isFileEmpty({ file }: FileUploadEvent): boolean {
    return !(file instanceof File) || file.size === 0;
  }

  private isFileLarge({ file }: FileUploadEvent): boolean {
    return !(file instanceof File) || file.size >= 20 * 1024 * 1024;
  }

  private createValidationError(file: File, error: HttpErrorResponse): MessageValidationErrors {
    return { upload: `${file.name} ${error.error?.message ?? error.message}` };
  }
}
