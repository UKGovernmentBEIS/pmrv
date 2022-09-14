import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';

import { finalize, interval, takeUntil } from 'rxjs';

import { DestroySubject } from './destroy-subject.service';

describe('DestroySubject', () => {
  let service: DestroySubject;
  let fixture: ComponentFixture<TestComponent>;
  let closeSpy: jest.Mock;

  @Component({ template: '', providers: [DestroySubject] })
  class TestComponent {
    constructor(private readonly destroy$: DestroySubject) {
      interval(100)
        .pipe(
          takeUntil(this.destroy$),
          finalize(() => closeSpy()),
        )
        .subscribe();
    }
  }

  beforeEach(async () => {
    closeSpy = jest.fn();
    await TestBed.configureTestingModule({ declarations: [TestComponent] }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    fixture.detectChanges();
    service = fixture.componentRef.injector.get(DestroySubject);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should close any subscriptions on destroy', () => {
    expect(closeSpy).not.toHaveBeenCalled();
    fixture.destroy();

    expect(service.isStopped).toBeTruthy();
    expect(closeSpy).toHaveBeenCalled();
  });
});
