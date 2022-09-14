import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, switchMap } from 'rxjs';

import { PendingRequestService } from '../../../core/guards/pending-request.service';
import { PERMIT_TASK_FORM } from '../../shared/permit-task-form.token';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { aboutVariationFormProvider } from './about-variation-form.provider';

@Component({
  selector: 'app-about-variation',
  templateUrl: './about-variation.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [aboutVariationFormProvider],
})
export class AboutVariationComponent {
  constructor(
    readonly store: PermitApplicationStore,
    @Inject(PERMIT_TASK_FORM) readonly form: FormGroup,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly pendingRequest: PendingRequestService,
  ) {}

  onContinue(): void {
    this.store
      .pipe(
        first(),
        switchMap((state) => {
          return this.store.postVariationDetails({
            ...state.permitVariationDetails,
            reason: this.form.get('reason').value,
          });
        }),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => this.router.navigate(['changes'], { relativeTo: this.route }));
  }
}
