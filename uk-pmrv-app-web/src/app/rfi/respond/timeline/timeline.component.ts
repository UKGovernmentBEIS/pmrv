import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { first, map, Observable, takeUntil } from 'rxjs';

import { DestroySubject } from '../../../core/services/destroy-subject.service';
import { BackLinkService } from '../../../shared/back-link/back-link.service';
import { RfiStore } from '../../store/rfi.store';

@Component({
  selector: 'app-timeline-rfi-submitted',
  templateUrl: './timeline.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [BackLinkService, DestroySubject],
})
export class TimelineComponent implements OnInit {
  readonly pairs$ = this.store.pipe(
    first(),
    map((state) => state.rfiQuestionPayload.questions.map((q, i) => [q, state.rfiResponsePayload.answers[i]])),
  ) as Observable<Array<Array<string>>>;

  constructor(
    readonly store: RfiStore,
    private readonly route: ActivatedRoute,
    private readonly backLinkService: BackLinkService,
    private readonly destroy$: DestroySubject,
  ) {}

  ngOnInit(): void {
    this.route.paramMap.pipe(takeUntil(this.destroy$)).subscribe(() => this.backLinkService.show());
  }

  getDownloadUrl(uuid: string): string | string[] {
    return ['..', 'file-download', uuid];
  }
}
