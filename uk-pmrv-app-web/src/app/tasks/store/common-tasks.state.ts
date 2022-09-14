import { ItemDTO, RequestActionInfoDTO, RequestTaskItemDTO, UserStatusDTO } from 'pmrv-api';

export interface CommonTasksState {
  requestTaskItem: RequestTaskItemDTO;
  relatedTasks: ItemDTO[];
  timeLineActions: RequestActionInfoDTO[];
  storeInitialized: boolean;
  isEditable: boolean;
  user: UserStatusDTO;
}

export const initialState: CommonTasksState = {
  requestTaskItem: undefined,
  relatedTasks: undefined,
  timeLineActions: undefined,
  storeInitialized: false,
  isEditable: undefined,
  user: undefined,
};
