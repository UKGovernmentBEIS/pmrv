import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PermitSurrenderStore } from '../../store/permit-surrender.store';

@Component({
  selector: 'app-permit-surrender-summary-details',
  templateUrl: './summary-details.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryDetailsComponent {
  @Input() isPreview: boolean;
  @Input() hasBottomBorder = true;

  permitSurrender$ = this.store.getPermitSurrender();

  constructor(
    readonly store: PermitSurrenderStore,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  changeClick(wizardStep?: string): void {
    this.router.navigate(['../' + wizardStep], { relativeTo: this.route, state: { changing: true } });
  }
}
