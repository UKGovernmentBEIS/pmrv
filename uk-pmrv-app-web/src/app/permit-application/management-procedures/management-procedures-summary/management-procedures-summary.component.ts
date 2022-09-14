import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { pluck } from 'rxjs';

import { PermitApplicationStore } from '../../store/permit-application.store';
import { ManagementProceduresDefinitionData } from '../management-procedures.interface';

@Component({
  selector: 'app-management-procedures-summary',
  templateUrl: './management-procedures-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ManagementProceduresSummaryComponent {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  permitTask$ = this.route.data.pipe(pluck<ManagementProceduresDefinitionData, 'permitTask'>('permitTask'));

  constructor(
    readonly store: PermitApplicationStore,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
  ) {}
}
