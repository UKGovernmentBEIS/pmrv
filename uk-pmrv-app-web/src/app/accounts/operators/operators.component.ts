import { HttpStatusCode } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { FormArray, FormBuilder, FormGroup, ValidatorFn } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import {
  BehaviorSubject,
  combineLatest,
  first,
  map,
  merge,
  Observable,
  of,
  pluck,
  shareReplay,
  Subject,
  switchMap,
  switchMapTo,
  takeUntil,
  tap,
} from 'rxjs';

import { AuthService } from '@core/services/auth.service';
import { DestroySubject } from '@core/services/destroy-subject.service';

import { GovukSelectOption, GovukTableColumn, GovukValidators } from 'govuk-components';

import {
  AccountOperatorAuthorityUpdateDTO,
  AccountOperatorsUsersAuthoritiesInfoDTO,
  AccountVerificationBodyService,
  AuthoritiesService,
  OperatorAuthoritiesService,
  UserAuthorityInfoDTO,
} from 'pmrv-api';

import { catchBadRequest, catchElseRethrow, ErrorCode } from '../../error/business-errors';
import { ConcurrencyErrorService } from '../../error/concurrency-error/concurrency-error.service';
import { accountFinalStatuses } from '../core/accountFinalStatuses';
import { savePartiallyNotFoundOperatorError } from './errors/concurrency-error';

