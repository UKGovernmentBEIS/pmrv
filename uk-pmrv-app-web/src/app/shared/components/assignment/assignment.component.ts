import { ChangeDetectionStrategy, Component, EventEmitter, Input, OnChanges, OnInit, Output } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';

import { BehaviorSubject, combineLatest, iif, map, mergeMap, Observable, of, switchMapTo, take, tap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { AuthService } from '@core/services/auth.service';
import { taskNotFoundError } from '@shared/errors/concurrency-error';

import { GovukSelectOption } from 'govuk-components';

import { RequestTaskItemDTO, TasksAssignmentService, TasksReleaseService } from 'pmrv-api';

import { ConcurrencyErrorService } from '../../../error/concurrency-error/concurrency-error.service';
import { catchNotFoundRequest, ErrorCode } from '../../../error/not-found-error';
import { UserFullNamePipe } from '../../pipes/user-full-name.pipe';

@Component({
  selector: 'app-assignment',
  templateUrl: './assignment.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [UserFullNamePipe],
})
export class AssignmentComponent implements OnInit, OnChanges {
  @Input() info: RequestTaskItemDTO;
  @Input() noBorder: boolean;

  @Output() readonly submitted = new EventEmitter<string>();

  info$ = new BehaviorSubject<RequestTaskItemDTO>(null);
  options$: Observable<GovukSelectOption<string>[]>;
  assignForm: FormGroup = this.fb.group({
    assignee: [],
  });

  constructor(
    private readonly fb: FormBuilder,
    private readonly authService: AuthService,
    private readonly userFullNamePipe: UserFullNamePipe,
    private readonly tasksAssignmentService: TasksAssignmentService,
    private readonly tasksReleaseService: TasksReleaseService,
    private readonly pendingRequestService: PendingRequestService,
    private readonly concurrencyErrorService: ConcurrencyErrorService,
  ) {}

  ngOnChanges(changes): void {
    if (changes.info) {
      this.info$.next(changes.info.currentValue);
    }
  }

  ngOnInit(): void {
    this.options$ = this.info$.pipe(
      mergeMap((info) => {
        return combineLatest([
          this.authService.userStatus,
          this.tasksAssignmentService.getCandidateAssigneesByTaskIdUsingGET(info.requestTask.id),
          of(info),
        ]);
      }),
      tap(([, , info]) => {
        this.assignForm.get('assignee').setValue(info.requestTask.assigneeUserId || null);
      }),
      map(([userStatus, candidates, info]) => [
        ...(userStatus.roleType !== 'OPERATOR' ? [{ text: 'Unassigned', value: null }] : []),
        ...candidates.map((candidate) => ({
          text: this.userFullNamePipe.transform(candidate),
          value: candidate.id,
        })),
        ...(!this.info.requestTask.assigneeUserId ||
        candidates.some((candidate) => candidate.id === info.requestTask.assigneeUserId)
          ? []
          : [
              {
                text: this.info.requestTask.assigneeFullName,
                value: this.info.requestTask.assigneeUserId,
                disabled: true,
              },
            ]),
      ]),
    );
  }

  submit(taskId: number, userId: string): void {
    iif(
      () => !!userId,
      this.tasksAssignmentService.assignTaskUsingPOST({ taskId, userId }),
      this.tasksReleaseService.releaseTaskUsingDELETE(taskId),
    )
      .pipe(
        catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
          this.concurrencyErrorService.showErrorForceNavigation(taskNotFoundError),
        ),
        this.pendingRequestService.trackRequest(),
        switchMapTo(this.options$),
        take(1),
      )
      .subscribe((users) => this.submitted.emit(userId ? users.find((user) => user.value === userId).text : null));
  }
}
