import { Directive, HostBinding } from '@angular/core';

@Directive({
  selector: 'div[govukInsetText]',
})
export class InsetTextDirective {
  @HostBinding('class')
  elementClass = 'govuk-inset-text';
}
