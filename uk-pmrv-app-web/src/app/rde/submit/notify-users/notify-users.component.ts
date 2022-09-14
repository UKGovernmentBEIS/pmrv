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

import { RdeStore } from '../../store/rde.store';
import { notifyUsersFormFactory, RDE_FORM } from './notify-users-form.provider';

@Component({
  selector: 'app-notify-users',
  templateUrl: './notify-users.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [notifyUsersFormFactory, UserFullNamePipe],
})
export class NotifyUsersComponent implements OnInit {
  readonly taskId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId'))));
  objectKeys = Object.keys;

  accountPrimaryContactUsersInfo$: Observable<NotifyAccountOperatorUsersInfo>;
  otherOperatorUsersInfo$: Observable<NotifyAccountOperatorUsersInfo>;
  assignees$: Observable<GovukSelectOption<string>[]>;

  constructor(
    @Inject(RDE_FORM) readonly form: FormGroup,
    private readonly operatorAuthoritiesService: OperatorAuthoritiesService,
    private readonly tasksAssignmentService: TasksAssignmentService,
    private readonly store: RdeStore,
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

  onContinue(): void {
    if (!this.form.dirty) {
      this.router.navigate(['../answers'], { relativeTo: this.route });
    } else {
      combineLatest([this.assignees$, this.accountPrimaryContactUsersInfo$, this.otherOperatorUsersInfo$, this.store])
        .pipe(
          first(),
          map(([assignees, accountPrimaryContactUsersInfo, otherOperatorUsersInfo, state]) => {
            const selectedSignatory = this.form.get('assignees').value;
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
              rdePayload: {
                ...state.rdePayload,
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
