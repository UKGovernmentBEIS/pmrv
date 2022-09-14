import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { FormArray, FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import {
  BehaviorSubject,
  map,
  merge,
  Observable,
  pluck,
  shareReplay,
  Subject,
  switchMapTo,
  takeUntil,
  tap,
} from 'rxjs';

import { GovukSelectOption, GovukTableColumn } from 'govuk-components';

import { RegulatorAuthoritiesService, RegulatorUserAuthorityInfoDTO, RegulatorUsersAuthoritiesInfoDTO } from 'pmrv-api';

import { AuthService } from '../core/services/auth.service';
import { DestroySubject } from '../core/services/destroy-subject.service';
import { catchBadRequest, ErrorCode } from '../error/business-errors';
import { ConcurrencyErrorService } from '../error/concurrency-error/concurrency-error.service';
import { savePartiallyNotFoundRegulatorError } from './errors/concurrency-error';

@Component({
  selector: 'app-regulators',
  templateUrl: './regulators.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class RegulatorsComponent implements OnInit {
  regulators$: Observable<RegulatorUserAuthorityInfoDTO[]>;
  isEditable$: Observable<boolean>;
  isSummaryDisplayed$ = new BehaviorSubject<boolean>(false);
  authorityStatuses: GovukSelectOption<string>[] = [
    { text: 'Active', value: 'ACTIVE' },
    { text: 'Disabled', value: 'DISABLED' },
  ];
  authorityStatusesAccepted: GovukSelectOption<string>[] = [
    { text: 'Accepted', value: 'ACCEPTED' },
    { text: 'Active', value: 'ACTIVE' },
  ];
  editableCols: GovukTableColumn[] = [
    { field: 'name', header: 'Name', isSortable: true },
    { field: 'jobTitle', header: 'Job title' },
    { field: 'authorityStatus', header: 'Account status' },
    { field: 'deleteBtn', header: undefined },
  ];
  nonEditableCols: GovukTableColumn[] = this.editableCols.slice(0, 2);
  regulatorsForm = this.fb.group({ regulatorsArray: this.fb.array([]) });
  refresh$ = new Subject<void>();

  constructor(
    readonly authService: AuthService,
    private readonly fb: FormBuilder,
    private readonly regulatorAuthoritiesService: RegulatorAuthoritiesService,
    private readonly route: ActivatedRoute,
    private readonly destroy$: DestroySubject,
    private readonly concurrencyErrorService: ConcurrencyErrorService,
  ) {}

  get regulatorsArray(): FormArray {
    return this.regulatorsForm.get('regulatorsArray') as FormArray;
  }

  ngOnInit(): void {
    const regulatorsManagement$ = merge(
      this.route.data.pipe(map((data: { regulators: RegulatorUsersAuthoritiesInfoDTO }) => data.regulators)),
      this.refresh$.pipe(switchMapTo(this.regulatorAuthoritiesService.getCaRegulatorsUsingGET())),
    ).pipe(takeUntil(this.destroy$), shareReplay({ bufferSize: 1, refCount: false }));
    this.regulators$ = regulatorsManagement$.pipe(pluck('caUsers'));
    this.isEditable$ = regulatorsManagement$.pipe(pluck('editable'));
  }

  saveRegulators(): void {
    if (!this.regulatorsForm.dirty) {
      return;
    }
    if (!this.regulatorsForm.valid) {
      this.isSummaryDisplayed$.next(true);
    } else {
      this.regulatorAuthoritiesService
        .updateCompetentAuthorityRegulatorUsersStatusUsingPOST(
          this.regulatorsArray.controls
            .filter((control) => control.dirty)
            .map((control) => ({
              authorityStatus: control.value.authorityStatus,
              userId: control.value.userId,
            })),
        )
        .pipe(
          catchBadRequest(ErrorCode.AUTHORITY1003, () =>
            this.concurrencyErrorService.showError(savePartiallyNotFoundRegulatorError),
          ),
          tap(() => this.regulatorsForm.markAsPristine()),
        )
        .subscribe(() => this.refresh$.next());
    }
  }
}
