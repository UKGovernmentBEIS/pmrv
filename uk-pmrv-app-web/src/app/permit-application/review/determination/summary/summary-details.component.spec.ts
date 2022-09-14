import { HttpClientModule } from '@angular/common/http';
import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '../../../../../testing';
import { SharedModule } from '../../../../shared/shared.module';
import { PermitApplicationStore } from '../../../store/permit-application.store';
import { mockState } from '../../../testing/mock-state';
import { SummaryDetailsComponent } from './summary-details.component';

describe('SummaryDetailsComponent', () => {
  let component: SummaryDetailsComponent;
  let fixture: ComponentFixture<TestComponent>;
  let page: Page;
  let store: PermitApplicationStore;

  @Component({
    template: `
      <app-determination-summary-details
        [decision]="decision"
        [reason]="reason"
        [activationDate]="activationDate"
      ></app-determination-summary-details>
    `,
  })
  class TestComponent {
    decision = 'GRANTED';
    reason = 'requirements are fulfilled';
    activationDate = new Date('2030-01-01T00:00:00Z').toISOString();
  }

  class Page extends BasePage<TestComponent> {
    get summaryDefinitions() {
      return this.queryAll('dd').map((row) => row.textContent.trim());
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientModule, SharedModule],
      declarations: [TestComponent, SummaryDetailsComponent],
      providers: [],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(PermitApplicationStore);
    store.setState(mockState);
    fixture = TestBed.createComponent(TestComponent);
    component = fixture.debugElement.query(By.directive(SummaryDetailsComponent)).componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the determination summary', () => {
    expect(page.summaryDefinitions).toEqual([
      'Grant',
      'Change',
      'requirements are fulfilled',
      'Change',
      '1 Jan 2030',
      'Change',
    ]);
  });
});
