import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { SummaryDetailsComponent } from '@shared/components/permit-notification/submit-summary/summary-details.component';
import { SharedModule } from '@shared/shared.module';
import { KeycloakService } from 'keycloak-angular';

describe('SummaryDetailsComponent', () => {
  let component: SummaryDetailsComponent;
  let fixture: ComponentFixture<SummaryDetailsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SummaryDetailsComponent],
      providers: [KeycloakService],
      imports: [SharedModule, RouterTestingModule, HttpClientTestingModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SummaryDetailsComponent);
    component = fixture.componentInstance;

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
