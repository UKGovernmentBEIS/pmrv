import { HttpClientModule } from '@angular/common/http';
import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { SourceStreamDescriptionPipe } from '@shared/pipes/source-streams-description.pipe';

import { PFCMonitoringApproach } from 'pmrv-api';

import { BasePage } from '../../../../../../../testing';
import { SharedModule } from '../../../../../../shared/shared.module';
import { CategoryTypeNamePipe } from '../../../../../shared/pipes/category-type-name.pipe';
import { EmissionPointPipe } from '../../../../../shared/pipes/emission-point.pipe';
import { EmissionSourcePipe } from '../../../../../shared/pipes/emission-source.pipe';
import { FindSourceStreamPipe } from '../../../../../shared/pipes/find-source-stream.pipe';
import { TaskPipe } from '../../../../../shared/pipes/task.pipe';
import { TierSourceStreamNamePipe } from '../../../../../shared/pipes/tier-source-stream-name.pipe';
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
    sourceStream = (mockPermitApplyPayload.permit.monitoringApproaches.PFC as PFCMonitoringApproach)
      .sourceStreamCategoryAppliedTiers[0].sourceStreamCategory;
  }

  class Page extends BasePage<TestComponent> {
    get summaryDefinitions() {
      return this.queryAll('dd').map((row) => row.textContent.trim());
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientModule, SharedModule],
      declarations: [
        TestComponent,
        FindSourceStreamPipe,
        CategoryTypeNamePipe,
        CategorySummaryOverviewComponent,
        EmissionSourcePipe,
        EmissionPointPipe,
        TaskPipe,
        TierSourceStreamNamePipe,
      ],
      providers: [SourceStreamDescriptionPipe, CategoryTypeNamePipe],
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
      'The big Ref Emission point 1',
      '23.5 tonnes',
      'Overvoltage method',
    ]);
  });
});
