import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, map, Observable, pluck, switchMap, takeUntil } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { BackLinkService } from '@shared/back-link/back-link.service';
import { hasRequestTaskAllowedActions } from "@shared/components/related-actions/request-task-allowed-actions.map";

import { RequestActionInfoDTO, RequestActionsService, RequestTaskItemDTO } from 'pmrv-api';

@Component({
  selector: 'app-review',
  templateUrl: './review.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [BackLinkService, DestroySubject],
})
export class ReviewComponent implements OnInit, PendingRequest {
  navigationState = { returnUrl: this.router.url };
  readonly info$: Observable<RequestTaskItemDTO> = this.route.data.pipe(pluck('review'));
  readonly formSubmission$ = new BehaviorSubject<FormSubmission>(null);

  hasRelatedActions$ = this.info$.pipe(
    map((info) => info.requestTask.assignable || hasRequestTaskAllowedActions(info.allowedRequestTaskActions))
  );

  actions$: Observable<(RequestActionInfoDTO & { payload?: any; expanded?: boolean })[]> = this.info$.pipe(
    switchMap((info) => this.requestActionsService.getRequestActionsByRequestIdUsingGET(info.requestInfo.id)),
    map((res) => this.sortTimeline(res)),
  );

  constructor(
    readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly destroy$: DestroySubject,
    private readonly backLinkService: BackLinkService,
    private readonly requestActionsService: RequestActionsService,
  ) {}

  ngOnInit(): void {
    this.formSubmission$.pipe(takeUntil(this.destroy$)).subscribe((res) => {
      if (res) {
        this.backLinkService.hide();
      } else {
        this.backLinkService.show();
      }
    });
  }

  submittedDecision(isAccepted: boolean): void {
    this.formSubmission$.next({ isAccepted, form: 'decision' });
  }

  archived(): void {
    this.router.navigate(['/dashboard']);
  }

  private sortTimeline(res: RequestActionInfoDTO[]): RequestActionInfoDTO[] {
    return res.slice().sort((a, b) => new Date(b.creationDate).getTime() - new Date(a.creationDate).getTime());
  }
}

type FormSubmission = { isAccepted: boolean; form: 'decision' };
