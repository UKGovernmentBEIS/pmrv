import { ManagementProceduresBodyPipe } from './management-procedures-body.pipe';

describe('ManagementProceduresBodyPipe', () => {
  let pipe: ManagementProceduresBodyPipe;

  beforeEach(() => (pipe = new ManagementProceduresBodyPipe()));

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should map task types to subtitle content', () => {
    expect(pipe.transform('assignmentOfResponsibilities')).toEqual(
      'Provide details of how the organisation assigns monitoring and reporting responsibilities. Include review processes and any training provided.',
    );
    expect(pipe.transform('monitoringPlanAppropriateness')).toEqual(
      'Provide details of how the organisation evaluates the appropriateness of the monitoring plan. Include any potential measures to improve the monitoring plan.',
    );
    expect(pipe.transform('reviewAndValidationOfData')).toEqual(
      'Provide details about the procedures for ensuring the data is regularly reviewed and validated internally.',
    );
    expect(pipe.transform('assessAndControlRisk')).toEqual(
      'Provide details of how the organisation assesses and controls inherent risks when establishing an effective control system.',
    );
    expect(pipe.transform('controlOfOutsourcedActivities')).toEqual(
      'Provide details about the procedures used to control the outsourced data flow and control activities. Include how the organization reviews the quality of the resulting data.',
    );
    expect(pipe.transform('qaMeteringAndMeasuringEquipment')).toEqual(
      "Provide details about how the metering and measurement equipment is calibrated and regularly checked. Include details about what the organisation does if the equipment doesn't meet the required performance levels.",
    );
    expect(pipe.transform('correctionsAndCorrectiveActions')).toEqual(
      'Provide details about what actions are undertaken if data flow activities and control activities are found to not function effectively.',
    );
    expect(pipe.transform('recordKeepingAndDocumentation')).toEqual(
      "Provide details about the organisation's document retention process, especially for data and information. Include how the data is stored so that the information can be made available when requested by the regulator or verifier.",
    );
    expect(pipe.transform('qaDataFlowActivities')).toEqual(
      'Provide details about how the information technology is tested and controlled, including access control, back-up, recovery and security.',
    );
    expect(pipe.transform('dataFlowActivities')).toEqual(
      'Provide details about the procedures used to manage data flow activities.',
    );
    expect(pipe.transform(null)).toEqual('');
  });
});
