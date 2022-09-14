import { TestBed } from '@angular/core/testing';

import { firstValueFrom, timer } from 'rxjs';

import { PendingRequestService } from './pending-request.service';

describe('PendingRequestService', () => {
  let service: PendingRequestService;

  beforeEach(() => {
    TestBed.configureTestingModule({ providers: [PendingRequestService] });
    service = TestBed.inject(PendingRequestService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should flag the start and end of a subscription', async () => {
    jest.useFakeTimers();

    timer(3000).pipe(service.trackRequest()).subscribe();

    const isPending = () => firstValueFrom(service.isRequestPending$);

    await expect(isPending()).resolves.toBeTruthy();

    jest.advanceTimersByTime(3001);

    await expect(isPending()).resolves.toBeFalsy();
  });

  it('should monitor multiple requests', async () => {
    jest.useFakeTimers();

    timer(3000).pipe(service.trackRequest()).subscribe();
    timer(5000).pipe(service.trackRequest()).subscribe();

    const isPending = () => firstValueFrom(service.isRequestPending$);

    await expect(isPending()).resolves.toBeTruthy();

    jest.advanceTimersByTime(3001);

    await expect(isPending()).resolves.toBeTruthy();
    expect(service.hasPendingRequests()).toBeTruthy();

    jest.advanceTimersByTime(2000);

    await expect(isPending()).resolves.toBeFalsy();
    expect(service.hasPendingRequests()).toBeFalsy();
  });
});
