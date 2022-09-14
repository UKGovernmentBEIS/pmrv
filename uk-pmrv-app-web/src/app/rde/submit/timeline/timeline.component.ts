import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { takeUntil } from 'rxjs';

import { DestroySubject } from '../../../core/services/destroy-subject.service';
import { BackLinkService } from '../../../shared/back-link/back-link.service';
import { RdeStore } from '../../store/rde.store';

@Component({
  selector: 'app-submit-timeline',
  templateUrl: './timeline.component.html',
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

  ngOnInit(): void {
    this.route.paramMap.pipe(takeUntil(this.destroy$)).subscribe(() => this.backLinkService.show());
  }
}
