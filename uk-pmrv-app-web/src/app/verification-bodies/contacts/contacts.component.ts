import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';
import { FormArray, FormGroup } from '@angular/forms';

import { BehaviorSubject, first, merge, Observable, shareReplay, Subject, switchMap, switchMapTo } from 'rxjs';

import { UsersAuthoritiesInfoDTO, VerifierAuthoritiesService, VerifierAuthorityUpdateDTO } from 'pmrv-api';

import { catchBadRequest, ErrorCode } from '../../error/business-errors';
import { ConcurrencyErrorService } from '../../error/concurrency-error/concurrency-error.service';
import { savePartiallyNotFoundVerifierError } from '../../verifiers/errors/concurrency-error';

@Component({
  selector: 'app-contacts',
  template: `
    <govuk-error-summary *ngIf="isSummaryDisplayed$ | async" [form]="verifiersForm"></govuk-error-summary>
    <a routerLink="add-contact" govukButton>Add new verifier admin</a>
    <app-verifiers-table
      [verifiersAuthorities]="verifiersAuthorities$"
      (verifiersFormSubmitted)="onVerifiersFormSubmitted($event)"
      (discard)="this.refresh$.next()"
    ></app-verifiers-table>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ContactsComponent implements OnInit {
  @Input() verificationBodyId: Observable<number>;

  isSummaryDisplayed$ = new BehaviorSubject<boolean>(false);
  refresh$ = new Subject<void>();
  verifiersAuthorities$: Observable<UsersAuthoritiesInfoDTO>;
  verifiersForm: FormGroup;

  constructor(
    private readonly concurrencyErrorService: ConcurrencyErrorService,
    private readonly verifierAuthoritiesService: VerifierAuthoritiesService,
  ) {}

  ngOnInit(): void {
    const verifiersAuthorities$ = this.verificationBodyId.pipe(
      first(),
      switchMap((vbId) => this.verifierAuthoritiesService.getVerifierAuthoritiesByVerificationBodyIdUsingGET(vbId)),
    );
    this.verifiersAuthorities$ = merge(
      verifiersAuthorities$,
      this.refresh$.pipe(
        switchMapTo(this.verificationBodyId),
        switchMap((vbId) => this.verifierAuthoritiesService.getVerifierAuthoritiesByVerificationBodyIdUsingGET(vbId)),
      ),
    ).pipe(shareReplay({ bufferSize: 1, refCount: true }));
  }

  onVerifiersFormSubmitted(verifiersForm: FormGroup) {
    if (!verifiersForm.valid) {
      this.isSummaryDisplayed$.next(true);
      this.verifiersForm = verifiersForm;
    } else {
      this.isSummaryDisplayed$.next(false);
      const updatedVerifiersAuthorities: VerifierAuthorityUpdateDTO[] = (
        verifiersForm.get('verifiersArray') as FormArray
      ).controls
        .filter((control) => control.dirty)
        .map((control) => ({
          authorityStatus: control.value.authorityStatus,
          roleCode: control.value.roleCode,
          userId: control.value.userId,
        }));

      this.verificationBodyId
        .pipe(
          first(),
          switchMap((vbId) =>
            this.verifierAuthoritiesService.updateVerifierAuthoritiesByVerificationBodyIdUsingPOST(
              vbId,
              updatedVerifiersAuthorities,
            ),
          ),
          catchBadRequest(ErrorCode.AUTHORITY1006, () =>
            this.concurrencyErrorService.showError(savePartiallyNotFoundVerifierError),
          ),
        )
        .subscribe(() => this.refresh$.next());
    }
  }
}
