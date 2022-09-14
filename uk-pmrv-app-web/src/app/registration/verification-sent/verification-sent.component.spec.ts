import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { GovukComponentsModule } from 'govuk-components';

import { SharedModule } from '../../shared/shared.module';
import { VerificationSentComponent } from './verification-sent.component';

describe('VerificationSentComponent', () => {
  let component: VerificationSentComponent;
  let fixture: ComponentFixture<VerificationSentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GovukComponentsModule, SharedModule, RouterTestingModule],
      declarations: [VerificationSentComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(VerificationSentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
