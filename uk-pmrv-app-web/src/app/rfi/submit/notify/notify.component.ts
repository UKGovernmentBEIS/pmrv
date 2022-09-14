import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, Observable, pluck, shareReplay, switchMap } from 'rxjs';

import {
  NotifyAccountOperatorUsersInfo,
  toAccountOperatorUser,
  toNotifyAccountOperatorUsersInfo,
} from '@shared/components/notify-operator/notify-operator';
import { UserFullNamePipe } from '@shared/pipes/user-full-name.pipe';

import { GovukSelectOption } from 'govuk-components';

import { OperatorAuthoritiesService, TasksAssignmentService } from 'pmrv-api';

import { RfiStore } from '../../store/rfi.store';
import { NOTIFY_FORM, notifyFormFactory } from './notify-form.provider';

@Component({
  selector: 'app-notify-operator',
  templateUrl: './notify.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [notifyFormFactory, UserFullNamePipe],
})
export class NotifyComponent implements OnInit {
  private readonly taskId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId'))));
  objectKeys = Object.keys;

  accountPrimaryContactUsersInfo$: Observable<NotifyAccountOperatorUsersInfo>;
  otherOperatorUsersInfo$: Observable<NotifyAccountOperatorUsersInfo>;
  assignees$: Observable<GovukSelectOption<string>[]>;

  constructor(
    @Inject(NOTIFY_FORM) readonly form: FormGroup,
    private readonly operatorAuthoritiesService: OperatorAuthoritiesService,
    private readonly tasksAssignmentService: TasksAssignmentService,
    private readonly store: RfiStore,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly fullNamePipe: UserFullNamePipe,
  ) {}

  ngOnInit(): void {
    const accountOperatorAuthorities$ = this.store.pipe(
      first(),
      pluck('accountId'),
      switchMap((accountId: number) =>
        this.operatorAuthoritiesService.getAccountOperatorAuthoritiesUsingGET(accountId),
      ),
      shareReplay({ bufferSize: 1, refCount: true }),
    );

    const users$ = combineLatest([
      accountOperatorAuthorities$.pipe(pluck('authorities')),
      accountOperatorAuthorities$.pipe(pluck('contactTypes')),
    ]).pipe(
      map(([authorities, contactTypes]) =>
        authorities
          .filter((authority) => authority.authorityStatus === 'ACTIVE')
          .map((authority) => toAccountOperatorUser(authority, contactTypes)),
      ),
    );

    this.accountPrimaryContactUsersInfo$ = users$.pipe(
      map((users) =>
        users.filter((user) => user.contactTypes.includes('PRIMARY')).reduce(toNotifyAccountOperatorUsersInfo, {}),
      ),
    );

    this.otherOperatorUsersInfo$ = users$.pipe(
      map((users) =>
        users.filter((user) => !user.contactTypes.includes('PRIMARY')).reduce(toNotifyAccountOperatorUsersInfo, {}),
      ),
    );

    this.assignees$ = this.taskId$.pipe(
      switchMap((id: number) => this.tasksAssignmentService.getCandidateAssigneesByTaskIdUsingGET(id)),
      map((assignees) =>
        assignees.map((assignee) => ({ text: this.fullNamePipe.transform(assignee), value: assignee.id })),
      ),
      shareReplay({ bufferSize: 1, refCount: true }),
    );
  }

  onSubmit(): void {
    if (!this.form.dirty) {
      this.router.navigate(['../answers'], { relativeTo: this.route });
    } else {
      combineLatest([this.assignees$, this.accountPrimaryContactUsersInfo$, this.otherOperatorUsersInfo$, this.store])
        .pipe(
          first(),
          map(([assignees, accountPrimaryContactUsersInfo, otherOperatorUsersInfo, state]) => {
            const selectedSignatory = this.form.get('assignee').value;
            const selectedSignatoryUserInfo = assignees.find((assignee) => assignee.value === selectedSignatory);

            const selectedOperators = this.form.get('users').value;
            const otherSelectedOperatorUsersInfo =
              selectedOperators?.length > 0
                ? Object.keys(otherOperatorUsersInfo)
                    .filter((key) => selectedOperators.includes(key))
                    .reduce((res, key) => ({ ...res, [key]: otherOperatorUsersInfo[key] }), {})
                : {};

            this.store.setState({
              ...state,
              rfiSubmitPayload: {
                ...state.rfiSubmitPayload,
                operators: selectedOperators,
                signatory: selectedSignatory,
              },
              usersInfo: {
                ...accountPrimaryContactUsersInfo,
                ...otherSelectedOperatorUsersInfo,
                [selectedSignatory]: { name: selectedSignatoryUserInfo.text },
              },
            });
          }),
        )
        .subscribe(() => this.router.navigate(['../answers'], { relativeTo: this.route }));
    }
  }
}
