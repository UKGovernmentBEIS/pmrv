import { Component, ElementRef, ViewChild } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { NavigationLinkDirective } from './navigation-link.directive';

describe('NavigationLinkDirective', () => {
  @Component({
    template: `
      <a #firstLink appNavigationLink [routerLink]="['test']">Test Link 1</a>
      <a #secondLink appNavigationLink [routerLink]="['test-2']">Test Link 2</a>
    `,
  })
  class TestComponent {
    @ViewChild('firstLink') firstLink: ElementRef<HTMLAnchorElement>;
    @ViewChild('secondLink') secondLink: ElementRef<HTMLAnchorElement>;
  }

  @Component({
    template: ` <router-outlet></router-outlet>`,
  })
  class RouterComponent {}

  let directive: NavigationLinkDirective;
  let hostComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;

  beforeEach(() => {
    fixture = TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([
          { path: 'test', component: TestComponent },
          { path: 'test-2', component: TestComponent },
        ]),
      ],
      declarations: [TestComponent, NavigationLinkDirective, RouterComponent],
    }).createComponent(TestComponent);

    TestBed.createComponent(RouterComponent);

    hostComponent = fixture.componentInstance;

    fixture.detectChanges();
    directive = fixture.debugElement.query(By.directive(NavigationLinkDirective)).injector.get(NavigationLinkDirective);
  });

  it('should create an instance', () => {
    expect(directive).toBeTruthy();
  });

  it('should add navigation link class', () => {
    const testElement: HTMLElement = fixture.componentInstance.firstLink.nativeElement;
    expect(testElement.classList).toContain('govuk-link');
    expect(testElement.classList).toContain('hmcts-primary-navigation__link');
  });

  it('should add anchor inside li element', () => {
    const testElement: HTMLElement = fixture.componentInstance.firstLink.nativeElement;
    const parentElement: HTMLElement = testElement.parentElement;
    expect(parentElement.tagName).toEqual('LI');
    expect(parentElement.classList).toContain('hmcts-primary-navigation__item');
  });

  it('should apply the aria-current attribute to active anchor element', () => {
    expect(fixture.componentInstance.firstLink.nativeElement.getAttributeNames()).not.toContain('aria-current');
    expect(fixture.componentInstance.secondLink.nativeElement.getAttributeNames()).not.toContain('aria-current');

    hostComponent.firstLink.nativeElement.click();

    fixture.detectChanges();

    expect(fixture.componentInstance.secondLink.nativeElement.getAttributeNames()).not.toContain('aria-current');
    expect(fixture.componentInstance.firstLink.nativeElement.getAttributeNames()).toContain('aria-current');
    expect(fixture.componentInstance.firstLink.nativeElement.getAttribute('aria-current')).toEqual('page');

    hostComponent.secondLink.nativeElement.click();

    fixture.detectChanges();

    expect(fixture.componentInstance.firstLink.nativeElement.getAttributeNames()).not.toContain('aria-current');
    expect(fixture.componentInstance.secondLink.nativeElement.getAttributeNames()).toContain('aria-current');
    expect(fixture.componentInstance.secondLink.nativeElement.getAttribute('aria-current')).toEqual('page');
  });
});
