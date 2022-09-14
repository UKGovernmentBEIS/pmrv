import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';

import { AppliedStandard } from 'pmrv-api';

import { BasePage } from '../../../../testing';
import { SharedModule } from '../../../shared/shared.module';
import { AppliedStandardFormSummaryComponent } from './applied-standard-form-summary.component';

describe('AppliedStandardFormSummaryComponent', () => {
  let component: AppliedStandardFormSummaryComponent;
  let fixture: ComponentFixture<TestComponent>;
  let page: Page;

  @Component({
    template: `
      <app-applied-standard-form-summary [appliedStandard]="appliedStandard"></app-applied-standard-form-summary>
    `,
  })
  class TestComponent {
    appliedStandard = appliedStandard;
  }

  const appliedStandard: AppliedStandard = {
    parameter: 'param1',
    appliedStandard: 'standard1',
    deviationFromAppliedStandardExist: true,
    deviationFromAppliedStandardDetails: 'deviation1',
    laboratoryName: 'lab1',
    laboratoryAccredited: false,
    laboratoryAccreditationEvidence: 'labEvidence',
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
      declarations: [TestComponent, AppliedStandardFormSummaryComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    component = fixture.debugElement.query(By.directive(AppliedStandardFormSummaryComponent)).componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the details that have value', () => {
    expect(page.summaryListValues).toEqual([
      [
        appliedStandard.parameter,
        appliedStandard.appliedStandard,
        `Yes  ${appliedStandard.deviationFromAppliedStandardDetails}`,
        appliedStandard.laboratoryName,
        `No  ${appliedStandard.laboratoryAccreditationEvidence}`,
      ],
    ]);
  });
});
