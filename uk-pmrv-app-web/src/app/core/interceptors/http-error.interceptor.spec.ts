import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';

import { mockClass } from '../../../testing';
import { GlobalErrorHandlingService } from '../services/global-error-handling.service';
import { HttpErrorInterceptor } from './http-error.interceptor';

describe(`HttpErrorInterceptor`, () => {
  let interceptorInstance: HttpErrorInterceptor;
  const globalErrorHandlingService = mockClass(GlobalErrorHandlingService);

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        HttpErrorInterceptor,
        {
          provide: HTTP_INTERCEPTORS,
          useClass: HttpErrorInterceptor,
          multi: true,
        },
        { provide: GlobalErrorHandlingService, useValue: globalErrorHandlingService },
      ],
    });
    interceptorInstance = TestBed.inject(HttpErrorInterceptor);
  });

  it('should be created', () => {
    expect(interceptorInstance).toBeDefined();
  });
});
