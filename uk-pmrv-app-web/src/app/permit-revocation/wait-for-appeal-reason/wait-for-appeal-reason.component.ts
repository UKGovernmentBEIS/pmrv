import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, Observable, startWith } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PermitRevocationState } from '@permit-revocation/store/permit-revocation.state';
import { PermitRevocationStore } from '@permit-revocation/store/permit-revocation-store';
import { BackLinkService } from '@shared/back-link/back-link.service';

import { PERMIT_REVOCATION_WITHDRAW_FORM, withdrawFormProvider } from './wait-for-appeal-form.provider';

@Component({
  selector: 'app-wait-for-appeal-reason',
  templateUrl: './wait-for-appeal-reason.component.html',
  providers: [BackLinkService, withdrawFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class WaitForAppealReasonComponent implements OnInit {
  readonly isFileUploaded$: Observable<boolean> = this.form.get('files').valueChanges.pipe(
    startWith(this.form.get('files').value),
    map((value) => value?.length > 0),
  );

  getDownloadUrl() {
    return this.store.createBaseFileDownloadUrl();
  }

  constructor(
    readonly route: ActivatedRoute,
    private readonly backlinkService: BackLinkService,
    readonly store: PermitRevocationStore,
    @Inject(PERMIT_REVOCATION_WITHDRAW_FORM) readonly form: FormGroup,
    private readonly router: Router,
    private readonly pendingRequest: PendingRequestService,
  ) {}

  ngOnInit(): void {
    this.backlinkService.show();
  }

  onSubmit() {
    if (this.form.valid) {
      const reason = this.form.get('reason').value;
      const files: string[] = this.form.get('files').value.map((file) => file.uuid);
      let state: PermitRevocationState = this.store.getValue();
      const addFiles: { [key: string]: string } = {};

      for (const file of this.form.get('files').value) {
        if (file) {
          const found = Object.keys(state.revocationAttachments).some((uuid) => uuid === file.uuid);
          if (!found) {
            addFiles[file.uuid] = file.file.name;
            state = { ...state, revocationAttachments: { ...state.revocationAttachments, ...addFiles } };
          }
        }
      }
      this.store
        .postPermitRevocationWithdraw({ ...state, reason, withdrawFiles: files })
        .pipe(first(), this.pendingRequest.trackRequest())
        .subscribe(() =>
          this.router.navigate(['..', 'summary'], { relativeTo: this.route, state: { notification: true } }),
        );
    }
  }
}
