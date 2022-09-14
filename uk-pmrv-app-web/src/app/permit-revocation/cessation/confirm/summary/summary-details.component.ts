import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PermitCessation } from 'pmrv-api';

import { OfficialNoticeTypeMap } from '../core/cessation';

@Component({
  selector: 'app-revocation-cessation-summary-details',
  templateUrl: './summary-details.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryDetailsComponent {
  @Input() cessation: PermitCessation;
  @Input() allowancesSurrenderRequired: boolean;
  @Input() isEditable: boolean;

  readonly officialNoticeTypeMap = OfficialNoticeTypeMap;

  constructor(private readonly router: Router, private readonly route: ActivatedRoute) {}

  changeClick(wizardStep?: string): void {
    this.router.navigate(['../' + wizardStep], { relativeTo: this.route, state: { changing: true } });
  }
}
