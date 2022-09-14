import { TestBed } from '@angular/core/testing';

import { PermitTaskType } from '../shared/types/permit-task.type';
import { PermitApplicationStore } from '../store/permit-application.store';

export const setStoreTask = <T>(key: PermitTaskType, value?: T, isCompleted?: boolean[], subtaskKey?: string): void => {
  const store = TestBed.inject(PermitApplicationStore);
  const state = store.getState();

  store.setState({
    ...state,
    permit: {
      ...state.permit,
      [key]: value,
    },
    permitSectionsCompleted: {
      ...state.permitSectionsCompleted,
      ...(isCompleted ? { [subtaskKey ?? key]: isCompleted } : null),
    },
  });
};
