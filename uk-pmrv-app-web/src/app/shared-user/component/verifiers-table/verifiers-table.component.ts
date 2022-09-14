import { ChangeDetectionStrategy, Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormArray, FormBuilder, FormGroup } from '@angular/forms';

import { Observable, pluck } from 'rxjs';

import { GovukSelectOption, GovukTableColumn, GovukValidators } from 'govuk-components';

import { UserAuthorityInfoDTO, UsersAuthoritiesInfoDTO } from 'pmrv-api';

import { AuthService } from '../../../core/services/auth.service';

type TableData = UserAuthorityInfoDTO & { name: string; deleteBtn: null };

const activeVerifierAdminValidator = GovukValidators.builder(
  'You must have an active verifier admin on your account',
  (verifiers: FormArray) =>
    (verifiers.value as UserAuthorityInfoDTO[])?.find(
      (item) => item.roleCode === 'verifier_admin' && item.authorityStatus === 'ACTIVE',
    )
      ? null
      : { noActiveVerifier: true },
);

@Component({
  selector: 'app-verifiers-table',
  templateUrl: './verifiers-table.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class VerifiersTableComponent implements OnInit {
  @Input() verifiersAuthorities: Observable<UsersAuthoritiesInfoDTO>;
  @Output() readonly verifiersFormSubmitted = new EventEmitter<FormGroup>();
  @Output() readonly discard = new EventEmitter<void>();

  verifiers$: Observable<UserAuthorityInfoDTO[]>;
  isEditable$: Observable<boolean>;
  roleCodes: GovukSelectOption<string>[] = [
    { text: 'Verifier admin', value: 'verifier_admin' },
    { text: 'Verifier', value: 'verifier' },
  ];
  authorityStatuses: GovukSelectOption<string>[] = [
    { text: 'Active', value: 'ACTIVE' },
    { text: 'Disabled', value: 'DISABLED' },
  ];
  authorityStatusesAccepted: GovukSelectOption<string>[] = [
    { text: 'Accepted', value: 'ACCEPTED' },
    { text: 'Active', value: 'ACTIVE' },
  ];
  editableCols: GovukTableColumn<TableData>[] = [
    { field: 'name', header: 'Name', isSortable: true },
    { field: 'roleName', header: 'User Type' },
    { field: 'authorityStatus', header: 'Account status' },
    { field: 'deleteBtn', header: undefined },
  ];
  nonEditableCols = this.editableCols.slice(0, 2);
  verifiersForm = this.fb.group({
    verifiersArray: this.fb.array([], { validators: activeVerifierAdminValidator }),
  });

  constructor(readonly authService: AuthService, private readonly fb: FormBuilder) {}

  ngOnInit(): void {
    this.isEditable$ = this.verifiersAuthorities.pipe(pluck('editable'));
    this.verifiers$ = this.verifiersAuthorities.pipe(pluck('authorities'));
  }

  get verifiersArray() {
    return this.verifiersForm.get('verifiersArray') as FormArray;
  }

  onSubmit() {
    if (this.verifiersForm.dirty) {
      this.verifiersFormSubmitted.emit(this.verifiersForm);
    }
  }
}
