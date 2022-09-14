import { Route } from '@angular/router';

export interface PaymentRoute extends Route {
  data?: PaymentRouteData;
  children?: PaymentRoute[];
}

export interface PaymentRouteData {
  pageTitle?: string;
}
