import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';

import { BasePage } from '../../../../testing';
import { SharedModule } from '../../../shared/shared.module';
import { ProcedureFormSummaryComponent } from './procedure-form-summary.component';

describe('ProcedureFormSummaryComponent', () => {
  let component: ProcedureFormSummaryComponent;
  let fixture: ComponentFixture<TestComponent>;
  let page: Page;

  @Component({
    template: ` <app-procedure-form-summary [details]="details"></app-procedure-form-summary> `,
  })
  class TestComponent {
    details = procedureForm;
  }

  const procedureForm = {
    appliedStandards: 'appliedStandards2',
    locationOfRecords: 'locationOfRecords2',
    procedureDescription: 'procedureDescription2',
    procedureDocumentName: 'procedureDocumentName2',
    procedureReference: 'procedureReference2',
    responsibleDepartmentOrRole: 'responsibleDepartmentOrRole2',
  };

  class Page extends BasePage<TestComponent> {
    get summaryListValues() {
      return this.queryAll<HTMLDListElement>('dl').map((installation) =>
        Array.from(installation.querySelectorAll('dd')).map((dd) => dd.textContent.trim()),
      );
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule],
      declarations: [TestComponent, ProcedureFormSummaryComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    component = fixture.debugElement.query(By.directive(ProcedureFormSummaryComponent)).componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the details that have value', () => {
    expect(page.summaryListValues).toEqual([
      [
        procedureForm.procedureDescription,
        procedureForm.procedureDocumentName,
        procedureForm.procedureReference,
        procedureForm.responsibleDepartmentOrRole,
        procedureForm.locationOfRecords,
        procedureForm.appliedStandards,
      ],
    ]);
  });
});
