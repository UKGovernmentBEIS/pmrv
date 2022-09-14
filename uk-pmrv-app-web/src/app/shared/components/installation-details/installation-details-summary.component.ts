import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';

import { InstallationOperatorDetails } from 'pmrv-api';

@Component({
  selector: 'app-installation-details-summary',
  templateUrl: './installation-details-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class InstallationDetailsSummaryComponent implements OnInit {
  @Input() cssClass: string;
  @Input() installationOperatorDetails: InstallationOperatorDetails;
  @Input() hideSubheadings = false;

  installationLocation: any;

  ngOnInit(): void {
    this.installationLocation = {
      ...this.installationOperatorDetails.installationLocation,
    };
  }
}
