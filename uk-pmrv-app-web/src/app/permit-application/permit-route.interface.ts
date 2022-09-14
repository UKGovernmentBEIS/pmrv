import { Route } from '@angular/router';

import { PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload } from 'pmrv-api';

import { PermitTaskType, StatusKey } from './shared/types/permit-task.type';

export interface PermitRoute extends Route {
  data?: PermitRouteData;
  children?: PermitRoute[];
}

export interface PermitRouteData {
  pageTitle?: string;
  permitTask?: PermitTaskType;
  statusKey?: StatusKey;
  taskKey?: string;
  groupKey?: PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload['group'];
}
