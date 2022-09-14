import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PermitApplicationStore } from '../../../store/permit-application.store';

@Component({
  selector: 'app-determination-summary-details',
  templateUrl: './summary-details.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryDetailsComponent implements OnInit {
  @Input() decision: string;
  @Input() reason: string;
  @Input() activationDate: string;
  @Input() officialNotice: string;
  @Input() annualEmissionsTargets;
  @Input() cssClass: string;
  @Input() changePerStage: boolean;

  emissions: { year: string; emissions: any }[];

  constructor(
    readonly store: PermitApplicationStore,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    this.emissions =
      this.annualEmissionsTargets &&
      Object.keys(this.annualEmissionsTargets).map((key) => ({
        year: key,
        emissions: this.annualEmissionsTargets[key],
      }));
  }

  changeClick(wizardStep?: string): void {
    this.router.navigate(['../' + wizardStep], { relativeTo: this.route, state: { changing: true } });
  }
}
