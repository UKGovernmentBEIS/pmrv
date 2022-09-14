import { HttpEvent } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AbstractControl, AsyncValidatorFn, FormBuilder, FormControl } from '@angular/forms';

import { first, map, Observable, of, switchMap } from 'rxjs';

import { FileUuidDTO, RequestTaskAttachmentActionProcessDTO, RequestTaskAttachmentsHandlingService } from 'pmrv-api';

import { Store } from '../../../core/store';
import { catchTaskReassignedBadRequest } from '../../../error/business-errors';
import { ConcurrencyErrorService } from '../../../error/concurrency-error/concurrency-error.service';
import { requestTaskReassignedError } from '../../errors/concurrency-error';
import { FileUploadService } from '../../file-input/file-upload.service';
import { FileUploadEvent } from '../../file-input/file-upload-event';
import { commonFileValidators, requiredFileValidator } from '../../file-input/file-validators';

@Injectable({ providedIn: 'root' })
export class RequestTaskFileService {
  readonly upload = (
    store: Store<any>,
    requestTaskActionType: RequestTaskAttachmentActionProcessDTO['requestTaskActionType'],
  ) => this.fileUploadService.upload((file) => this.storeUpload(store, file, requestTaskActionType));

  readonly uploadMany = (
    state: Store<any>,
    requestTaskActionType: RequestTaskAttachmentActionProcessDTO['requestTaskActionType'],
  ) => this.fileUploadService.uploadMany((file) => this.storeUpload(state, file, requestTaskActionType));

  constructor(
    private readonly fileUploadService: FileUploadService,
    private readonly requestTaskAttachmentsHandlingService: RequestTaskAttachmentsHandlingService,
    private readonly concurrencyErrorService: ConcurrencyErrorService,
    private readonly formBuilder: FormBuilder,
  ) {}

  buildFormControl(
    store: Store<any>,
    uuid: string | string[],
    propertyKey: string,
    requestTaskActionType: RequestTaskAttachmentActionProcessDTO['requestTaskActionType'],
    required = false,
    disabled = false,
  ): FormControl {
    return this.formBuilder.control(
      {
        value: !uuid
          ? null
          : Array.isArray(uuid)
          ? uuid.map((id) => this.buildFileEvent(store, id, propertyKey))
          : this.buildFileEvent(store, uuid, propertyKey),
        disabled,
      },
      {
        asyncValidators: [
          ...this.createCommonFileValidators(required),
          Array.isArray(uuid)
            ? this.uploadMany(store, requestTaskActionType)
            : this.upload(store, requestTaskActionType),
        ],
        updateOn: 'change',
      },
    );
  }

  // convert synchronous file validators to asynchronous
  // in order for file upload to run even if there are errors
  createCommonFileValidators(isRequired: boolean): AsyncValidatorFn[] {
    return commonFileValidators
      .concat(isRequired ? [requiredFileValidator] : [])
      .map((v) => (control: AbstractControl) => of(v(control)));
  }

  private buildFileEvent(store: Store<any>, uuid: string, propertyKey: string): Pick<FileUploadEvent, 'uuid' | 'file'> {
    //todo This should be refactored and replace the store with what is absolutely  necessary.
    // currently an assumption is mde that either the state will have
    // the property key property or the store will have a getter for the specific property
    // check common-tasks.store.ts
    const key = store.getState()[propertyKey] || store[propertyKey];
    return {
      uuid,
      file: { name: key[uuid] } as File,
    };
  }

  private storeUpload(
    store: Store<any>,
    file: File,
    requestTaskActionType: RequestTaskAttachmentActionProcessDTO['requestTaskActionType'],
  ): Observable<HttpEvent<FileUuidDTO>> {
    //todo This should be refactored and replace the store with what is absolutely necessary.
    // currently an assumption is mde that either the state will have
    // the requestTaskId property or the store will have a getter for requestTaskId
    return store.pipe(
      first(),
      map((state) => state.requestTaskId ?? (<any>store).requestTaskId),
      switchMap((requestTaskId) =>
        this.requestTaskAttachmentsHandlingService.uploadRequestTaskAttachmentUsingPOST(
          file,
          {
            requestTaskActionType,
            requestTaskId,
          },
          'events',
          true,
        ),
      ),
      catchTaskReassignedBadRequest(() =>
        this.concurrencyErrorService.showErrorForceNavigation(requestTaskReassignedError()),
      ),
    );
  }
}
