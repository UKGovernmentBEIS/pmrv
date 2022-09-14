import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { GovukComponentsModule } from 'govuk-components';

import { MeasMeasuredEmissions, N2OMeasuredEmissions } from 'pmrv-api';

import { ActivatedRouteStub, BasePage } from '../../../../../testing';
import { SharedModule } from '../../../../shared/shared.module';
import { AppliedTierPipe } from '../../pipes/applied-tier.pipe';
import { MeasurementDevicesLabelPipe } from '../../pipes/measurement-devices-label.pipe';
import { SamplingFrequencyPipe } from '../../pipes/sampling-frequency.pipe';
import { MeasuredEmissionsOverviewComponent } from './measured-emissions-overview.component';

describe('MeasuredEmissionsOverviewComponent', () => {
  let component: MeasuredEmissionsOverviewComponent;
  let fixture: ComponentFixture<TestComponent>;
  let page: Page;

  @Component({
    template: `
      <app-measured-emissions-overview [measuredEmissions]="measuredEmissions"></app-measured-emissions-overview>
    `,
  })
  class TestComponent {
    measuredEmissions = measuredEmissions;
  }

  const measuredEmissions: N2OMeasuredEmissions & MeasMeasuredEmissions = {
    measurementDevicesOrMethods: ['16236817394240.1574963093314700'],
    samplingFrequency: 'MONTHLY',
    tier: 'TIER_1',
    isHighestRequiredTier: false,
    noHighestRequiredTierJustification: {
      isCostUnreasonable: true,
      isTechnicallyInfeasible: true,
      technicalInfeasibilityExplanation: 'This is an explanation',
    },
  };

  class Page extends BasePage<TestComponent> {
    get summaryListValues() {
      return this.queryAll<HTMLDListElement>('dl').map((installation) =>
        Array.from(installation.querySelectorAll('dd')).map((dd) => dd.textContent.trim()),
      );
    }
  }

  const taskKeys = [
    'monitoringApproaches.N2O.sourceStreamCategoryAppliedTiers',
    'monitoringApproaches.MEASUREMENT.sourceStreamCategoryAppliedTiers',
  ];

  for (const taskKey of taskKeys) {
    describe(taskKey, () => {
      beforeEach(async () => {
        const route = new ActivatedRouteStub({ index: 0 }, null, {
          taskKey: taskKey,
        });
        await TestBed.configureTestingModule({
          imports: [GovukComponentsModule, SharedModule, RouterTestingModule],
          declarations: [
            TestComponent,
            MeasuredEmissionsOverviewComponent,
            MeasurementDevicesLabelPipe,
            SamplingFrequencyPipe,
            AppliedTierPipe,
          ],
          providers: [{ provide: ActivatedRoute, useValue: route }],
        }).compileComponents();
      });

      beforeEach(() => {
        fixture = TestBed.createComponent(TestComponent);
        component = fixture.debugElement.query(By.directive(MeasuredEmissionsOverviewComponent)).componentInstance;
        page = new Page(fixture);
        fixture.detectChanges();
      });

      it('should create', () => {
        expect(component).toBeTruthy();
      });

      it('should display the details that have value', () => {
        expect(page.summaryListValues).toEqual([
          ['Monthly', 'Tier 1', 'No'],
          ['Unreasonable cost  Technical infeasibility', 'This is an explanation'],
        ]);
      });
    });
  }
});
