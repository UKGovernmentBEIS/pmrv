import { HttpStatusCode } from '@angular/common/http';
import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { BehaviorSubject, first, map, switchMap } from 'rxjs';

import { VerificationBodiesService } from 'pmrv-api';

import { catchElseRethrow } from '../../error/business-errors';
import { ConcurrencyErrorService } from '../../error/concurrency-error/concurrency-error.service';
import { saveNotFoundVerificationBodyError } from '../errors/concurrency-error';

@Component({
  selector: 'app-delete',
  templateUrl: './delete.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DeleteComponent {
  isConfirmationDisplayed$ = new BehaviorSubject<boolean>(false);

  constructor(
    private readonly verificationBodiesService: VerificationBodiesService,
    private readonly route: ActivatedRoute,
    private readonly concurrencyErrorService: ConcurrencyErrorService,
  ) {}

  delete(): void {
    this.route.paramMap
      .pipe(
        first(),
        map((paramMap) => Number(paramMap.get('verificationBodyId'))),
        switchMap((verificationBodyId) =>
          this.verificationBodiesService.deleteVerificationBodyByIdUsingDELETE(verificationBodyId),
        ),
        catchElseRethrow(
          (res) => res.status === HttpStatusCode.NotFound,
          () => this.concurrencyErrorService.showError(saveNotFoundVerificationBodyError),
        ),
      )
      .subscribe(() => this.isConfirmationDisplayed$.next(true));
  }
}
