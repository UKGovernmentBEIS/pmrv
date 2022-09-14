import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { map, Observable } from 'rxjs';

import { PermitRevocationState } from '@permit-revocation/store/permit-revocation.state';
import { PermitRevocationStore } from '@permit-revocation/store/permit-revocation-store';
import { BackLinkService } from '@shared/back-link/back-link.service';

@Component({
  selector: 'app-permit-revocation-submitted',
  template: ` <app-page-heading>
      {{ (route.data | async)?.pageTitle }}
      <span class="govuk-body govuk-!-display-block govuk-!-margin-top-2">{{
        (store | async)?.creationDate | govukDate
      }}</span>
    </app-page-heading>
    <app-summary sectionHeading="Details"></app-summary>
    <app-official-notice-recipients
      [noticeRecipientsData]="noticeRecipients$ | async"
    ></app-official-notice-recipients>`,
  providers: [BackLinkService],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PermitRevocationSubmittedComponent implements OnInit {
  constructor(
    private readonly backLinkService: BackLinkService,
    readonly route: ActivatedRoute,
    readonly store: PermitRevocationStore,
  ) {}
  noticeRecipients$: Observable<
    Pick<PermitRevocationState, 'requestActionId' | 'decisionNotification' | 'usersInfo' | 'officialNotice'>
  > = this.store.pipe(
    map(({ requestActionId, decisionNotification, usersInfo, officialNotice }) => ({
      requestActionId,
      decisionNotification,
      usersInfo,
      officialNotice,
    })),
  );

  ngOnInit(): void {
    this.backLinkService.show();
  }
}
