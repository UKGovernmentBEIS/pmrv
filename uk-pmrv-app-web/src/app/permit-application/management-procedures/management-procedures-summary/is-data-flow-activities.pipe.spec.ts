import { TestBed } from '@angular/core/testing';

import { DataFlowActivities, ManagementProceduresDefinition, TasksService } from 'pmrv-api';

import { mockClass } from '../../../../testing';
import { IsDataFlowActivitiesPipe } from './is-data-flow-activities.pipe';

describe('DataFlowActivitiesPipe', () => {
  let pipe: IsDataFlowActivitiesPipe;

  beforeEach(async () => {
    TestBed.configureTestingModule({
      declarations: [IsDataFlowActivitiesPipe],
      providers: [{ provide: TasksService, useValue: mockClass(TasksService) }],
    });

    pipe = new IsDataFlowActivitiesPipe();
  });

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should return data flow activities task', () => {
    const task: DataFlowActivities = {
      appliedStandards: 'Standards',
      itSystemUsed: 'asdas',
      locationOfRecords: 'Location',
      procedureDescription: 'sadas',
      procedureDocumentName: 'Amazing',
      procedureReference: 'Ref',
      diagramReference: 'diagram ref',
      responsibleDepartmentOrRole: 'Department',
      primaryDataSources: 'Primary data sources',
      processingSteps: 'Processing steps',
    };

    expect(pipe.transform(task, 'dataFlowActivities')).toBeTruthy();
  });

  it('should return null fot task other than data flow activities', () => {
    const task: ManagementProceduresDefinition = {
      appliedStandards: 'Standards',
      itSystemUsed: 'asdas',
      locationOfRecords: 'Location',
      procedureDescription: 'sadas',
      procedureDocumentName: 'Amazing',
      procedureReference: 'Ref',
      diagramReference: 'diagram ref',
      responsibleDepartmentOrRole: 'Department',
    };
    expect(pipe.transform(task, 'monitoringPlanAppropriateness')).toBeFalsy();
  });
});
