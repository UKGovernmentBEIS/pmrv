import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';

import { takeUntil, tap } from 'rxjs';
import { map } from 'rxjs';

import { AuthService } from '../../../core/services/auth.service';
import { DestroySubject } from '../../../core/services/destroy-subject.service';
import { BackLinkService } from '../../../shared/back-link/back-link.service';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { applicationDecisionNameMap, determinationTypeMap } from './decisionMap';

@Component({
  selector: 'app-decision-summary',
  templateUrl: './decision-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [BackLinkService, DestroySubject],
})
export class DecisionSummaryComponent implements OnInit {
  readonly determinationTypeMap = determinationTypeMap;
  readonly applicationDecisionNameMap = applicationDecisionNameMap;

  permitLink$ = this.authService.userStatus.pipe(map((user) => (user.roleType === 'OPERATOR' ? '../..' : '..')));
  emissions: { year: string; emissions: any }[];
  signatory: string;
  operators: string[];

  constructor(
    readonly store: PermitApplicationStore,
    private readonly authService: AuthService,
    private readonly backLinkService: BackLinkService,
    private readonly destroy$: DestroySubject,
  ) {}

  ngOnInit(): void {
    this.backLinkService.show();

    this.store
      .pipe(
        takeUntil(this.destroy$),
        tap((state) => {
          this.emissions =
            state.determination.annualEmissionsTargets &&
            Object.keys(state.determination.annualEmissionsTargets).map((key) => ({
              year: key,
              emissions: state.determination.annualEmissionsTargets[key],
            }));
          this.signatory = state.permitDecisionNotification.signatory;
          this.operators = Object.keys(state.usersInfo).filter((userId) => userId !== this.signatory);
        }),
      )
      .subscribe();
  }
}
