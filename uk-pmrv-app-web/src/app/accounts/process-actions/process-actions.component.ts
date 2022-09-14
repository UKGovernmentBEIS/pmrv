import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, Observable, pluck, switchMap, withLatestFrom } from 'rxjs';

import { AuthService } from '@core/services/auth.service';
import { BackLinkService } from '@shared/back-link/back-link.service';
import { ItemLinkPipe } from '@shared/pipes/item-link.pipe';

import {
  RequestCreateActionProcessDTO,
  RequestCreateValidationResult,
  RequestDetailsDTO,
  RequestItemsService,
  RequestsService,
  UserStatusDTO,
} from 'pmrv-api';

import { workflowDetailsTypesMap } from '../workflows/workflowMap';
import { WorkflowLabel, WorkflowMap } from './process-actions-map';

@Component({
  selector: 'app-process-actions',
  templateUrl: './process-actions.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [BackLinkService],
})
export class ProcessActionsComponent implements OnInit {
  accountId$: Observable<number>;
  availableTasks$: Observable<WorkflowLabel[]>;

  private readonly variationWorkflow: Partial<WorkflowLabel> = {
    title: 'Make a permanent change to your permit plan related to emissions, emission equipment or legal changes',
    button: 'Start a variation',
  };

  private operatorsWorkflowMessagesMap: Partial<WorkflowMap> = {
    PERMIT_SURRENDER: {
      title: 'Surrender your permit and close this installation',
      button: 'Start a permit surrender',
    },
    PERMIT_VARIATION: this.variationWorkflow,
    PERMIT_TRANSFER: undefined,
    PERMIT_ISSUANCE: undefined,
    PERMIT_NOTIFICATION: {
      title: 'Notify the regulator of a temporary or minor change to your permit plan',
      button: 'Start a notification',
    },
    AER: undefined,
  };

  private regulatorsWorkflowMessagesMap: Partial<WorkflowMap> = {
    PERMIT_VARIATION: this.variationWorkflow,
    PERMIT_REVOCATION: {
      title: 'Revoke your permit',
      button: 'Start a permit revocation',
    },
  };

  private userRoleWorkflowsMap: Record<UserStatusDTO['roleType'], Partial<WorkflowMap>> = {
    OPERATOR: this.operatorsWorkflowMessagesMap,
    REGULATOR: this.regulatorsWorkflowMessagesMap,
    VERIFIER: undefined,
  };

  constructor(
    private readonly activatedRoute: ActivatedRoute,
    private readonly requestsService: RequestsService,
    private readonly requestItemsService: RequestItemsService,
    private readonly authService: AuthService,
    private readonly router: Router,
    private readonly itemLinkPipe: ItemLinkPipe,
    private readonly backLinkService: BackLinkService,
  ) {}

  ngOnInit(): void {
    this.backLinkService.show();

    this.accountId$ = this.activatedRoute.paramMap.pipe(map((parameters) => +parameters.get('accountId')));

    // request for available tasks
    this.availableTasks$ = this.accountId$.pipe(
      switchMap((accountId) => this.requestsService.getAvailableWorkflowsUsingGET(accountId, 'PERMIT')),
      withLatestFrom(
        this.authService.userStatus.pipe(
          pluck('roleType'),
          map((roleType) => this.userRoleWorkflowsMap[roleType]),
        ),
      ),
      map(([validationResults, userRoleWorkflowMessagesMap]) =>
        Object.entries(validationResults)
          .filter(([type]) => userRoleWorkflowMessagesMap[type])
          .map(([type, result]) => ({
            ...userRoleWorkflowMessagesMap[type],
            type: type,
            errors: result.valid ? undefined : this.createErrorMessages(type, result),
          })),
      ),
    );
  }

  onRequestButtonClick(requestType: RequestCreateActionProcessDTO['requestCreateActionType']) {
    this.accountId$
      .pipe(
        switchMap((accountId) =>
          this.requestsService.processRequestCreateActionUsingPOST(accountId, {
            requestCreateActionType: requestType,
            requestCreateActionPayload: {
              payloadType: 'EMPTY_PAYLOAD',
            },
          }),
        ),
        switchMap(({ requestId }) => this.requestItemsService.getItemsByRequestUsingGET(requestId)),
        first(),
      )
      .subscribe(({ items }) => {
        const link = items?.length == 1 ? this.itemLinkPipe.transform(items[0]) : ['/dashboard'];
        this.router.navigate(link);
      });
  }

  private createErrorMessages(requestedTaskType: string, result: RequestCreateValidationResult): string[] {
    if (result?.accountStatus === 'LIVE') {
      const typeString = workflowDetailsTypesMap[requestedTaskType].toLowerCase();
      return [`You cannot start a ${typeString} while the account status in not Live.`];
    } else {
      return result?.requests?.length > 0
        ? [...result.requests.map((r) => this.createErrorMessage(requestedTaskType, r))]
        : ['Action currently unavailable'];
    }
  }

  private createErrorMessage(type: string, result: RequestDetailsDTO['requestType']): string {
    const workflowTypeString = workflowDetailsTypesMap[type].toLowerCase();
    switch (result) {
      case 'PERMIT_SURRENDER':
        return `You cannot start a ${workflowTypeString} as there is already one in progress.`;
      case 'PERMIT_VARIATION':
        return `You cannot start a ${workflowTypeString} while a permit variation is in progress.`;
      case 'PERMIT_TRANSFER':
        return `You cannot start a ${workflowTypeString} while a permit transfer is in progress.`;
      case 'PERMIT_NOTIFICATION':
        return `You cannot start a ${workflowTypeString} while the account status is not Live`;
      case 'PERMIT_REVOCATION':
        return `You cannot start a ${workflowTypeString} as there is already one in progress.`;
      case 'PERMIT_ISSUANCE':
      default:
        //TODO need implementation for Issuance
        throw new Error(`No implementation message for: ${result}`);
    }
  }
}
