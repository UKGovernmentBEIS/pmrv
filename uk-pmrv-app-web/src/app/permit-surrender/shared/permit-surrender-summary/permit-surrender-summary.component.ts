import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map, Observable } from 'rxjs';

import { PermitSurrender } from 'pmrv-api';

import { PermitSurrenderStore } from '../../store/permit-surrender.store';

@Component({
  selector: 'app-permit-surrender-summary',
  templateUrl: './permit-surrender-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PermitSurrenderSummaryComponent {
  readonly permitSurrender$: Observable<PermitSurrender> = this.store.pipe(map((state) => state.permitSurrender));

  constructor(readonly store: PermitSurrenderStore) {}
}
