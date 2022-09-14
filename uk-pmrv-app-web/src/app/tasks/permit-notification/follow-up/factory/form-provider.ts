import { InjectionToken } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { Store } from '@core/store';
import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import moment from 'moment';

import { GovukValidators, MessageValidatorFn } from 'govuk-components';

import { RequestTaskActionProcessDTO } from 'pmrv-api';

import { CommonTasksStore } from '../../../store/common-tasks.store';

export const PERMIT_NOTIFICATION_FOLLOW_UP_FORM = new InjectionToken<FormGroup>(
  'Permit notification follow up task form',
);

let followUpResponseExpirationDate = '';

export const permitNotificationFollowUpFormProvider = {
  provide: PERMIT_NOTIFICATION_FOLLOW_UP_FORM,
  deps: [FormBuilder, CommonTasksStore, ActivatedRoute, RequestTaskFileService],
  useFactory: (
    fb: FormBuilder,
    store: CommonTasksStore,
    route: ActivatedRoute,
    requestTaskFileService: RequestTaskFileService,
  ) => {
    const keys: string[] = route.snapshot.data.keys;
    const configFilesOptions = route.snapshot.data?.configFilesOptions;
    const skipValidators = route.snapshot.data.skipValidators;
    const formGroupObj = {};

    const state = store.getValue();
    const payload = state.requestTaskItem.requestTask.payload;
    const disabled = !state.isEditable;

    for (const key of keys) {
      if (key) {
        if (key === 'followUpResponseExpirationDate') {
          followUpResponseExpirationDate = payload['followUpResponseExpirationDate'];
        }
        formGroupObj[key] =
          key !== 'files'
            ? [
                {
                  value:
                    payload !== undefined && payload[key] !== undefined && payload[key] !== null
                      ? value(payload, key)
                      : null,
                  disabled,
                },
                { validators: skipValidators ? [] : addValidators(key) },
              ]
            : createFilesFormControl(
                configFilesOptions['attachmentsName'],
                configFilesOptions['type'],
                store,
                requestTaskFileService,
                false,
                disabled,
                configFilesOptions['filesName'],
              );
      }
    }
    return fb.group(formGroupObj);
  },
};

const value = (payload: any, key: string) => {
  return key.toLowerCase().includes('date') ? new Date(payload[key]) : payload[key];
};

const createFilesFormControl = (
  property: string,
  requestTaskActionType: RequestTaskActionProcessDTO['requestTaskActionType'],
  store: Store<any>,
  requestTaskFileService: RequestTaskFileService,
  required: boolean,
  disabled: boolean,
  filesProperty: string,
): FormControl => {
  const state = store.getValue();
  const payload = state.requestTaskItem.requestTask.payload;
  const files =
    payload !== undefined && payload[filesProperty] != undefined && payload[filesProperty] != null
      ? payload[filesProperty]
      : [];
  return requestTaskFileService.buildFormControl(store, files, property, requestTaskActionType, required, disabled);
};

// Validators
export const dueDateMinValidator = (): ValidatorFn => {
  return (group: FormGroup): ValidationErrors => {
    return group.value && group.value < new Date()
      ? { invalidDate: `The date must be in the future` }
      : group.value && group.value <= new Date(followUpResponseExpirationDate)
      ? { invalidDate: `The date must be after the ${moment(followUpResponseExpirationDate).format('DD MMM YYYY')}` }
      : null;
  };
};

const addValidators = (key: string): MessageValidatorFn[] => {
  switch (key) {
    case 'followUpResponseExpirationDate':
      return [GovukValidators.required('Enter the date'), dueDateMinValidator()];
    case 'followUpResponse':
      return [
        GovukValidators.required('Enter a response'),
        GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
      ];
    default:
      return null;
  }
};
