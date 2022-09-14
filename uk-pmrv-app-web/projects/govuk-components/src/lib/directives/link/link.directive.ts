import {
  ChangeDetectorRef,
  Directive,
  ElementRef,
  HostBinding,
  Input,
  OnDestroy,
  OnInit,
  Optional,
  Renderer2,
} from '@angular/core';
import { NavigationEnd, Router, RouterLink, RouterLinkWithHref } from '@angular/router';

import { filter, Subscription } from 'rxjs';

@Directive({
  selector: 'a[govukLink]',
})
export class LinkDirective implements OnDestroy, OnInit {
  // eslint-disable-next-line @angular-eslint/no-input-rename
  @Input('govukLink') navLinkType: 'header' | 'footer' | 'meta' | 'breadcrumb' | 'notification' | '' = '';

  private isActive = false;
  private subscription: Subscription;
  private liElement: HTMLLIElement;

  constructor(
    private readonly elementRef: ElementRef,
    private readonly renderer: Renderer2,
    private readonly router: Router,
    private readonly changeDetectorRef: ChangeDetectorRef,
    @Optional() private readonly link?: RouterLink,
    @Optional() private readonly linkWithHref?: RouterLinkWithHref,
  ) {}

  @HostBinding('class.govuk-link') get hasSimpleLink() {
    return !this.navLinkType;
  }

  @HostBinding('class.govuk-breadcrumbs__link') get hasBreadcrumbsLink() {
    return this.navLinkType === 'breadcrumb';
  }

  @HostBinding('class.govuk-footer__link') get hasFooterLink() {
    return this.navLinkType === 'meta' || this.navLinkType === 'footer';
  }

  @HostBinding('class.govuk-header__link') get hasHeaderLink() {
    return this.navLinkType === 'header';
  }

  @HostBinding('class.govuk-notification-banner__link') get hasNotificationLink() {
    return this.navLinkType === 'notification';
  }

  ngOnInit(): void {
    if (this.navLinkType) {
      this.subscription = this.router.events
        .pipe(filter((s) => s instanceof NavigationEnd))
        .subscribe(() => this.update());

      const element: HTMLElement = this.elementRef.nativeElement;
      const parentNode = element.parentNode;
      this.liElement = this.renderer.createElement('li');
      this.renderer.addClass(this.liElement, this.getLiClassName());
      this.renderer.insertBefore(parentNode, this.liElement, element);
      this.renderer.appendChild(this.liElement, element);
    }
  }

  ngOnDestroy(): void {
    this.subscription?.unsubscribe();
    if (this.liElement) {
      this.renderer.removeChild(this.liElement.parentElement, this.liElement);
    }
  }

  private getLiClassName(): string {
    switch (this.navLinkType) {
      case 'meta':
        return 'govuk-footer__inline-list-item';
      case 'footer':
        return 'govuk-footer__list-item';
      case 'header':
        return 'govuk-header__navigation-item';
      case 'breadcrumb':
        return 'govuk-breadcrumbs__list-item';
    }
  }

  private getActiveLiClassName(): string {
    switch (this.navLinkType) {
      case 'header':
        return 'govuk-header__navigation-item--active';
      default:
        return null;
    }
  }

  private isLinkActive(link: RouterLink | RouterLinkWithHref): boolean {
    return this.router.isActive(link.urlTree, {
      paths: 'subset',
      queryParams: 'subset',
      fragment: 'ignored',
      matrixParams: 'ignored',
    });
  }

  private hasActiveLinks(): boolean {
    return (this.link && this.isLinkActive(this.link)) || (this.linkWithHref && this.isLinkActive(this.linkWithHref));
  }

  private update(): void {
    const activeClass = this.getActiveLiClassName();
    const hasActiveLinks = this.hasActiveLinks();
    if (this.isActive !== hasActiveLinks) {
      this.isActive = hasActiveLinks;

      if (hasActiveLinks) {
        this.renderer.addClass(this.liElement, activeClass);
      } else {
        this.renderer.removeClass(this.liElement, activeClass);
      }

      this.changeDetectorRef.markForCheck();
    }
  }
}
