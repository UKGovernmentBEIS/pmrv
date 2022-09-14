import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { map } from 'rxjs';

import { PermitApplicationStore } from '../../../store/permit-application.store';

@Component({
  selector: 'app-uncertainty-analysis-summary-details',
  templateUrl: './summary-details.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryDetailsComponent {
  @Input() isPreview: boolean;
  @Input() hasBottomBorder = true;

  readonly files$ = this.store.pipe(map((state) => state.permit.uncertaintyAnalysis?.attachments ?? []));

  constructor(
    readonly store: PermitApplicationStore,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  changeClick(wizardStep?: string): void {
    this.router.navigate(['../' + wizardStep], { relativeTo: this.route, state: { changing: true } });
  }
}
