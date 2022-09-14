import { Component, ElementRef, ViewChild } from '@angular/core';
import { ComponentFixture, fakeAsync, TestBed, tick } from '@angular/core/testing';
import { By } from '@angular/platform-browser';

import { DebounceClickDirective } from './debounce-click.directive';

describe('DebounceClickDirective', () => {
  let directive: DebounceClickDirective;
  let fixture: ComponentFixture<TestComponent>;

  @Component({
    template: ` <button #button govukDebounceClick (debounceClick)="onClick()">Simple button</button> `,
  })
  class TestComponent {
    @ViewChild('button') button: ElementRef;

    onClick(): void {}
  }

  beforeEach(() => {
    fixture = TestBed.configureTestingModule({
      declarations: [TestComponent, DebounceClickDirective],
    }).createComponent(TestComponent);

    fixture.detectChanges();
    directive = fixture.debugElement.query(By.directive(DebounceClickDirective)).injector.get(DebounceClickDirective);
  });

  it('should create an instance', () => {
    expect(directive).toBeTruthy();
  });

  it('should click on single click', fakeAsync(() => {
    jest.spyOn(fixture.componentInstance, 'onClick');
    const button: HTMLButtonElement = fixture.debugElement.nativeElement.querySelector('button');
    button.click();
    tick(500);
    expect(fixture.componentInstance.onClick).toHaveBeenCalled();
  }));

  it('should click once on double click', fakeAsync(() => {
    jest.spyOn(fixture.componentInstance, 'onClick');
    const button: HTMLButtonElement = fixture.debugElement.nativeElement.querySelector('button');
    button.click();
    button.click();
    tick(500);
    expect(fixture.componentInstance.onClick).toHaveBeenCalledTimes(1);
  }));
});
