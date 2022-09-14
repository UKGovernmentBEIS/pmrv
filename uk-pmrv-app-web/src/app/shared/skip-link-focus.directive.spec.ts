import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { SkipLinkComponent } from 'govuk-components';

import { RouterStubComponent } from '../../testing';
import { SkipLinkFocusDirective } from './skip-link-focus.directive';

describe('SkipLinkFocusDirective', () => {
  @Component({
    template: '<govuk-skip-link></govuk-skip-link><router-outlet appSkipLinkFocus></router-outlet>',
  })
  class TestComponent {}

  let directive: SkipLinkFocusDirective;
  let fixture: ComponentFixture<TestComponent>;

  beforeEach(() => {
    fixture = TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'test', component: TestComponent }])],
      declarations: [SkipLinkComponent, TestComponent, SkipLinkFocusDirective, RouterStubComponent],
    }).createComponent(TestComponent);

    fixture.detectChanges();
    directive = fixture.debugElement.query(By.directive(SkipLinkFocusDirective)).injector.get(SkipLinkFocusDirective);
  });

  it('should create an instance', () => {
    expect(directive).toBeTruthy();
  });

  it('should focus to skip link after navigation', async () => {
    expect(document.activeElement).toEqual(document.body);

    await TestBed.inject(Router).navigate(['test']);
    expect(fixture.nativeElement.querySelector('govuk-skip-link')).toEqual(document.activeElement);
  });
});
