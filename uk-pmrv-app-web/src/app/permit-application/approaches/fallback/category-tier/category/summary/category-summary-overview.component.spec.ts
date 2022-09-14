import { HttpClientModule } from '@angular/common/http';
import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { FallbackMonitoringApproach } from 'pmrv-api';

import { BasePage } from '../../../../../../../testing';
import { SharedModule } from '../../../../../../shared/shared.module';
import { SharedPermitModule } from '../../../../../shared/shared-permit.module';
import { PermitApplicationStore } from '../../../../../store/permit-application.store';
import { mockPermitApplyPayload } from '../../../../../testing/mock-permit-apply-action';
import { mockState } from '../../../../../testing/mock-state';
import { CategorySummaryOverviewComponent } from './category-summary-overview.component';

describe('CategorySummaryOverviewComponent', () => {
  let component: CategorySummaryOverviewComponent;
  let fixture: ComponentFixture<TestComponent>;
  let page: Page;
  let store: PermitApplicationStore;

  @Component({
    template: ` <app-category-summary-overview [sourceStreamCategory]="sourceStream"></app-category-summary-overview> `,
  })
  class TestComponent {
    sourceStream = (mockPermitApplyPayload.permit.monitoringApproaches.FALLBACK as FallbackMonitoringApproach)
      .sourceStreamCategoryAppliedTiers[0].sourceStreamCategory;
  }

  class Page extends BasePage<TestComponent> {
    get summaryDefinitions() {
      return this.queryAll('dd').map((row) => row.textContent.trim());
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientModule, SharedModule, SharedPermitModule],
      declarations: [TestComponent, CategorySummaryOverviewComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(PermitApplicationStore);
    store.setState(mockState);
    fixture = TestBed.createComponent(TestComponent);
    component = fixture.debugElement.query(By.directive(CategorySummaryOverviewComponent)).componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the source stream category summary', () => {
    expect(page.summaryDefinitions).toEqual([
      '13123124 White Spirit & SBP: Major',
      'S1 Boiler',
      '23.8 tonnes',
      'ref1, Ultrasonic meter, Specified uncertainty ±2%',
    ]);
  });
});
