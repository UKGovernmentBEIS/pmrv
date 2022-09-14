import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { ReviewDecisionSummaryComponent } from '@shared/components/permit-notification/review-decision-summary/review-decision-summary.component';
import { SharedModule } from '@shared/shared.module';
import { KeycloakService } from 'keycloak-angular';

describe('ReviewDecisionSummaryComponent', () => {
  let component: ReviewDecisionSummaryComponent;
  let fixture: ComponentFixture<ReviewDecisionSummaryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ReviewDecisionSummaryComponent],
      providers: [KeycloakService],
      imports: [SharedModule, RouterTestingModule, HttpClientTestingModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ReviewDecisionSummaryComponent);
    component = fixture.componentInstance;

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
