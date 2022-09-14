import { RequestActionDTO } from 'pmrv-api';

export interface CommonActionsState {
  action: RequestActionDTO;
  storeInitialized: boolean;
}

export const initialState: CommonActionsState = {
  action: undefined,
  storeInitialized: false,
};
