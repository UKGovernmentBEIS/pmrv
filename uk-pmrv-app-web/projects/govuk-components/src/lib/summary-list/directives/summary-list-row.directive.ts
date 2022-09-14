import { Directive, HostBinding } from '@angular/core';

@Directive({
  selector: 'div[govukSummaryListRow]',
})
export class SummaryListRowDirective {
  @HostBinding('class') className = 'govuk-summary-list__row';
}
