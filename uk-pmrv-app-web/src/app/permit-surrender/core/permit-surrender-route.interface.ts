import { Route } from '@angular/router';

import { StatusKey } from './permit-surrender.type';

export interface PermitSurrenderRoute extends Route {
  data?: PermitSurrenderRouteData;
  children?: PermitSurrenderRoute[];
}

export interface PermitSurrenderRouteData {
  pageTitle?: string;
  statusKey?: StatusKey;
}
