import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Router } from '@angular/router';

import { filter, map, Observable } from 'rxjs';

import { DestroySubject } from '../../../core/services/destroy-subject.service';
import { PermitApplicationStore } from '../../store/permit-application.store';

@Component({
  selector: 'app-confidentiality-statement-summary',
  templateUrl: './confidentiality-statement-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class ConfidentialityStatementSummaryComponent {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  hasConfidentialSections$: Observable<boolean> = this.store.getTask('confidentialityStatement').pipe(
    filter((item) => !!item),
    map((item) => !!item.exist),
  );

  constructor(readonly store: PermitApplicationStore, private readonly router: Router) {}
}
