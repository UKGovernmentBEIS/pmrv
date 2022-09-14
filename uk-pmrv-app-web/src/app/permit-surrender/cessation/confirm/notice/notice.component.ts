import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, switchMap } from 'rxjs';

import { PendingRequestService } from '../../../../core/guards/pending-request.service';
import { PendingRequest } from '../../../../core/interfaces/pending-request.interface';
import { PERMIT_SURRENDER_TASK_FORM } from '../../../core/permit-surrender-task-form.token';
import { PermitSurrenderStore } from '../../../store/permit-surrender.store';
import { OfficialNoticeTypeMap } from '../../core/cessation';
import { noticeFormProvider } from './notice-form.provider';

@Component({
  selector: 'app-notice',
  templateUrl: './notice.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [noticeFormProvider],
})
export class NoticeComponent implements PendingRequest {
  readonly officialNoticeTypeMap = OfficialNoticeTypeMap;

  constructor(
    @Inject(PERMIT_SURRENDER_TASK_FORM) readonly form: FormGroup,
    readonly store: PermitSurrenderStore,
    readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onContinue(): void {
    if (!this.form.dirty) {
      this.router.navigate(['../notes'], { relativeTo: this.route });
    } else {
      const noticeType = this.form.value.noticeType;

      this.store
        .pipe(
          first(),
          switchMap((state) =>
            this.store.postSaveCessation(
              {
                ...state.cessation,
                noticeType,
              },
              false,
            ),
          ),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.router.navigate(['../notes'], { relativeTo: this.route }));
    }
  }
}
