import { initialState, PermitApplicationState } from '../../store/permit-application.state';
import { mockRequestActionState, mockState } from '../../testing/mock-state';
import { DecisionFilesPipe } from './decision-files.pipe';

describe('DecisionFilesPipe', () => {
  let pipe: DecisionFilesPipe;

  const state: PermitApplicationState = {
    ...initialState,
    reviewGroupDecisions: {
      INSTALLATION_DETAILS: {
        type: 'OPERATOR_AMENDS_NEEDED',
        notes: 'notes',
        files: ['e227ea8a-778b-4208-9545-e108ea66c114'],
      },
    },
    reviewAttachments: {
      'e227ea8a-778b-4208-9545-e108ea66c114': 'amends.pdf',
    },
  };

  beforeEach(() => (pipe = new DecisionFilesPipe()));

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should return file from action', () => {
    expect(
      pipe.transform('INSTALLATION_DETAILS', {
        ...state,
        ...mockRequestActionState,
        requestActionType: 'PERMIT_ISSUANCE_APPLICATION_SUBMITTED',
      }),
    ).toEqual([
      {
        downloadUrl: '/permit-application/action/1/file-download/e227ea8a-778b-4208-9545-e108ea66c114',
        fileName: 'amends.pdf',
      },
    ]);
  });

  it('should return file for task', () => {
    expect(pipe.transform('INSTALLATION_DETAILS', { ...state, ...mockState })).toEqual([
      {
        downloadUrl: '/permit-application/237/file-download/e227ea8a-778b-4208-9545-e108ea66c114',
        fileName: 'amends.pdf',
      },
    ]);
  });
});
