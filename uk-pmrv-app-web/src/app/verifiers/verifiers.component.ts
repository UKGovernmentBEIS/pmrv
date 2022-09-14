import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { FormArray, FormBuilder, FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import {
  BehaviorSubject,
  distinctUntilChanged,
  merge,
  Observable,
  pluck,
  shareReplay,
  Subject,
  switchMap,
  switchMapTo,
} from 'rxjs';

import { GovukSelectOption } from 'govuk-components';

import {
  AccountContactDTO,
  AccountContactVbInfoResponse,
  UserAuthorityInfoDTO,
  UsersAuthoritiesInfoDTO,
  VBSiteContactsService,
  VerifierAuthoritiesService,
  VerifierAuthorityUpdateDTO,
} from 'pmrv-api';

import { AuthService } from '../core/services/auth.service';
import { catchBadRequest, ErrorCode } from '../error/business-errors';
import { ConcurrencyErrorService } from '../error/concurrency-error/concurrency-error.service';
import { savePartiallyNotFoundSiteContactsError, savePartiallyNotFoundVerifierError } from './errors/concurrency-error';

@Component({
  selector: 'app-verifiers',
  templateUrl: './verifiers.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class VerifiersComponent implements OnInit {
  siteContact$: Observable<AccountContactVbInfoResponse>;
  siteContactsPage$ = new BehaviorSubject<number>(null);
  siteContactsPageSize = 50;

  refresh$ = new Subject<void>();
  isSummaryDisplayed$ = new BehaviorSubject<boolean>(false);
  addNewUserForm = this.fb.group({
    roleCode: ['verifier'],
  });
  verifiersAuthorities$: Observable<UsersAuthoritiesInfoDTO>;
  verifiers$: Observable<UserAuthorityInfoDTO[]>;
  isEditable$: Observable<boolean>;
  verifiersForm: FormGroup;

  roleCodes: GovukSelectOption<string>[] = [
    { text: 'Verifier admin', value: 'verifier_admin' },
    { text: 'Verifier', value: 'verifier' },
  ];

  constructor(
    readonly authService: AuthService,
    private readonly fb: FormBuilder,
    private readonly verifierAuthoritiesService: VerifierAuthoritiesService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly vbSiteContactsService: VBSiteContactsService,
    private readonly concurrencyErrorService: ConcurrencyErrorService,
  ) {}

  ngOnInit(): void {
    this.verifiersAuthorities$ = merge(
      (this.route.data as Observable<{ verifiers: UsersAuthoritiesInfoDTO }>).pipe(pluck('verifiers')),
      this.refresh$.pipe(switchMapTo(this.verifierAuthoritiesService.getVerifierAuthoritiesUsingGET())),
    ).pipe(shareReplay({ bufferSize: 1, refCount: true }));

    this.verifiers$ = this.verifiersAuthorities$.pipe(pluck('authorities'));
    this.isEditable$ = this.verifiersAuthorities$.pipe(pluck('editable'));

    this.siteContact$ = merge(
      this.refresh$.pipe(switchMapTo(this.siteContactsPage$)),
      this.siteContactsPage$.pipe(distinctUntilChanged()),
    ).pipe(
      switchMap((page) => this.vbSiteContactsService.getVbSiteContactsUsingGET(page - 1, this.siteContactsPageSize)),
      shareReplay({ bufferSize: 1, refCount: true }),
    );
  }

  addNewUser(): void {
    this.router.navigate(['add'], { relativeTo: this.route, queryParams: this.addNewUserForm.value });
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

      this.verifierAuthoritiesService
        .updateVerifierAuthoritiesUsingPOST(updatedVerifiersAuthorities)
        .pipe(
          catchBadRequest(ErrorCode.AUTHORITY1006, () =>
            this.concurrencyErrorService.showError(savePartiallyNotFoundVerifierError),
          ),
        )
        .subscribe(() => this.refresh$.next());
    }
  }

  saveSiteContacts(contacts: AccountContactDTO[]): void {
    this.vbSiteContactsService
      .updateVbSiteContactsUsingPOST(contacts)
      .pipe(
        catchBadRequest([ErrorCode.ACCOUNT1005, ErrorCode.AUTHORITY1006], () =>
          this.concurrencyErrorService.showError(savePartiallyNotFoundSiteContactsError),
        ),
      )
      .subscribe(() => this.refresh$.next());
  }
}
