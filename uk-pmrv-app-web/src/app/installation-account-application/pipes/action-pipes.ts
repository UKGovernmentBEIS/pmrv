import { map, OperatorFunction, pipe } from 'rxjs';

import cleanDeep from 'clean-deep';

import { RequestCreateActionProcessDTO, RequestTaskActionProcessDTO } from 'pmrv-api';

import { Section } from '../store/installation-account-application.state';
import { mapApplication, mapApplicationDecision } from './submit-application';

export type DismissActionType = Extract<
  RequestTaskActionProcessDTO['requestTaskActionType'],
  'INSTALLATION_ACCOUNT_OPENING_ARCHIVE' | 'SYSTEM_MESSAGE_DISMISS'
>;

export function mapToSubmit(): OperatorFunction<Section[], RequestCreateActionProcessDTO> {
  return pipe(
    map((sections) => ({
      requestCreateActionPayload: cleanDeep(
        mapApplication(sections, 'INSTALLATION_ACCOUNT_OPENING_SUBMIT_APPLICATION_PAYLOAD'),
      ),
      requestCreateActionType: 'INSTALLATION_ACCOUNT_OPENING_SUBMIT_APPLICATION',
    })),
  );
}

export function mapToAmend(): OperatorFunction<[Section[], number], RequestTaskActionProcessDTO> {
  return pipe(
    map(([sections, requestTaskId]) => ({
      requestTaskActionPayload: cleanDeep(
        mapApplication(sections, 'INSTALLATION_ACCOUNT_OPENING_AMEND_APPLICATION_PAYLOAD'),
      ),
      requestTaskActionType: 'INSTALLATION_ACCOUNT_OPENING_AMEND_APPLICATION',
      requestTaskId,
    })),
  );
}

export function mapToDecision(
  isAccepted: boolean,
  reason?: string,
): OperatorFunction<number, RequestTaskActionProcessDTO> {
  return pipe(
    map((requestTaskId) => ({
      requestTaskActionPayload: cleanDeep(mapApplicationDecision(isAccepted ? 'ACCEPTED' : 'REJECTED', reason)),
      requestTaskActionType: 'INSTALLATION_ACCOUNT_OPENING_SUBMIT_DECISION',
      requestTaskId,
    })),
  );
}

export function mapToDismiss(type: DismissActionType): OperatorFunction<number, RequestTaskActionProcessDTO> {
  return pipe(
    map((requestTaskId) => ({
      requestTaskActionPayload: { payloadType: 'EMPTY_PAYLOAD' },
      requestTaskActionType: type,
      requestTaskId,
    })),
  );
}
