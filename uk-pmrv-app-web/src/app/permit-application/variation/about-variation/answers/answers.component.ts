import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { first, switchMap } from 'rxjs';

import { PendingRequestService } from '../../../../core/guards/pending-request.service';
import { DestroySubject } from '../../../../core/services/destroy-subject.service';
import { BreadcrumbService } from '../../../../shared/breadcrumbs/breadcrumb.service';
import { PermitApplicationStore } from '../../../store/permit-application.store';

@Component({
  selector: 'app-answers',
  templateUrl: './answers.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [BreadcrumbService, DestroySubject],
})
export class AnswersComponent {
  constructor(
    readonly store: PermitApplicationStore,
    readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  confirm(): void {
    this.store
      .pipe(
        first(),
        switchMap((state) => {
          return this.store.postVariationDetails(state.permitVariationDetails, true);
        }),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => this.router.navigate(['../summary'], { relativeTo: this.route, state: { notification: true } }));
  }
}
