import { HttpErrorResponse, HttpStatusCode } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { AbstractControl, FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import {
  BehaviorSubject,
  filter,
  first,
  map,
  Observable,
  pluck,
  shareReplay,
  switchMap,
  switchMapTo,
  takeUntil,
  tap,
  withLatestFrom,
} from 'rxjs';

import { GovukSelectOption, GovukValidators } from 'govuk-components';

import { AccountVerificationBodyService, VerificationBodyNameInfoDTO } from 'pmrv-api';

import { DestroySubject } from '../../../../core/services/destroy-subject.service';
import { catchBadRequest, catchElseRethrow, ErrorCode } from '../../../../error/business-errors';
import { ConcurrencyErrorService } from '../../../../error/concurrency-error/concurrency-error.service';
import { BackLinkService } from '../../../../shared/back-link/back-link.service';
import {
  appointedVerificationBodyError,
  saveNotFoundVerificationBodyError,
  savePartiallyNotFoundOperatorError,
} from '../../errors/concurrency-error';

@Component({
  selector: 'app-appoint',
  templateUrl: './appoint.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [BackLinkService, DestroySubject],
})
export class AppointComponent implements OnInit {
  form = this.fb.group({
    verificationBodyId: [null, GovukValidators.required('Select a verification body')],
  });
  activeBodies$: Observable<GovukSelectOption<number>[]>;
  isSummaryDisplayed$ = new BehaviorSubject<boolean>(false);
  appointedAccount$: Observable<string>;
  currentVerificationBody$: Observable<VerificationBodyNameInfoDTO> = (
    this.route.data as Observable<{
      verificationBody: VerificationBodyNameInfoDTO;
    }>
  ).pipe(pluck('verificationBody'));

  private accountId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('accountId'))));

  constructor(
    private readonly fb: FormBuilder,
    private readonly destroy$: DestroySubject,
    private readonly route: ActivatedRoute,
    private readonly accountVerificationBodyService: AccountVerificationBodyService,
    private readonly concurrencyErrorService: ConcurrencyErrorService,
    private readonly backLinkService: BackLinkService,
  ) {}

  ngOnInit(): void {
    this.backLinkService.show();
    this.activeBodies$ = this.accountId$.pipe(
      switchMap((accountId) => this.accountVerificationBodyService.getActiveVerificationBodiesUsingGET(accountId)),
      map((bodies) => bodies.map((body) => ({ text: body.name, value: body.id }))),
      shareReplay({ bufferSize: 1, refCount: true }),
    );

    this.currentVerificationBody$
      .pipe(
        takeUntil(this.destroy$),
        filter((value) => !!value),
      )
      .subscribe((verificationBody) => {
        this.form.get('verificationBodyId').setValue(verificationBody.id);
        this.form
          .get('verificationBodyId')
          .setValidators([
            GovukValidators.required('Select a verification body'),
            GovukValidators.builder(
              'This verification body is already appointed. Please select another one.',
              (control: AbstractControl) => (control.value === verificationBody.id ? { duplicate: true } : null),
            ),
          ]);
      });
  }

  onSubmit(): void {
    if (this.form.valid) {
      const value = this.form.value;

      this.appointedAccount$ = this.accountId$.pipe(
        first(),
        withLatestFrom(this.currentVerificationBody$),
        switchMap(([accountId, currentVerificationBody]) =>
          currentVerificationBody
            ? this.accountVerificationBodyService.replaceVerificationBodyToAccountUsingPATCH(accountId, value)
            : this.accountVerificationBodyService.appointVerificationBodyToAccountUsingPOST(accountId, value),
        ),
        switchMapTo(this.activeBodies$),
        map((bodies) => bodies.find((body) => body.value === value.verificationBodyId).text),
        tap((name) => {
          if (name) {
            this.backLinkService.hide();
          }
        }),
        catchBadRequest(ErrorCode.ACCOUNT1006, () =>
          this.accountId$.pipe(
            first(),
            switchMap((accountId) => this.concurrencyErrorService.showError(appointedVerificationBodyError(accountId))),
          ),
        ),
        catchBadRequest(ErrorCode.ACCOUNT1007, () =>
          this.accountId$.pipe(
            first(),
            switchMap((accountId) =>
              this.concurrencyErrorService.showError(savePartiallyNotFoundOperatorError(accountId)),
            ),
          ),
        ),
        catchElseRethrow(
          (res: HttpErrorResponse) => res.status === HttpStatusCode.NotFound,
          () =>
            this.accountId$.pipe(
              first(),
              withLatestFrom(this.currentVerificationBody$),
              switchMap(([accountId, currentVerificationBody]) =>
                this.concurrencyErrorService.showError(
                  currentVerificationBody
                    ? savePartiallyNotFoundOperatorError(accountId)
                    : saveNotFoundVerificationBodyError(accountId),
                ),
              ),
            ),
        ),
      );
    } else {
      this.isSummaryDisplayed$.next(true);
      this.form.markAllAsTouched();
    }
  }
}
