import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { BasePage, mockClass } from '@testing';

import { MiReportsService } from '../../../../dist/pmrv-api';
import { mockExecutedRequestActionMiReportResult } from '../testing/mock-data';
import { CompletedWorkComponent } from './completed-work.component';

describe('CompletedWorkComponent', () => {
  let component: CompletedWorkComponent;
  let fixture: ComponentFixture<CompletedWorkComponent>;
  let page: Page;
  const miReportsService = mockClass(MiReportsService);

  class Page extends BasePage<CompletedWorkComponent> {
    get reportOptions() {
      return this.queryAll<HTMLInputElement>('input[name$="option"]');
    }

    get yearOptionValue() {
      return this.getInputValue('#year');
    }

    set yearOptionValue(value: string) {
      this.setInputValue('#year', value);
    }
    get optionsErrorMessage() {
      return this.query<HTMLElement>('div[formcontrolname="option"] span.govuk-error-message');
    }

    get yearOptionErrorMessage() {
      return this.query<HTMLElement>('div[formcontrolname="year"] span.govuk-error-message');
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }

    get noResults() {
      return this.query<HTMLDivElement>('.noResults p');
    }

    get table() {
      return this.query<HTMLDivElement>('.govuk-table');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule],
      declarations: [CompletedWorkComponent],
      providers: [{ provide: MiReportsService, useValue: miReportsService }],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CompletedWorkComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    miReportsService.generateReportUsingPOST.mockReturnValueOnce(of(mockExecutedRequestActionMiReportResult));
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should return report data', () => {
    expect(page.noResults.textContent.trim()).toEqual('There are no results to show');
    expect(page.reportOptions.length).toEqual(2);
    page.reportOptions.every((option) => expect(option.checked).toBeFalsy());

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.optionsErrorMessage).toBeTruthy();
    expect(page.optionsErrorMessage.textContent.trim()).toContain('Select an option');

    page.reportOptions[1].click();
    page.submitButton.click();
    fixture.detectChanges();

    expect(page.yearOptionErrorMessage).toBeTruthy();
    expect(page.yearOptionErrorMessage.textContent.trim()).toContain('Enter a year value');

    page.yearOptionValue = 'abcd';
    page.submitButton.click();
    fixture.detectChanges();

    expect(page.yearOptionErrorMessage).toBeTruthy();
    expect(page.yearOptionErrorMessage.textContent.trim()).toContain('Enter a valid year value e.g. 2022');

    page.yearOptionValue = '2022';
    page.submitButton.click();
    fixture.detectChanges();

    expect(page.yearOptionErrorMessage).toBeFalsy();
    expect(miReportsService.generateReportUsingPOST).toHaveBeenCalledTimes(1);
    expect(miReportsService.generateReportUsingPOST).toHaveBeenCalledWith({
      reportType: 'COMPLETED_WORK',
      fromDate: '2022-01-01',
      toDate: '2023-01-01',
    });
    const cells = Array.from(page.table.querySelectorAll('td'));
    expect(cells.map((cell) => cell.textContent.trim())).toEqual([
      'Installation',
      '1',
      'Installation name',
      'New',
      'Legal entity Name',
      'UK-W-15',
      'REQ-123',
      'Permit Application',
      'In Progress',
      'Permit application approved',
      'Teo James',
      '12 Aug 2022',
    ]);
  });
});
