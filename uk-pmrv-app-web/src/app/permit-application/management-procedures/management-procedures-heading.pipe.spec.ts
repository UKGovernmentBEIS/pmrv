import { ManagementProceduresHeadingPipe } from './management-procedures-heading.pipe';

describe('ManagementProceduresHeading', () => {
  let pipe: ManagementProceduresHeadingPipe;

  beforeEach(() => (pipe = new ManagementProceduresHeadingPipe()));

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should map task types to title content', () => {
    expect(pipe.transform('assignmentOfResponsibilities')).toEqual('Assignment of responsibilities');
    expect(pipe.transform('monitoringPlanAppropriateness')).toEqual('Monitoring plan appropriateness');
    expect(pipe.transform('reviewAndValidationOfData')).toEqual('Review and validation of data');
    expect(pipe.transform('assessAndControlRisk')).toEqual('Assessing and controlling risks');
    expect(pipe.transform('controlOfOutsourcedActivities')).toEqual('Control of outsourced activities');
    expect(pipe.transform('qaMeteringAndMeasuringEquipment')).toEqual(
      'Quality assurance of metering and measuring equipment',
    );
    expect(pipe.transform('correctionsAndCorrectiveActions')).toEqual('Corrections and corrective actions');
    expect(pipe.transform('recordKeepingAndDocumentation')).toEqual('Record keeping and documentation');
    expect(pipe.transform('qaDataFlowActivities')).toEqual('Quality assurance of IT used for data flow activities');
    expect(pipe.transform('dataFlowActivities')).toEqual('Data flow activities');
    expect(pipe.transform(null)).toEqual('');
  });
});
