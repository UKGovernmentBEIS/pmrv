import { KeyValue } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { SummaryList } from '@tasks/permit-notification/follow-up/model/model';

@Component({
  selector: 'app-follow-up-summary',
  templateUrl: './follow-up-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FollowUpSummaryComponent {
  @Input() data: { [key: string]: any | number | boolean | string[] };
  @Input() sectionHeading: string = undefined;
  @Input() summaryListMapper: { [key: string]: SummaryList };
  @Input() allowChange = true;
  @Input() changeLink: string = undefined;
  @Input() files: { downloadUrl: string; fileName: string }[];

  constructor(private readonly router: Router, readonly route: ActivatedRoute) {}

  changeClick(url?: string): void {
    this.router.navigate(['../' + url], { relativeTo: this.route, state: { changing: true } });
  }

  keysByOrder = (a: KeyValue<string, any>, b: KeyValue<string, any>): number => {
    const _a = this.summaryListMapper[a.key];
    const _b = this.summaryListMapper[b.key];

    if (_a != undefined && _a['order'] && _b != undefined && _b['order']) {
      return _a['order'] > _b['order'] ? 1 : _b['order'] > _a['order'] ? -1 : 0;
    } else {
      return 0;
    }
  };

  hasValue(key: string, value: any): boolean {
    const type = this.summaryListMapper[key]?.type;
    const statusKey = this.summaryListMapper[key]?.statusKey;

    switch (type) {
      case 'number':
      case 'date':
      case 'string':
        return !!value;
      case 'boolean':
        return statusKey ? value[statusKey] : null;
      case 'files':
        return value && value.length > 0;
    }
  }
}
