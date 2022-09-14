import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, first, Observable, pluck, switchMap } from 'rxjs';

import { CaExternalContactDTO, CaExternalContactsService } from 'pmrv-api';

import { catchBadRequest, ErrorCode } from '../../../error/business-errors';
import { ConcurrencyErrorService } from '../../../error/concurrency-error/concurrency-error.service';
import { saveNotFoundExternalContactError } from '../../errors/concurrency-error';

@Component({
  selector: 'app-delete',
  templateUrl: './delete.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DeleteComponent {
  contact$: Observable<CaExternalContactDTO> = this.route.data.pipe(pluck('contact'));

  isConfirmationDisplayed$ = new BehaviorSubject<boolean>(false);

  constructor(
    private readonly externalContactsService: CaExternalContactsService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly concurrencyErrorService: ConcurrencyErrorService,
  ) {}

  deleteExternalContact(): void {
    this.contact$
      .pipe(
        first(),
        switchMap((contact) => this.externalContactsService.deleteCaExternalContactByIdUsingDELETE(contact.id)),
        catchBadRequest(ErrorCode.EXTCONTACT1000, () =>
          this.concurrencyErrorService.showError(saveNotFoundExternalContactError),
        ),
      )
      .subscribe(() => this.isConfirmationDisplayed$.next(true));
  }
}
