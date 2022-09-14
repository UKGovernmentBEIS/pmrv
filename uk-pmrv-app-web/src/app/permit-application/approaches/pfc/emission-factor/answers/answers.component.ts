import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { first, switchMapTo, takeUntil } from 'rxjs';

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
      .subscribe((paramMap) => this.backLinkService.show(`/permit-application/${paramMap.get('taskId')}/pfc`));
  }

  confirm(): void {
    this.store
      .postStatus(this.route.snapshot.data.statusKey, true)
      .pipe(this.pendingRequest.trackRequest(), switchMapTo(this.store), first())
      .subscribe((state) =>
        this.router.navigate(
          [state.requestTaskType === 'PERMIT_ISSUANCE_APPLICATION_REVIEW' ? '../../../review/pfc' : '../summary'],
          { relativeTo: this.route, state: { notification: true } },
        ),
      );
  }
}
