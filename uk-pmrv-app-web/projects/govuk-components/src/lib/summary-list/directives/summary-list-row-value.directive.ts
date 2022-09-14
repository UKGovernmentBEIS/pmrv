import { Directive, HostBinding } from '@angular/core';

@Directive({
  selector: 'dd[govukSummaryListRowValue]',
})
export class SummaryListRowValueDirective {
  @HostBinding('class') className = 'govuk-summary-list__value';
}
