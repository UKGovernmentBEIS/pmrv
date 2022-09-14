import { Injectable } from '@angular/core';

import { distinctUntilChanged, first, map, Observable, pluck, switchMap, tap } from 'rxjs';

import { requestTaskReassignedError, taskNotFoundError } from '@shared/errors/concurrency-error';
import { StatusKey } from '@tasks/aer/core/aer-task.type';
import { CommonTasksState } from '@tasks/store/common-tasks.state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import {
  Aer,
  AerApplicationSubmitRequestTaskPayload,
  RequestMetadata,
  RequestTaskActionPayload,
  RequestTaskActionProcessDTO,
  TasksService,
} from 'pmrv-api';

import { catchTaskReassignedBadRequest } from '../../../error/business-errors';
import { ConcurrencyErrorService } from '../../../error/concurrency-error/concurrency-error.service';
import { catchNotFoundRequest, ErrorCode } from '../../../error/not-found-error';

@Injectable({ providedIn: 'root' })
export class AerService {
  constructor(
    private readonly store: CommonTasksStore,
    private readonly tasksService: TasksService,
    private readonly concurrencyErrorService: ConcurrencyErrorService,
  ) {}

  private get attachments() {
    let attachments: { [key: string]: string };
    const requestTaskType = this.store.requestTaskType;
    switch (requestTaskType) {
      case 'AER_APPLICATION_SUBMIT':
        attachments = this.store.aerAttachments;
        break;
      default:
        throw Error('Unhandled task payload type: ' + requestTaskType);
    }
    return attachments;
  }

  getPayload(): Observable<any> {
    return this.store.payload$.pipe(map((payload) => payload));
  }

  getTask<K extends keyof Aer>(aerTask: K): Observable<Aer[K]> {
    return this.getPayload().pipe(pluck('aer', aerTask), distinctUntilChanged());
  }

  getDownloadUrlFiles(files: string[]): { downloadUrl: string; fileName: string }[] {
    const attachments: { [key: string]: string } = this.attachments;
    const url = this.createBaseFileDownloadUrl();
    return (
      files?.map((id) => ({
        downloadUrl: url + `${id}`,
        fileName: attachments[id],
      })) ?? []
    );
  }

  createBaseFileDownloadUrl() {
    const requestTaskId = this.store.requestTaskId;
    return `/tasks/${requestTaskId}/file-download/`;
  }

  get requestTaskId() {
    return this.store.requestTaskId;
  }

  get isEditable$(): Observable<boolean> {
    return this.store.isEditable$;
  }

  get requestMetadata$(): Observable<RequestMetadata> {
    return this.store.requestMetadata$;
  }

  postTaskSave(
    value: any,
    attachments?: { [key: string]: string },
    statusValue?: boolean | boolean[],
    statusKey?: StatusKey,
  ) {
    return this.store.pipe(
      first(),
      switchMap((state) =>
        this.postAer(
          {
            ...state,
            requestTaskItem: {
              ...state.requestTaskItem,
              requestTask: {
                ...state.requestTaskItem.requestTask,
                payload: {
                  ...state.requestTaskItem.requestTask.payload,
                  aer: {
                    ...(state.requestTaskItem.requestTask.payload as AerApplicationSubmitRequestTaskPayload).aer,
                    ...value,
                  },
                  aerAttachments: {
                    ...(state.requestTaskItem.requestTask.payload as AerApplicationSubmitRequestTaskPayload)
                      ?.aerAttachments,
                    ...attachments,
                  },
                  aerSectionsCompleted: {
                    ...(state.requestTaskItem.requestTask.payload as AerApplicationSubmitRequestTaskPayload)
                      ?.aerSectionsCompleted,
                    ...(statusKey
                      ? { [statusKey]: Array.isArray(statusValue) ? statusValue : [statusValue] }
                      : undefined),
                  },
                } as AerApplicationSubmitRequestTaskPayload,
              },
            },
          },
          'AER_SAVE_APPLICATION',
        ),
      ),
    );
  }

  postAer(state: CommonTasksState, actionType: RequestTaskActionProcessDTO['requestTaskActionType']) {
    const payload = state.requestTaskItem.requestTask.payload as AerApplicationSubmitRequestTaskPayload;
    return this.tasksService
      .processRequestTaskActionUsingPOST({
        requestTaskActionType: actionType,
        requestTaskId: state.requestTaskItem.requestTask.id,
        requestTaskActionPayload: this.createRequestTaskActionPayload(actionType, payload),
      })
      .pipe(
        catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
          this.concurrencyErrorService.showErrorForceNavigation(taskNotFoundError),
        ),
        catchTaskReassignedBadRequest(() =>
          this.concurrencyErrorService.showErrorForceNavigation(requestTaskReassignedError()),
        ),
        tap(() => this.store.setState(state)),
      );
  }

  private createRequestTaskActionPayload(
    actionType: RequestTaskActionProcessDTO['requestTaskActionType'],
    payload: AerApplicationSubmitRequestTaskPayload,
  ) {
    return {
      ...(actionType === 'AER_SAVE_APPLICATION'
        ? {
            payloadType: 'AER_SAVE_APPLICATION_PAYLOAD',
            aer: payload.aer,
            aerSectionsCompleted: payload.aerSectionsCompleted,
          }
        : {
            payloadType: 'EMPTY_PAYLOAD',
          }),
    } as RequestTaskActionPayload;
  }
}
