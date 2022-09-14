import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { lastValueFrom, Observable, timer } from 'rxjs';

import { PendingRequest } from '../interfaces/pending-request.interface';
import { PendingRequestGuard } from './pending-request.guard';
import { PendingRequestService } from './pending-request.service';

describe('PendingRequestGuard', () => {
  let testComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let guard: PendingRequestGuard;
  let router: Router;
  let windowAlert: jest.SpyInstance;

  @Component({ template: '', providers: [PendingRequestService] })
  class TestComponent implements PendingRequest {
    someRequest = timer(3000).pipe(this.pendingRequest.trackRequest());

    constructor(readonly pendingRequest: PendingRequestService) {}
  }

  @Component({ template: '' })
  class EmptyTestComponent {}

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      declarations: [TestComponent, EmptyTestComponent],
    });
    fixture = TestBed.createComponent(TestComponent);
    testComponent = fixture.componentInstance;
    guard = TestBed.inject(PendingRequestGuard);
    router = TestBed.inject(Router);
    windowAlert = jest.spyOn(window, 'alert').mockImplementation();
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should alert if deactivating while request is pending', async () => {
    jest.useFakeTimers();

    testComponent.someRequest.subscribe();

    await expect(lastValueFrom(guard.canDeactivate(testComponent) as Observable<boolean>)).resolves.toBeFalsy();
    expect(windowAlert).toHaveBeenCalled();

    jest.advanceTimersByTime(3000);
  });

  it('should allow deactivation if forced navigation', () => {
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { forceNavigation: true } } } as any);

    expect(guard.canDeactivate(testComponent)).toEqual(true);
  });

  it('should allow deactivation if request is not pending', () => {
    return expect(lastValueFrom(guard.canDeactivate(testComponent) as Observable<boolean>)).resolves.toBeTruthy();
  });

  it('should allow deactivation if there is no globally pending request', async () => {
    jest.useFakeTimers();
    const pendingRequestService = TestBed.inject(PendingRequestService);
    const canDeactivate = guard.canDeactivate(
      TestBed.createComponent(EmptyTestComponent).componentInstance,
    ) as Observable<boolean>;

    timer(3000).pipe(pendingRequestService.trackRequest()).subscribe();

    await expect(lastValueFrom(canDeactivate)).resolves.toBeFalsy();

    jest.advanceTimersByTime(3000);

    await expect(lastValueFrom(canDeactivate)).resolves.toBeTruthy();
  });

  it('should allow deactivation if there is no globally or locally pending request', async () => {
    jest.useFakeTimers();
    const pendingRequestService = TestBed.inject(PendingRequestService);
    const canDeactivate = guard.canDeactivate(testComponent) as Observable<boolean>;

    timer(2000).pipe(pendingRequestService.trackRequest()).subscribe();
    testComponent.someRequest.subscribe();

    await expect(lastValueFrom(canDeactivate)).resolves.toBeFalsy();

    jest.advanceTimersByTime(2000);

    await expect(lastValueFrom(canDeactivate)).resolves.toBeFalsy();

    jest.advanceTimersByTime(3000);

    await expect(lastValueFrom(canDeactivate)).resolves.toBeTruthy();
  });
});
