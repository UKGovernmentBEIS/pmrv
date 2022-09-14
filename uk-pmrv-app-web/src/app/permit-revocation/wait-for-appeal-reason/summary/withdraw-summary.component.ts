import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { map, Observable } from 'rxjs';

import { PermitRevocationState } from '@permit-revocation/store/permit-revocation.state';
import { PermitRevocationStore } from '@permit-revocation/store/permit-revocation-store';

@Component({
  selector: 'app-withdraw-summary',
  templateUrl: './withdraw-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class WithdrawSummaryComponent {
  @Input() sectionHeading: string = undefined;

  constructor(
    readonly store: PermitRevocationStore,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  data: Observable<Pick<PermitRevocationState, 'isEditable' | 'reason' | 'withdrawFiles'>> = this.store.pipe(
    map(({ isEditable, reason, withdrawFiles }) => ({ isEditable, reason, withdrawFiles })),
  );

  changeClick(wizardStep?: string): void {
    this.router.navigate(['../' + wizardStep], { relativeTo: this.route, state: { changing: true } });
  }
}
