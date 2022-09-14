import { Component, ElementRef, ViewChild } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';

import { InsetTextDirective } from './inset-text.directive';

describe('InsetTextDirective', () => {
  let directive: InsetTextDirective;
  let fixture: ComponentFixture<TestComponent>;

  @Component({
    template: ` <div #insetText govukInsetText>Some text</div>`,
  })
  class TestComponent {
    @ViewChild('insetText') insetTextDiv: ElementRef;
  }

  beforeEach(() => {
    fixture = TestBed.configureTestingModule({
      declarations: [TestComponent, InsetTextDirective],
    }).createComponent(TestComponent);

    fixture.detectChanges();
    directive = fixture.debugElement.query(By.directive(InsetTextDirective)).injector.get(InsetTextDirective);
  });

  it('should create an instance', () => {
    expect(directive).toBeTruthy();
  });

  it('should have inset-text class', () => {
    expect(fixture.componentInstance.insetTextDiv.nativeElement.classList).toContain('govuk-inset-text');
  });
});
