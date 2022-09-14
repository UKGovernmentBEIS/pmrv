import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { pluck, takeUntil } from 'rxjs';

import { DestroySubject } from '../../../core/services/destroy-subject.service';
import { BackLinkService } from '../../../shared/back-link/back-link.service';
import { RdeStore } from '../../store/rde.store';

@Component({
  selector: 'app-timeline-rde-submitted',
  template: `
    <app-page-heading>Response to request for deadline extension</app-page-heading>
    <dl govuk-summary-list [hasBorders]="false" [class.summary-list--edge-border]="true">
      <div govukSummaryListRow>
        <dt govukSummaryListRowKey>Response</dt>
        <dd govukSummaryListRowValue>Rejected</dd>
      </div>
      <div govukSummaryListRow>
        <dt govukSummaryListRowKey>Reason</dt>
        <dd govukSummaryListRowValue>{{ reason$ | async }}</dd>
      </div>
    </dl>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [BackLinkService, DestroySubject],
})
export class TimelineComponent implements OnInit {
  constructor(
    readonly store: RdeStore,
    private readonly route: ActivatedRoute,
    private readonly backLinkService: BackLinkService,
    private readonly destroy$: DestroySubject,
  ) {}

  reason$ = this.store.pipe(pluck('reason'));

  ngOnInit(): void {
    this.route.paramMap.pipe(takeUntil(this.destroy$)).subscribe(() => this.backLinkService.show());
  }
}
