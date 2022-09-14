import { TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, of } from 'rxjs';

import { AccountsService, LegalEntitiesService, RequestActionsService, TasksService } from 'pmrv-api';

import { ActivatedRouteSnapshotStub, mockClass, MockType } from '../../../testing';
import { AccountsServiceStub } from '../../../testing/accounts.service.stub';
import { LegalEntitiesServiceStub } from '../../../testing/legal-entities.service.stub';
import { installationFormFactory } from '../factories/installation-form.factory';
import { legalEntityFormFactory } from '../factories/legal-entity-form.factory';
import {
  ApplicationSectionType,
  InstallationAccountApplicationState,
} from '../store/installation-account-application.state';
import { InstallationAccountApplicationStore } from '../store/installation-account-application.store';
import { mockPayload, mockState } from '../testing/mock-state';
import { ApplicationGuard } from './application.guard';

describe('ApplicationGuard', () => {
  let guard: ApplicationGuard;
  let store: InstallationAccountApplicationStore;
  const taskService = mockClass(TasksService);

  const payload = {
    ...mockPayload,
    legalEntity: { ...mockPayload.legalEntity, id: 1 },
  };

  taskService.getTaskItemInfoByIdUsingGET.mockReturnValue(of({ requestTask: { payload } } as any));

  const response = {
    id: 1,
    type: 'INSTALLATION_ACCOUNT_OPENING_SUBMIT_APPLICATION',
    creationDate: new Date().toISOString(),
    submitter: 'John Doe',
    payload,
  };

  const requestActionsService: MockType<RequestActionsService> = {
    getRequestActionByIdUsingGET: jest.fn().mockReturnValue(of(response)),
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, ReactiveFormsModule],
      providers: [
        ApplicationGuard,
        installationFormFactory,
        legalEntityFormFactory,
        InstallationAccountApplicationStore,
        { provide: RequestActionsService, useValue: requestActionsService },
        { provide: AccountsService, useClass: AccountsServiceStub },
        { provide: LegalEntitiesService, useClass: LegalEntitiesServiceStub },
        { provide: TasksService, useValue: taskService },
      ],
    });
    guard = TestBed.inject(ApplicationGuard);
    store = TestBed.inject(InstallationAccountApplicationStore);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should fetch and cache the request actions', async () => {
    await expect(
      firstValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: '1', actionId: '1' }))),
    ).resolves.toBeTruthy();

    const expectedState: InstallationAccountApplicationState = {
      tasks: [
        ...mockState.tasks.filter((task) => task.type !== ApplicationSectionType.legalEntity),
        {
          ...mockState.tasks.find((task) => task.type === ApplicationSectionType.legalEntity),
          type: ApplicationSectionType.legalEntity,
          value: {
            selectGroup: {
              isNew: false,
              id: payload.legalEntity.id,
            },
            detailsGroup: {
              name: payload.legalEntity.name,
              address: payload.legalEntity.address,
              type: payload.legalEntity.type,
              referenceNumber: payload.legalEntity.referenceNumber,
              noReferenceNumberReason: null,
            },
          },
        },
      ],
    };

    async function expectStoreToHaveTask(type: ApplicationSectionType) {
      await expect(firstValueFrom(store.getTask(type))).resolves.toEqual(
        expectedState.tasks.find((task) => task.type === type),
      );
    }

    await Promise.all(
      [
        ApplicationSectionType.installation,
        ApplicationSectionType.legalEntity,
        ApplicationSectionType.commencement,
      ].map(expectStoreToHaveTask),
    );

    await expect(firstValueFrom(store.select('isReviewed'))).resolves.toBeTruthy();
  });
});
