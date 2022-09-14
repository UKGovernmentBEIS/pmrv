import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { PermitApplicationModule } from '../../permit-application.module';
import { ConfidentialityStatementSummaryTemplateComponent } from './confidentiality-statement-summary-template.component';

describe('ConfidentialityStatementSummaryTemplateComponent', () => {
  let component: ConfidentialityStatementSummaryTemplateComponent;
  let fixture: ComponentFixture<ConfidentialityStatementSummaryTemplateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PermitApplicationModule, RouterTestingModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ConfidentialityStatementSummaryTemplateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
