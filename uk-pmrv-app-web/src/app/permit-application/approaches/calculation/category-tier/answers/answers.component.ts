import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, switchMap, takeUntil } from 'rxjs';

import { PendingRequestService } from '../../../../../core/guards/pending-request.service';
import { PendingRequest } from '../../../../../core/interfaces/pending-request.interface';
import { DestroySubject } from '../../../../../core/services/destroy-subject.service';
import { BackLinkService } from '../../../../../shared/back-link/back-link.service';
import { PermitApplicationStore } from '../../../../store/permit-application.store';

@Component({
  selector: 'app-answers',
  templateUrl: './answers.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [BackLinkService, DestroySubject],
})
export class AnswersComponent implements OnInit, PendingRequest {
  index$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('index'))));

  private readonly statusKey = this.route.snapshot.data.statusKey;

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
      .subscribe((paramMap) =>
        this.backLinkService.show(
          `/permit-application/${paramMap.get('taskId')}/calculation/category-tier/${paramMap.get('index')}`,
        ),
      );
  }

  onConfirm(): void {
    combineLatest([this.index$, this.store])
      .pipe(
        first(),
        switchMap(([index, state]) =>
          this.store.postStatus(
            this.statusKey,
            state.permitSectionsCompleted[this.statusKey].map((item, idx) => (index === idx ? true : item)),
          ),
        ),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => this.navigateNext());
  }

  private navigateNext(): void {
    this.router.navigate([`../summary`], { relativeTo: this.route, state: { notification: true } });
  }
}
