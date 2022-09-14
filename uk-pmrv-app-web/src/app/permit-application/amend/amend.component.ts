import { ChangeDetectionStrategy, Component } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject } from 'rxjs';

import { GovukValidators } from 'govuk-components';

import { PendingRequestService } from '../../core/guards/pending-request.service';
import { PendingRequest } from '../../core/interfaces/pending-request.interface';
import { PermitApplicationStore } from '../store/permit-application.store';
import { getAmendTaskStatusKey } from './amend';

@Component({
  selector: 'app-amend',
  templateUrl: './amend.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AmendComponent implements PendingRequest {
  section = this.route.snapshot.paramMap.get('section');
  form: FormGroup = this.fb.group({
    changes: [
      null,
      GovukValidators.required('Check the box to confirm you have made changes and want to mark as complete'),
    ],
  });
  displayErrorSummary$ = new BehaviorSubject<boolean>(false);

  constructor(
    readonly pendingRequest: PendingRequestService,
    readonly store: PermitApplicationStore,
    private readonly fb: FormBuilder,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
  ) {}

  confirm() {
    if (!this.form.valid) {
      this.displayErrorSummary$.next(true);
    } else {
      this.store
        .postStatus(getAmendTaskStatusKey(this.section), true)
        .pipe(this.pendingRequest.trackRequest())
        .subscribe(() => this.router.navigate([`../../amend/${this.section}/summary`], { relativeTo: this.route }));
    }
  }
}
