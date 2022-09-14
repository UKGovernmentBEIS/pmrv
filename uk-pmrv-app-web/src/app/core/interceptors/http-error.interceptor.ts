import { HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';

import { catchError, Observable } from 'rxjs';

import { GlobalErrorHandlingService } from '../services/global-error-handling.service';

@Injectable()
export class HttpErrorInterceptor implements HttpInterceptor {
  constructor(private readonly globalErrorHandlingService: GlobalErrorHandlingService) {}

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next
      .handle(request)
      .pipe(catchError((res: HttpErrorResponse) => this.globalErrorHandlingService.handleHttpError(res)));
  }
}
