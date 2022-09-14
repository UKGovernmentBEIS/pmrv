import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';
import { BasePage } from '@testing';

import { AbbreviationsSummaryTemplateComponent } from './abbreviations-summary-template.component';

describe('AbbreviationsSummaryTemplateComponent', () => {
  let page: Page;
  let component: AbbreviationsSummaryTemplateComponent;
  let fixture: ComponentFixture<AbbreviationsSummaryTemplateComponent>;

  class Page extends BasePage<AbbreviationsSummaryTemplateComponent> {
    get abbreviationDefinitions() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AbbreviationsSummaryTemplateComponent);
    component = fixture.componentInstance;
    component.abbreviations = [
      [
        { key: 'Abbreviation, acronym or terminology', value: 'Abbreviation1' },
        { key: 'Definition', value: 'Definition1' },
      ],
      [
        { key: 'Abbreviation, acronym or terminology', value: 'Abbreviation2' },
        { key: 'Definition', value: 'Definition2' },
      ],
    ];
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary details', () => {
    expect(page.abbreviationDefinitions).toHaveLength(4);
    expect(page.abbreviationDefinitions).toEqual([
      ['Abbreviation, acronym or terminology', 'Abbreviation1'],
      ['Definition', 'Definition1'],
      ['Abbreviation, acronym or terminology', 'Abbreviation2'],
      ['Definition', 'Definition2'],
    ]);
  });
});
