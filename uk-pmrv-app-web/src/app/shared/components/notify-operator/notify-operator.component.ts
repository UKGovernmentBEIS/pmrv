import { ChangeDetectionStrategy, Component, Inject, Input, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';

import { BehaviorSubject, combineLatest, map, Observable, of, pluck, shareReplay, switchMap, tap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { BackLinkService } from '@shared/back-link/back-link.service';
import { requestTaskReassignedError, taskNotFoundError } from '@shared/errors/concurrency-error';
import { UserFullNamePipe } from '@shared/pipes/user-full-name.pipe';

import { GovukSelectOption } from 'govuk-components';

import {
  CaExternalContactDTO,
  CaExternalContactsService,
  OperatorAuthoritiesService,
  RequestTaskActionPayload,
  RequestTaskActionProcessDTO,
  TasksAssignmentService,
  TasksService,
} from 'pmrv-api';

import { catchTaskReassignedBadRequest } from '../../../error/business-errors';
import { ConcurrencyErrorService } from '../../../error/concurrency-error/concurrency-error.service';
import { catchNotFoundRequest, ErrorCode } from '../../../error/not-found-error';
import {
  NotifyAccountOperatorUsersInfo,
  toAccountOperatorUser,
  toNotifyAccountOperatorUsersInfo,
} from './notify-operator';
import { NOTIFY_OPERATOR_FORM, notifyOperatorFormFactory } from './notify-operator-form.provider';

@Component({
  selector: 'app-notify-operator',
  templateUrl: './notify-operator.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [notifyOperatorFormFactory, UserFullNamePipe, BackLinkService],
})
export class NotifyOperatorComponent implements PendingRequest, OnInit {
  @Input() taskId: number;
  @Input() accountId: number;
  @Input() requestTaskActionType: RequestTaskActionProcessDTO['requestTaskActionType'];
  @Input() pendingRfi: boolean;
  @Input() pendingRde: boolean;
  @Input() confirmationMessage: string;

  isFormSubmitted$ = new BehaviorSubject(false);
  isSummaryDisplayed = new BehaviorSubject<boolean>(false);
  objectKeys = Object.keys;

  accountPrimaryContactUsersInfo$: Observable<NotifyAccountOperatorUsersInfo>;
  otherOperatorUsersInfo$: Observable<NotifyAccountOperatorUsersInfo>;
  contacts$: Observable<Array<CaExternalContactDTO>>;
  assignees$: Observable<GovukSelectOption<string>[]>;

  constructor(
    @Inject(NOTIFY_OPERATOR_FORM) readonly form: FormGroup,
    private readonly operatorAuthoritiesService: OperatorAuthoritiesService,
    private readonly externalContactsService: CaExternalContactsService,
    private readonly tasksAssignmentService: TasksAssignmentService,
    private readonly tasksService: TasksService,
    private readonly fullNamePipe: UserFullNamePipe,
    private readonly concurrencyErrorService: ConcurrencyErrorService,
    readonly pendingRequest: PendingRequestService,
    private readonly backLinkService: BackLinkService,
  ) {}

  ngOnInit(): void {
    this.backLinkService.show();

    const accountOperatorAuthorities$ = this.operatorAuthoritiesService
      .getAccountOperatorAuthoritiesUsingGET(this.accountId)
      .pipe(shareReplay({ bufferSize: 1, refCount: true }));

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

    this.contacts$ = this.externalContactsService
      .getCaExternalContactsUsingGET()
      .pipe(pluck('caExternalContacts'), shareReplay({ bufferSize: 1, refCount: true }));

    this.assignees$ = of(this.taskId).pipe(
      switchMap((id: number) => this.tasksAssignmentService.getCandidateAssigneesByTaskIdUsingGET(id)),
      map((assignees) =>
        assignees.map((assignee) => ({ text: this.fullNamePipe.transform(assignee), value: assignee.id })),
      ),
      shareReplay({ bufferSize: 1, refCount: true }),
    );
  }

  returnToUrl(requestTaskActionType: string): string {
    switch (requestTaskActionType) {
      case 'PERMIT_REVOCATION_NOTIFY_OPERATOR_FOR_WITHDRAWAL':
        return '/dashboard';
      default:
        return '..';
    }
  }

  onSubmit(): void {
    if (this.form.valid) {
      let payloadType: RequestTaskActionPayload['payloadType'];
      let payloadKey: string;

      switch (this.requestTaskActionType) {
        case 'PERMIT_ISSUANCE_NOTIFY_OPERATOR_FOR_DECISION':
          payloadType = 'PERMIT_ISSUANCE_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD';
          payloadKey = 'permitDecisionNotification';
          break;
        case 'PERMIT_SURRENDER_NOTIFY_OPERATOR_FOR_DECISION':
          payloadType = 'PERMIT_SURRENDER_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD';
          payloadKey = 'decisionNotification';
          break;
        case 'PERMIT_SURRENDER_CESSATION_NOTIFY_OPERATOR_FOR_DECISION':
          payloadType = 'PERMIT_SURRENDER_CESSATION_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD';
          payloadKey = 'decisionNotification';
          break;
        case 'PERMIT_REVOCATION_NOTIFY_OPERATOR_FOR_SUBMISSION':
          payloadType = 'PERMIT_REVOCATION_NOTIFY_OPERATOR_FOR_SUBMISSION_PAYLOAD';
          payloadKey = 'decisionNotification';
          break;
        case 'PERMIT_REVOCATION_NOTIFY_OPERATOR_FOR_WITHDRAWAL':
          payloadType = 'PERMIT_REVOCATION_NOTIFY_OPERATOR_FOR_WITHDRAWAL_PAYLOAD';
          payloadKey = 'decisionNotification';
          break;
        case 'PERMIT_REVOCATION_NOTIFY_OPERATOR_FOR_CESSATION':
          payloadType = 'PERMIT_REVOCATION_NOTIFY_OPERATOR_FOR_CESSATION_PAYLOAD';
          payloadKey = 'decisionNotification';
          break;
        case 'PERMIT_NOTIFICATION_NOTIFY_OPERATOR_FOR_DECISION':
          payloadType = 'PERMIT_NOTIFICATION_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD';
          payloadKey = 'decisionNotification';
          break;
        case 'PERMIT_NOTIFICATION_FOLLOW_UP_NOTIFY_OPERATOR_FOR_DECISION':
          payloadType = 'PERMIT_NOTIFICATION_FOLLOW_UP_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD';
          payloadKey = 'decisionNotification';
          break;
      }

      of(this.taskId)
        .pipe(
          switchMap((taskId) =>
            this.tasksService.processRequestTaskActionUsingPOST({
              requestTaskActionType: this.requestTaskActionType,
              requestTaskId: taskId,
              requestTaskActionPayload: {
                payloadType,
                [payloadKey]: {
                  operators: this.form.get('users').value,
                  externalContacts: this.form.get('contacts').value,
                  signatory: this.form.get('assignees').value,
                },
              } as RequestTaskActionPayload,
            }),
          ),
          this.pendingRequest.trackRequest(),
          catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
            this.concurrencyErrorService.showErrorForceNavigation(taskNotFoundError),
          ),
          catchTaskReassignedBadRequest(() =>
            this.concurrencyErrorService.showErrorForceNavigation(requestTaskReassignedError()),
          ),
          tap(() => this.backLinkService.hide()),
        )
        .subscribe(() => {
          this.isFormSubmitted$.next(true);
          this.backLinkService.hide();
        });
    } else {
      this.isSummaryDisplayed.next(true);
    }
  }
}
