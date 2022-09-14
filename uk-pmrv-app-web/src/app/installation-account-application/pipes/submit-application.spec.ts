import { cloneDeep } from 'lodash-es';

import { LocationOffShoreDTO, LocationOnShoreDTO } from 'pmrv-api';

import { OnshoreInstallation } from '../installation-type/installation';
import { mockOnshoreInstallation, mockState } from '../testing/mock-state';
import { mapApplication, mapApplicationDecision } from './submit-application';

describe('SubmitApplication', () => {
  it('should be an onshore installation', () => {
    const payload = mapApplication(mockState.tasks, 'INSTALLATION_ACCOUNT_OPENING_SUBMIT_DECISION_PAYLOAD');
    expect(payload.competentAuthority).toEqual(
      (mockState.tasks[2].value as OnshoreInstallation).locationGroup.location,
    );
    expect((payload.location as LocationOnShoreDTO).address).toBeDefined();
    expect((payload.location as LocationOnShoreDTO).gridReference).toBeDefined();
    expect((payload.location as LocationOffShoreDTO).latitude).toBeUndefined();
    expect((payload.location as LocationOffShoreDTO).longitude).toBeUndefined();
  });

  it('should be offshore installation', () => {
    const testState = cloneDeep(mockState);
    testState.tasks[2].value = mockOnshoreInstallation;
    const payload = mapApplication(testState.tasks, 'INSTALLATION_ACCOUNT_OPENING_SUBMIT_DECISION_PAYLOAD');

    expect(payload.competentAuthority).toEqual('ENGLAND');
    expect((payload.location as LocationOnShoreDTO).address).toBeDefined();
    expect((payload.location as LocationOnShoreDTO).gridReference).toEqual(
      mockOnshoreInstallation.onshoreGroup.gridReference,
    );
    expect((payload.location as LocationOffShoreDTO).latitude).toBeUndefined();
    expect((payload.location as LocationOffShoreDTO).longitude).toBeUndefined();
  });

  it('should have a decision and reason', () => {
    const payload = mapApplicationDecision('ACCEPTED', 'because');

    expect(payload.decision).toEqual('ACCEPTED');
    expect(payload.reason).toEqual('because');
  });
});
