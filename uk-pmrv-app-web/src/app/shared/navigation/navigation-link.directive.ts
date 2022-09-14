import { Directive, ElementRef, OnInit, Optional, Renderer2 } from '@angular/core';
import { NavigationEnd, Router, RouterLink, RouterLinkWithHref } from '@angular/router';

import { filter, takeUntil } from 'rxjs';

import { DestroySubject } from '../../core/services/destroy-subject.service';

@Directive({
  selector: 'a[appNavigationLink]',
  providers: [DestroySubject],
})
export class NavigationLinkDirective implements OnInit {
  private isActive = false;

  private liElement: HTMLLIElement;

  constructor(
    private readonly elementRef: ElementRef,
    private readonly renderer: Renderer2,
    private readonly router: Router,
    private readonly destroy$: DestroySubject,
    @Optional() private readonly link?: RouterLink,
    @Optional() private readonly linkWithHref?: RouterLinkWithHref,
  ) {}

  ngOnInit(): void {
    this.router.events
      .pipe(
        filter((s) => s instanceof NavigationEnd),
        takeUntil(this.destroy$),
      )
      .subscribe(() => this.update());
    const parentNode: HTMLElement = this.elementRef.nativeElement.parentNode;
    const element: HTMLElement = this.elementRef.nativeElement;
    this.liElement = this.renderer.createElement('li');
    this.renderer.addClass(element, 'hmcts-primary-navigation__link');
    this.renderer.addClass(element, 'govuk-link');
    this.renderer.addClass(this.liElement, 'hmcts-primary-navigation__item');
    this.renderer.appendChild(this.liElement, element);
    this.renderer.appendChild(parentNode, this.liElement);
  }

  private isLinkActive(router: Router): (link: RouterLink | RouterLinkWithHref) => boolean {
    return (link) =>
      router.isActive(link.urlTree, {
        paths: 'subset',
        queryParams: 'subset',
        fragment: 'ignored',
        matrixParams: 'ignored',
      });
  }

  private hasActiveLinks(): boolean {
    const isActiveCheckFn = this.isLinkActive(this.router);

    return (this.link && isActiveCheckFn(this.link)) || (this.linkWithHref && isActiveCheckFn(this.linkWithHref));
  }

  private update(): void {
    const hasActiveLinks = this.hasActiveLinks();
    if (this.isActive !== hasActiveLinks) {
      this.isActive = hasActiveLinks;

      if (hasActiveLinks) {
        this.renderer.setAttribute(this.elementRef.nativeElement, 'aria-current', 'page');
      } else {
        this.renderer.removeAttribute(this.elementRef.nativeElement, 'aria-current');
      }
    }
  }
}
