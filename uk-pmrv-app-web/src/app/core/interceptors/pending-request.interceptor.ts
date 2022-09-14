import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';

import { Observable } from 'rxjs';

import { PendingRequestService } from '../guards/pending-request.service';

@Injectable()
export class PendingRequestInterceptor implements HttpInterceptor {
  constructor(private readonly pendingRequest: PendingRequestService) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    if (request.method !== 'GET') {
      return next.handle(request).pipe(this.pendingRequest.trackRequest());
    } else {
      return next.handle(request);
    }
  }
}
