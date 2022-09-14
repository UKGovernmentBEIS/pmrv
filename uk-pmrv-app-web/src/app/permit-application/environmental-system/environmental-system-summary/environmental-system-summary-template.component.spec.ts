import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '../../../shared/shared.module';
import { SharedPermitModule } from '../../shared/shared-permit.module';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockState } from '../../testing/mock-state';
import { EnvironmentalSystemSummaryTemplateComponent } from './environmental-system-summary-template.component';

describe('EnvironmentalSystemSummaryTemplateComponent', () => {
  let component: EnvironmentalSystemSummaryTemplateComponent;
  let fixture: ComponentFixture<EnvironmentalSystemSummaryTemplateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, SharedPermitModule, RouterTestingModule],
    }).compileComponents();
  });

  beforeEach(() => {
    const store = TestBed.inject(PermitApplicationStore);
    store.setState(mockState);
    fixture = TestBed.createComponent(EnvironmentalSystemSummaryTemplateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
