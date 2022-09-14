import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GovukComponentsModule } from 'govuk-components';

import { EmailConfirmedComponent } from './email-confirmed.component';

describe('EmailConfirmedComponent', () => {
  let component: EmailConfirmedComponent;
  let fixture: ComponentFixture<EmailConfirmedComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GovukComponentsModule],
      declarations: [EmailConfirmedComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EmailConfirmedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
