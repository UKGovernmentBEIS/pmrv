import { Directive, ElementRef, HostBinding, HostListener } from '@angular/core';

@Directive({
  selector:
    'a[govukButton], a[govukWarnButton], button[govukButton], button[govukWarnButton], button[govukSecondaryButton]',
})
export class ButtonDirective {
  constructor(private readonly elementRef: ElementRef) {}

  @HostBinding('attr.aria-disabled')
  @HostBinding('class.govuk-button--disabled')
  get ariaDisabled(): boolean | null {
    return ButtonDirective.isButton(this.nativeElement) && this.nativeElement.disabled ? true : null;
  }

  @HostBinding('class.govuk-button')
  get elementClass(): boolean {
    return true;
  }

  @HostBinding('class.govuk-button--secondary')
  get secondaryButton(): boolean {
    return this.nativeElement.hasAttribute('govuksecondarybutton');
  }

  @HostBinding('class.govuk-button--warning')
  get warningButton(): boolean {
    return this.nativeElement.hasAttribute('govukwarnbutton');
  }

  private get nativeElement(): HTMLButtonElement | HTMLAnchorElement {
    return this.elementRef.nativeElement;
  }

  private static isButton(nativeElement: HTMLButtonElement | HTMLAnchorElement): nativeElement is HTMLButtonElement {
    return nativeElement.tagName === 'BUTTON';
  }

  @HostListener('keydown', ['$event'])
  onKeyDown(event: KeyboardEvent): void {
    if (event.code === 'Space') {
      event.target.dispatchEvent(new MouseEvent('click'));
    }
  }
}
