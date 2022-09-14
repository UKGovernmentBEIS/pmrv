import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { first, Observable, pluck, switchMap, takeUntil } from 'rxjs';

import { PendingRequestService } from '../../../../core/guards/pending-request.service';
import { PendingRequest } from '../../../../core/interfaces/pending-request.interface';
import { DestroySubject } from '../../../../core/services/destroy-subject.service';
import { BackLinkService } from '../../../../shared/back-link/back-link.service';
import { PermitApplicationStore } from '../../../store/permit-application.store';

@Component({
  selector: 'app-answers',
  templateUrl: './answers.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [BackLinkService, DestroySubject],
})
export class AnswersComponent implements OnInit, PendingRequest {
  decision$ = this.store.pipe(pluck('determination', 'type'));
  reason$ = this.store.pipe(pluck('determination', 'reason'));
  activationDate$ = this.store.pipe(pluck('determination', 'activationDate')) as Observable<string>;
  officialNotice$ = this.store.pipe(pluck('determination', 'officialNotice')) as Observable<string>;
  annualEmissionsTargets$ = this.store.pipe(pluck('determination', 'annualEmissionsTargets')) as Observable<{
    [key: string]: number;
  }>;

  constructor(
    readonly store: PermitApplicationStore,
    readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly backLinkService: BackLinkService,
    private readonly destroy$: DestroySubject,
  ) {}

  ngOnInit(): void {
    this.route.paramMap
      .pipe(takeUntil(this.destroy$))
      .subscribe((paramMap) => this.backLinkService.show(`/permit-application/${paramMap.get('taskId')}/review`));
  }

  confirm(): void {
    this.store
      .pipe(
        first(),
        switchMap((state) => this.store.postDetermination(state.determination, true)),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => this.router.navigate(['../summary'], { relativeTo: this.route, state: { notification: true } }));
  }
}
