import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { AerModule } from '@tasks/aer/aer.module';
import { KeycloakService } from 'keycloak-angular';

import { ConfidentialityStatementSummaryTemplateComponent } from './confidentiality-statement-summary-template.component';

describe('ConfidentialityStatementSummaryTemplateComponent', () => {
  let component: ConfidentialityStatementSummaryTemplateComponent;
  let fixture: ComponentFixture<ConfidentialityStatementSummaryTemplateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AerModule, RouterTestingModule],
      providers: [KeycloakService],
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
