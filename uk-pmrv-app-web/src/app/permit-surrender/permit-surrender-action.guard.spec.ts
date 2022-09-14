import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { lastValueFrom, of } from 'rxjs';

import { RequestActionPayload, RequestActionsService } from 'pmrv-api';

import { ActivatedRouteSnapshotStub, MockType } from '../../testing';
import { SharedModule } from '../shared/shared.module';
import { PermitSurrenderActionGuard } from './permit-surrender-action.guard';
import { PermitSurrenderStore } from './store/permit-surrender.store';
import { mockActionState, mockActionSubmittedPayload } from './testing/mock-state';

describe('PermitSurrenderActionGuard', () => {
  let guard: PermitSurrenderActionGuard;
  let store: PermitSurrenderStore;

  const requestActionsService: MockType<RequestActionsService> = {
    getRequestActionByIdUsingGET: jest.fn().mockReturnValue(of(null)),
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule],
      providers: [{ provide: RequestActionsService, useValue: requestActionsService }],
    });
    guard = TestBed.inject(PermitSurrenderActionGuard);
    store = TestBed.inject(PermitSurrenderStore);
  });

  afterEach(() => jest.clearAllMocks());

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should allow permit surrender payloads', async () => {
    requestActionsService.getRequestActionByIdUsingGET.mockReturnValueOnce(
      of({
        id: mockActionState.requestActionId,
        type: mockActionState.requestActionType,
        payload: {
          payloadType: mockActionSubmittedPayload.payloadType,
          permitSurrender: mockActionSubmittedPayload.permitSurrender,
          permitSurrenderAttachments: mockActionSubmittedPayload.permitSurrenderAttachments,
        } as RequestActionPayload,
        submitter: 'submitter',
        creationDate: mockActionState.creationDate,
      }),
    );

    await expect(
      lastValueFrom(
        guard.canActivate(new ActivatedRouteSnapshotStub({ actionId: `${mockActionState.requestActionId}` })),
      ),
    ).resolves.toBeTruthy();

    expect(requestActionsService.getRequestActionByIdUsingGET).toHaveBeenCalledTimes(1);
    expect(requestActionsService.getRequestActionByIdUsingGET).toHaveBeenLastCalledWith(
      mockActionState.requestActionId,
    );
  });

  it('should update store', async () => {
    requestActionsService.getRequestActionByIdUsingGET.mockReturnValueOnce(
      of({
        id: mockActionState.requestActionId,
        type: mockActionState.requestActionType,
        payload: {
          payloadType: mockActionSubmittedPayload.payloadType,
          permitSurrender: mockActionSubmittedPayload.permitSurrender,
          permitSurrenderAttachments: mockActionSubmittedPayload.permitSurrenderAttachments,
        } as RequestActionPayload,
        submitter: 'submitter',
        creationDate: mockActionState.creationDate,
      }),
    );

    const resetSpy = jest.spyOn(store, 'reset');

    await expect(
      lastValueFrom(
        guard.canActivate(new ActivatedRouteSnapshotStub({ actionId: `${mockActionState.requestActionId}` })),
      ),
    ).resolves.toBeTruthy();

    expect(requestActionsService.getRequestActionByIdUsingGET).toHaveBeenCalledTimes(1);
    expect(requestActionsService.getRequestActionByIdUsingGET).toHaveBeenLastCalledWith(
      mockActionState.requestActionId,
    );

    expect(resetSpy).toHaveBeenCalledTimes(1);
    expect(store.getState()).toEqual(mockActionState);
  });
});
