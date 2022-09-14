import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { PermitApplicationModule } from '../../../../permit-application.module';
import { SharedPermitModule } from '../../../../shared/shared-permit.module';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { mockState } from '../../../../testing/mock-state';
import { InherentCO2DescriptionSummaryTemplateComponent } from './inherent-co2-description-summary-template.component';

describe('InherentCO2DescriptionSummaryTemplateComponent', () => {
  let component: InherentCO2DescriptionSummaryTemplateComponent;
  let fixture: ComponentFixture<InherentCO2DescriptionSummaryTemplateComponent>;
  let store: PermitApplicationStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PermitApplicationModule, RouterTestingModule, SharedPermitModule],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(PermitApplicationStore);
    store.setState(mockState);
    fixture = TestBed.createComponent(InherentCO2DescriptionSummaryTemplateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
