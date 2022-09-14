import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { pluck, takeUntil } from 'rxjs';

import { RequestActionsService } from 'pmrv-api';

import { DestroySubject } from '../../../core/services/destroy-subject.service';
import { BackLinkService } from '../../../shared/back-link/back-link.service';
import { RdeStore } from '../../store/rde.store';

@Component({
  selector: 'app-timeline-rde-forced-submitted',
  templateUrl: './timeline.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [BackLinkService, DestroySubject],
})
export class TimelineComponent implements OnInit {
  creationDate: string;

  constructor(
    readonly store: RdeStore,
    private readonly route: ActivatedRoute,
    private readonly backLinkService: BackLinkService,
    private readonly destroy$: DestroySubject,
    private readonly requestActionsService: RequestActionsService,
    private readonly cdRef: ChangeDetectorRef,
  ) {}

  decision$ = this.store.pipe(pluck('rdeForceDecisionPayload', 'decision'));
  evidence$ = this.store.pipe(pluck('rdeForceDecisionPayload', 'evidence'));

  ngOnInit(): void {
    this.route.paramMap.pipe(takeUntil(this.destroy$)).subscribe(() => this.backLinkService.show());
    this.requestActionsService
      .getRequestActionByIdUsingGET(Number(this.route.snapshot.paramMap.get('actionId')))
      .subscribe((res) => {
        this.creationDate = res.creationDate;
        this.cdRef.markForCheck();
      });
  }
}
