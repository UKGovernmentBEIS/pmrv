import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { map, Observable } from 'rxjs';

import { PermitRevocationState } from '@permit-revocation/store/permit-revocation.state';
import { PermitRevocationStore } from '@permit-revocation/store/permit-revocation-store';
import { BackLinkService } from '@shared/back-link/back-link.service';

@Component({
  selector: 'app-withdraw-completed',
  templateUrl: './withdraw-completed.component.html',
  providers: [BackLinkService],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class WithdrawCompletedComponent implements OnInit {
  constructor(
    readonly store: PermitRevocationStore,
    readonly route: ActivatedRoute,
    readonly router: Router,
    private readonly backService: BackLinkService,
  ) {}
  creationDate$: Observable<string> = this.store.pipe(map(({ creationDate }) => creationDate));
  noticeRecipients$: Observable<
    Pick<PermitRevocationState, 'requestActionId' | 'decisionNotification' | 'usersInfo' | 'withdrawnOfficialNotice'>
  > = this.store.pipe(
    map(({ requestActionId, decisionNotification, usersInfo, withdrawnOfficialNotice }) => ({
      requestActionId,
      decisionNotification,
      usersInfo,
      withdrawnOfficialNotice,
    })),
  );

  ngOnInit(): void {
    this.backService.show();
  }
}
