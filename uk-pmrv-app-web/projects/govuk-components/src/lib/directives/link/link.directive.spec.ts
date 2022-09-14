import { Component, ElementRef, ViewChild } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { NotificationBannerComponent } from '../../notification-banner/notification-banner.component';
import { LinkDirective } from './link.directive';

describe('LinkDirective', () => {
  @Component({
    template: `
      <a #meta govukLink="meta" href="#">Test Meta Link</a>
      <a #header govukLink="header" [routerLink]="['test']">Test Header Link</a>
      <h1>Some middle text</h1>
      <a #footer govukLink="footer" href="#">Test Footer Link</a>
      <a #breadcrumb govukLink="breadcrumb" href="#">Test Breadcrumb Link</a>
      <a #simple govukLink href="#">Simple link</a>
      <govuk-notification-banner>
        <p class="govuk-body">
          There is a request waiting!
          <a govukLink="notification" #notification href="#">Click here</a>
        </p>
      </govuk-notification-banner>
    `,
  })
  class TestComponent {
    @ViewChild('meta') metaLink: ElementRef<HTMLAnchorElement>;
    @ViewChild('header') headerLink: ElementRef<HTMLAnchorElement>;
    @ViewChild('footer') footerLink: ElementRef<HTMLAnchorElement>;
    @ViewChild('breadcrumb') breadcrumbLink: ElementRef<HTMLAnchorElement>;
    @ViewChild('simple') simpleLink: ElementRef<HTMLAnchorElement>;
    @ViewChild('notification') notificationLink: ElementRef<HTMLAnchorElement>;
  }

  @Component({ template: '<router-outlet></router-outlet>' })
  class RouterComponent {}

  let directive: LinkDirective;
  let hostComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;

  beforeEach(() => {
    fixture = TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'test', component: TestComponent }])],
      declarations: [TestComponent, LinkDirective, RouterComponent, NotificationBannerComponent],
    }).createComponent(TestComponent);

    TestBed.createComponent(RouterComponent);

    hostComponent = fixture.componentInstance;

    fixture.detectChanges();
    directive = fixture.debugElement.query(By.directive(LinkDirective)).injector.get(LinkDirective);
  });

  it('should create an instance', () => {
    expect(directive).toBeTruthy();
  });

  it('should add navigation link class (meta link)', () => {
    const testElement: HTMLElement = fixture.componentInstance.metaLink.nativeElement;
    expect(testElement.classList).toContain('govuk-footer__link');
  });

  it('should add anchor inside li element (meta link)', () => {
    const testElement: HTMLElement = fixture.componentInstance.metaLink.nativeElement;
    const parentElement: HTMLElement = testElement.parentElement;
    expect(parentElement.tagName).toEqual('LI');
    expect(parentElement.classList).toContain('govuk-footer__inline-list-item');
  });

  it('should add navigation link class (header link)', () => {
    const testElement: HTMLElement = fixture.componentInstance.headerLink.nativeElement;
    expect(testElement.classList).toContain('govuk-header__link');
  });

  it('should add anchor inside li element (header link)', () => {
    const testElement: HTMLElement = fixture.componentInstance.headerLink.nativeElement;
    const parentElement: HTMLElement = testElement.parentElement;
    expect(parentElement.tagName).toEqual('LI');
    expect(parentElement.classList).toContain('govuk-header__navigation-item');
  });

  it('should add navigation link class (footer link)', () => {
    const testElement: HTMLElement = fixture.componentInstance.footerLink.nativeElement;
    expect(testElement.classList).toContain('govuk-footer__link');
  });

  it('should add anchor inside li element (footer link)', () => {
    const testElement: HTMLElement = fixture.componentInstance.footerLink.nativeElement;
    const parentElement: HTMLElement = testElement.parentElement;
    expect(parentElement.tagName).toEqual('LI');
    expect(parentElement.classList).toContain('govuk-footer__list-item');
  });

  it('should add navigation li element and link class (breadcrumb link)', () => {
    const breadcrumbLinkElement: HTMLElement = fixture.componentInstance.breadcrumbLink.nativeElement;
    const parentElement: HTMLElement = breadcrumbLinkElement.parentElement;

    expect(breadcrumbLinkElement.classList).toContain('govuk-breadcrumbs__link');
    expect(parentElement.tagName).toEqual('LI');
    expect(parentElement.classList).toContain('govuk-breadcrumbs__list-item');
  });

  it('should apply the active class to li element', () => {
    const element: HTMLElement = fixture.nativeElement;

    expect(element.querySelector('.govuk-header__navigation-item--active')).toBeFalsy();

    hostComponent.headerLink.nativeElement.click();

    fixture.detectChanges();

    expect(element.querySelector('.govuk-header__navigation-item--active')).toBeTruthy();
  });

  it('should apply the link class to simple links', () => {
    expect(fixture.componentInstance.simpleLink.nativeElement.classList).toContain('govuk-link');
  });

  it('should not wrap simple links with list elements', () => {
    expect(fixture.componentInstance.simpleLink.nativeElement.parentElement.tagName).not.toEqual('LI');
  });

  it('should apply the notification link class in links inside the notification banner', () => {
    expect(fixture.componentInstance.notificationLink.nativeElement.className).toEqual(
      'govuk-notification-banner__link',
    );
  });

  it('should keep the order of sibling elements', () => {
    const element: HTMLElement = fixture.nativeElement;

    expect(element.querySelector('h1').nextElementSibling.textContent).toEqual('Test Footer Link');
  });

  it('should clean up the li element on destroy', () => {
    const element: HTMLElement = fixture.nativeElement;
    fixture.destroy();
    expect(element.querySelectorAll('li')).toHaveLength(0);
  });
});
