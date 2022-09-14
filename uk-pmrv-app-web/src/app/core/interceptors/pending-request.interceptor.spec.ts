import { HttpRequest, HttpResponse } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';

import { mapTo, timer } from 'rxjs';

import { PendingRequestService } from '../guards/pending-request.service';
import { PendingRequestInterceptor } from './pending-request.interceptor';

describe('PendingRequestInterceptor', () => {
  let interceptor: PendingRequestInterceptor;
  let pendingRequestService: PendingRequestService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [PendingRequestInterceptor],
    });

    interceptor = TestBed.inject(PendingRequestInterceptor);
    pendingRequestService = TestBed.inject(PendingRequestService);
  });

  it('should be created', () => {
    expect(interceptor).toBeTruthy();
  });

  it('should track non-GET pending requests', () => {
    jest.useFakeTimers();

    const next = { handle: () => timer(1000).pipe(mapTo(new HttpResponse())) };
    interceptor.intercept(new HttpRequest<unknown>('POST', 'http://localhost', {}), next).subscribe();

    expect(pendingRequestService.hasPendingRequests()).toBeTruthy();

    jest.advanceTimersByTime(3000);

    expect(pendingRequestService.hasPendingRequests()).toBeFalsy();
  });

  it('should not track GET requests', () => {
    const next = { handle: () => timer(1000).pipe(mapTo(new HttpResponse())) };
    interceptor.intercept(new HttpRequest<unknown>('GET', 'http://localhost'), next).subscribe();

    expect(pendingRequestService.hasPendingRequests()).toBeFalsy();
  });
});
