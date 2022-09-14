import { WorkflowTypePipe } from './workflow-type.pipe';

describe('WorkflowTypePipe', () => {
  let pipe: WorkflowTypePipe;

  beforeEach(() => (pipe = new WorkflowTypePipe()));

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('hould properly transform workflow types', () => {
    expect(pipe.transform('AER')).toEqual('AER');
    expect(pipe.transform('INSTALLATION_ACCOUNT_OPENING')).toEqual('Account creation');
    expect(pipe.transform('PERMIT_ISSUANCE')).toEqual('Permit Application');
    expect(pipe.transform('PERMIT_NOTIFICATION')).toEqual('Permit Notification');
    expect(pipe.transform('PERMIT_NOTIFICATION_FOLLOW_UP')).toEqual('Notification follow-up');
    expect(pipe.transform('PERMIT_REVOCATION')).toEqual('Permit Revocation');
    expect(pipe.transform('PERMIT_SURRENDER')).toEqual('Permit Surrender');
    expect(pipe.transform('PERMIT_TRANSFER')).toEqual('Permit Transfer');
    expect(pipe.transform('PERMIT_VARIATION')).toEqual('Permit Variation');
    expect(pipe.transform('SYSTEM_MESSAGE_NOTIFICATION')).toEqual('System Message Notification');
    expect(pipe.transform(undefined)).toEqual(null);
  });
});