@Component({
  selector: 'app-operators',
  templateUrl: './operators.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class OperatorsComponent implements OnInit {
  private accountId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('accountId'))));

  isAccountEditable$: Observable<boolean> = this.route.data.pipe(
    map((data) => data.account),
    pluck('status'),
    map((status) => accountFinalStatuses(status)),
  );
  accountAuthorities$: Observable<UserAuthorityInfoDTO[]>;
  contactType$: Observable<{ [key: string]: string }>;
  isEditable$: Observable<boolean>;
  isSummaryDisplayed$ = new BehaviorSubject<boolean>(false);
  addUserForm: FormGroup = this.fb.group({ userType: ['operator'] });
  editableCols: GovukTableColumn[] = [
    { field: 'name', header: 'Name', isSortable: true },
    { field: 'roleName', header: 'User Type', widthClass: 'app-column-width-20-per' },
    { field: 'PRIMARY', header: 'Primary contact' },
    { field: 'SECONDARY', header: 'Secondary contact' },
    { field: 'SERVICE', header: 'Service contact' },
    { field: 'FINANCIAL', header: 'Financial contact' },
    { field: 'authorityStatus', header: 'Account status', widthClass: 'app-column-width-15-per' },
    { field: 'deleteBtn', header: undefined },
  ];
  nonEditableCols = this.editableCols.slice(0, 6);
  userTypes: GovukSelectOption<string>[] = [
    { text: 'Operator admin', value: 'operator_admin' },
    { text: 'Operator', value: 'operator' },
  ];
  authorityStatuses: GovukSelectOption<UserAuthorityInfoDTO['authorityStatus']>[] = [
    { text: 'Active', value: 'ACTIVE' },
    { text: 'Disabled', value: 'DISABLED' },
  ];
  authorityStatusesAccepted: GovukSelectOption<UserAuthorityInfoDTO['authorityStatus']>[] = [
    { text: 'Accepted', value: 'ACCEPTED' },
    { text: 'Active', value: 'ACTIVE' },
  ];
  hasVerificationBody$: Observable<boolean>;
  userType$: Observable<GovukSelectOption<string>[]>;
  private validators = [
    this.activeOperatorAdminValidator('You must have an active operator admin on your account'),
    this.activeContactValidator('PRIMARY'),
    this.primarySecondaryValidator(
      'You cannot assign the same user as a primary and secondary contact on your account',
    ),
    this.activeContactValidator('SERVICE'),
    this.activeContactValidator('FINANCIAL'),
  ];
  usersForm = this.fb.group(
    {
      usersArray: this.fb.array([]),
      contactTypes: this.fb.group(
        {
          PRIMARY: [null, GovukValidators.required('You must have a primary contact on your account')],
          SECONDARY: [],
          SERVICE: [null, GovukValidators.required('You must have a service contact on your account')],
          FINANCIAL: [null, GovukValidators.required('You must have a financial contact on your account')],
        },
        { updateOn: 'change' },
      ),
    },
    { validators: this.validators },
  );
  refresh$ = new Subject<void>();

  constructor(
    private readonly fb: FormBuilder,
    readonly authService: AuthService,
    private readonly authoritiesService: AuthoritiesService,
    private readonly operatorAuthoritiesService: OperatorAuthoritiesService,
    private readonly accountVerificationBodyService: AccountVerificationBodyService,
    private readonly concurrencyErrorService: ConcurrencyErrorService,
    private readonly router: Router,
    private readonly destroy$: DestroySubject,
    private readonly route: ActivatedRoute,
  ) {}

  get usersArray(): FormArray {
    return this.usersForm.get('usersArray') as FormArray;
  }

  get contactTypes(): FormGroup {
    return this.usersForm.get('contactTypes') as FormGroup;
  }

  ngOnInit(): void {
    const operatorsManagement$ = merge(
      this.route.data.pipe(map((data: { users: AccountOperatorsUsersAuthoritiesInfoDTO }) => data.users)),
      this.refresh$.pipe(
        switchMapTo(this.accountId$),
        switchMap((accountId) => this.operatorAuthoritiesService.getAccountOperatorAuthoritiesUsingGET(accountId)),
      ),
    ).pipe(shareReplay({ bufferSize: 1, refCount: true }));
    const verificationBody$ = this.accountId$.pipe(
      switchMap((accountId) =>
        this.accountVerificationBodyService.getVerificationBodyOfAccountUsingGET(accountId).pipe(
          catchElseRethrow(
            (error) => error.status === HttpStatusCode.NotFound,
            () => of(null),
          ),
        ),
      ),
      shareReplay({ bufferSize: 1, refCount: true }),
    );
    this.accountAuthorities$ = combineLatest([
      operatorsManagement$.pipe(pluck('authorities')),
      verificationBody$.pipe(
        map((body) =>
          body ? [{ firstName: body.name, lastName: '', roleName: 'Verifier', roleCode: 'verifier' }] : [],
        ),
      ),
    ]).pipe(map(([operators, bodies]) => operators.concat(bodies)));
    this.contactType$ = operatorsManagement$.pipe(pluck('contactTypes'));
    this.isEditable$ = operatorsManagement$.pipe(pluck('editable'));
    this.userType$ = this.accountId$.pipe(
      switchMap((accountId) => this.authoritiesService.getOperatorRoleCodesUsingGET(accountId)),
      map((res) => res.map((role) => ({ text: role.name, value: role.code }))),
    );
    this.hasVerificationBody$ = verificationBody$.pipe(map((value) => !!value));
    this.contactType$
      .pipe(takeUntil(this.destroy$))
      .subscribe((contactTypes) => this.contactTypes.patchValue(contactTypes));
  }

  addUser(userType: string): void {
    this.router.navigate(['users/add', userType], { relativeTo: this.route });
  }

  saveUsers(): void {
    if (!this.usersForm.dirty) {
      return;
    }
    if (!this.usersForm.valid) {
      this.usersForm.markAllAsTouched();
      this.isSummaryDisplayed$.next(true);
    } else {
      this.accountId$
        .pipe(
          first(),
          switchMap((accountId) =>
            this.operatorAuthoritiesService.updateAccountOperatorAuthoritiesUsingPOST(accountId, {
              accountOperatorAuthorityUpdateList: this.usersArray.controls
                .filter((users) => users.dirty)
                .map((user) => ({
                  userId: user.value.userId,
                  roleCode: user.value.roleCode,
                  authorityStatus: user.value.authorityStatus,
                })),
              contactTypes: this.contactTypes.value,
            }),
          ),
          tap(() => this.usersForm.markAsPristine()),
          catchBadRequest(ErrorCode.AUTHORITY1004, () =>
            this.accountId$.pipe(
              switchMap((accountId) =>
                this.concurrencyErrorService.showError(savePartiallyNotFoundOperatorError(accountId)),
              ),
            ),
          ),
        )
        .subscribe(() => this.refresh$.next());
    }
  }

  private activeOperatorAdminValidator(message: string): ValidatorFn {
    return GovukValidators.builder(message, (group: FormGroup) =>
      (group.get('usersArray').value as Array<AccountOperatorAuthorityUpdateDTO>).find(
        (item) => item?.roleCode === 'operator_admin' && item.authorityStatus === 'ACTIVE',
      )
        ? null
        : { noActiveOperatorAdmin: true },
    );
  }

  private activeContactValidator(contactType: string): ValidatorFn {
    return GovukValidators.builder(
      `You must have a ${contactType.toLowerCase()} contact on your account`,
      (group: FormGroup) =>
        (group.get('usersArray').value as Array<AccountOperatorAuthorityUpdateDTO>).find(
          (item) =>
            item.authorityStatus !== 'ACTIVE' && item.userId === group.get('contactTypes').get(contactType).value,
        )
          ? { [`${contactType.toLowerCase()}NotActive`]: true }
          : null,
    );
  }

  private primarySecondaryValidator(message: string): ValidatorFn {
    return GovukValidators.builder(message, (group: FormGroup) =>
      group.get('contactTypes').get('PRIMARY').value === group.get('contactTypes').get('SECONDARY').value
        ? { samePrimarySecondary: true }
        : null,
    );
  }
}
