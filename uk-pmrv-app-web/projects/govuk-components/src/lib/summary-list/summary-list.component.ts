import {
  ChangeDetectionStrategy,
  Component,
  ContentChild,
  ContentChildren,
  HostBinding,
  Input,
  QueryList,
  TemplateRef,
} from '@angular/core';

import { SummaryListRowDirective } from './directives/summary-list-row.directive';
import { SummaryItem } from './summary-list.interface';

@Component({
  selector: 'dl[govuk-summary-list]',
  templateUrl: './summary-list.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryListComponent {
  @Input() details: SummaryItem[];
  @Input() hasBorders = true;

  @ContentChildren(SummaryListRowDirective) rows: QueryList<SummaryListRowDirective>;
  @ContentChild('keyTemplate') keyTemplate: TemplateRef<any>;
  @ContentChild('valueTemplate') valueTemplate: TemplateRef<any>;

  @HostBinding('class.govuk-summary-list') readonly govukSummaryList = true;
  @HostBinding('class.govuk-summary-list--no-border') get govukSummaryListNoBorderClass(): boolean {
    return !this.hasBorders;
  }
}
