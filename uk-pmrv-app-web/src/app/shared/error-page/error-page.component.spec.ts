import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';

import { PageHeadingComponent } from '../page-heading/page-heading.component';
import { ErrorPageComponent } from './error-page.component';

describe('ErrorPageComponent', () => {
  let component: ErrorPageComponent;
  let fixture: ComponentFixture<TestComponent>;
  let element: HTMLElement;

  @Component({
    template: `
      <app-error-page heading="Test heading">
        <p class="govuk-body">Some text</p>
      </app-error-page>
    `,
  })
  class TestComponent {}

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ErrorPageComponent, PageHeadingComponent, TestComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    component = fixture.debugElement.query(By.directive(ErrorPageComponent)).componentInstance;
    element = fixture.nativeElement;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the heading', () => {
    expect(element.querySelector('h1').textContent).toEqual('Test heading');
  });

  it('should render the body', () => {
    expect(element.querySelector('p').textContent).toEqual('Some text');
  });
});
