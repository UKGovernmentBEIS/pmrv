import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Router } from '@angular/router';

import { filter, map, Observable } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { AerService } from '@tasks/aer/core/aer.service';

@Component({
  selector: 'app-confidentiality-statement-summary',
  templateUrl: './confidentiality-statement-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class ConfidentialityStatementSummaryComponent {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  hasConfidentialSections$: Observable<boolean> = this.aerService.getTask('confidentialityStatement').pipe(
    filter((item) => !!item),
    map((item) => !!item.exist),
  );

  constructor(readonly aerService: AerService, private readonly router: Router) {}
}
