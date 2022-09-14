import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';

import { PermitRevocationState } from '@permit-revocation/store/permit-revocation.state';

@Component({
  selector: 'app-official-notice-recipients',
  templateUrl: './official-notice-recipients.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OfficialNoticeRecipientsComponent implements OnInit {
  @Input() noticeRecipientsData: Pick<
    PermitRevocationState,
    'requestActionId' | 'decisionNotification' | 'usersInfo' | 'officialNotice' | 'withdrawnOfficialNotice'
  >;

  signatory: string;
  operators: string[];

  ngOnInit(): void {
    this.signatory = this.noticeRecipientsData.decisionNotification.signatory;
    this.operators = Object.keys(this.noticeRecipientsData.usersInfo).filter((userId) => userId !== this.signatory);
  }
}
