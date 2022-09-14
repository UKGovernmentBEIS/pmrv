import { Directive, HostBinding } from '@angular/core';

@Directive({
  selector: 'dt[govukSummaryListRowKey]',
})
export class SummaryListRowKeyDirective {
  @HostBinding('class') className = 'govuk-summary-list__key';
}
