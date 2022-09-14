import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Router } from '@angular/router';

import { Observable, pluck } from 'rxjs';

import { PermitSurrenderReviewDeterminationReject } from 'pmrv-api';

import { PendingRequestService } from '../../../../../core/guards/pending-request.service';
import { PendingRequest } from '../../../../../core/interfaces/pending-request.interface';
import { BackLinkService } from '../../../../../shared/back-link/back-link.service';
import { PermitSurrenderStore } from '../../../../store/permit-surrender.store';

@Component({
  selector: 'app-summary',
  templateUrl: './summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [BackLinkService],
})
export class SummaryComponent implements PendingRequest {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;

  rejectDetermination$ = this.store.pipe(
    pluck('reviewDetermination'),
  ) as Observable<PermitSurrenderReviewDeterminationReject>;
  isEditable$ = this.store.pipe(pluck('isEditable'));

  constructor(
    readonly pendingRequest: PendingRequestService,
    private readonly store: PermitSurrenderStore,
    private readonly router: Router,
  ) {}
}
