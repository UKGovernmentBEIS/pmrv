import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { ProcedureOptionalForm } from 'pmrv-api';

import { PermitApplicationModule } from '../../../../permit-application.module';
import { SharedPermitModule } from '../../../../shared/shared-permit.module';
import { TaskKey } from '../../../../shared/types/permit-task.type';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { mockPermitCompletePayload } from '../../../../testing/mock-permit-apply-action';
import { mockStateBuild } from '../../../../testing/mock-state';
import { OptionalSummaryTemplateComponent } from './optional-summary-template.component';

describe('OptionalSummaryTemplateComponent', () => {
  let component: OptionalSummaryTemplateComponent;
  let fixture: ComponentFixture<TestComponent>;
  let store: PermitApplicationStore;

  const procedureOptionalForm: ProcedureOptionalForm = {
    exist: true,
    procedureForm: {
      appliedStandards: 'appliedStandards2',
      locationOfRecords: 'locationOfRecords2',
      procedureDescription: 'procedureDescription2',
      procedureDocumentName: 'procedureDocumentName2',
      procedureReference: 'procedureReference2',
      responsibleDepartmentOrRole: 'responsibleDepartmentOrRole2',
    },
  };

  const taskKey: TaskKey = 'monitoringApproaches.MEASUREMENT.gasFlowCalculation';

  @Component({
    template: `
      <app-measurement-optional-summary-template
        [procedureOptionalForm]="procedureOptionalForm"
        [taskKey]="taskKey"
      ></app-measurement-optional-summary-template>
    `,
  })
  class TestComponent {
    procedureOptionalForm = procedureOptionalForm;
    taskKey = taskKey;
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PermitApplicationModule, RouterTestingModule, SharedPermitModule],
      declarations: [TestComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(PermitApplicationStore);
    store.setState(
      mockStateBuild({ ...mockPermitCompletePayload.permit }, { ...mockPermitCompletePayload.permitSectionsCompleted }),
    );
    fixture = TestBed.createComponent(TestComponent);
    component = fixture.debugElement.query(By.directive(OptionalSummaryTemplateComponent)).componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
