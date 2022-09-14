import { AfterContentInit, ContentChild, Directive, ElementRef, HostBinding, Input } from '@angular/core';

import { FieldsetHintDirective } from './fieldset-hint.directive';

@Directive({ selector: 'fieldset[govukFieldset]' })
export class FieldsetDirective implements AfterContentInit {
  @Input() id = 'fieldset';
  @ContentChild(FieldsetHintDirective, { read: ElementRef, static: true }) hint: ElementRef<HTMLSpanElement>;
  @HostBinding('class.govuk-fieldset') readonly fieldsetClass = true;

  @HostBinding('attr.id') get identifier() {
    return this.id;
  }

  @HostBinding('attr.aria-describedby') get ariaDescribedby() {
    return this.hint ? `${this.id}-hint` : null;
  }

  ngAfterContentInit(): void {
    if (this.hint) {
      this.hint.nativeElement.id = `${this.identifier}-hint`;
    }
  }
}
