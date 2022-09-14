import { ApplicationRef, ComponentFactoryResolver, Directive, ElementRef, HostListener, Injector } from '@angular/core';

import { RouterLinkComponent } from './router-link.component';

@Directive({
  // eslint-disable-next-line @angular-eslint/directive-selector
  selector: 'markdown,[markdown]',
})
export class ConvertLinksDirective {
  constructor(
    private injector: Injector,
    private applicationRef: ApplicationRef,
    private componentFactoryResolver: ComponentFactoryResolver,
    private element: ElementRef<HTMLElement>,
  ) {}

  @HostListener('ready')
  processAnchors(): void {
    this.element.nativeElement.querySelectorAll('a[routerLink]').forEach((a) => {
      const component = this.componentFactoryResolver
        .resolveComponentFactory(RouterLinkComponent)
        .create(this.injector, []);
      this.applicationRef.attachView(component.hostView);
      const routerLink = a.getAttribute('routerLink') || '';

      component.instance.href = routerLink.split('#')[0];
      component.instance.fragment = routerLink.split('#')[1];
      component.instance.text = `${a.textContent}` || '';

      a.parentElement.replaceChild(component.location.nativeElement, a);
    });
  }
}
