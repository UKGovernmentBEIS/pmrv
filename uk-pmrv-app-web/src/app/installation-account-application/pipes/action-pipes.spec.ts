import { catchError, firstValueFrom, of, throwError } from 'rxjs';

import { RequestTaskActionProcessDTO } from 'pmrv-api';

import { mockState } from '../testing/mock-state';
import { mapToDecision, mapToSubmit } from './action-pipes';

describe('ActionPipes', () => {
  it('should map to submit application payload', async () => {
    const payload = await firstValueFrom(of(mockState.tasks).pipe(mapToSubmit()));
    expect(payload.requestCreateActionType).toEqual('INSTALLATION_ACCOUNT_OPENING_SUBMIT_APPLICATION');
    expect(payload.requestCreateActionPayload).not.toBeUndefined();
  });

  it('should map to decision application payload', async () => {
    const payload: RequestTaskActionProcessDTO = await firstValueFrom(
      of(mockState.taskId).pipe(mapToDecision(true, 'because')),
    );

    expect(payload.requestTaskActionType).toEqual('INSTALLATION_ACCOUNT_OPENING_SUBMIT_DECISION');
    expect(payload.requestTaskActionPayload).not.toBeUndefined();
    expect(payload.requestTaskActionPayload.payloadType).toEqual(
      'INSTALLATION_ACCOUNT_OPENING_SUBMIT_DECISION_PAYLOAD',
    );
    expect((payload.requestTaskActionPayload as any).decision).toEqual('ACCEPTED');
    expect((payload.requestTaskActionPayload as any).reason).toEqual('because');
  });

  it('should throw error', async () => {
    await firstValueFrom(
      throwError(() => new Error('Oops')).pipe(
        mapToSubmit(),
        catchError((err) => {
          expect(err.message).toEqual('Oops');
          return of(err);
        }),
      ),
    );
  });
});
