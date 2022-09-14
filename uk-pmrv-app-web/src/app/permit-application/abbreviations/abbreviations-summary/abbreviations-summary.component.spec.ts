import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '../../../../testing';
import { PermitApplicationModule } from '../../permit-application.module';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockState, mockStateBuild } from '../../testing/mock-state';
import { AbbreviationsSummaryComponent } from './abbreviations-summary.component';

describe('AbbreviationsSummaryComponent', () => {
  let page: Page;
  let router: Router;
  let store: PermitApplicationStore;
  let component: AbbreviationsSummaryComponent;
  let fixture: ComponentFixture<AbbreviationsSummaryComponent>;

  class Page extends BasePage<AbbreviationsSummaryComponent> {
    get abbreviationDefinitions() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }

    get notificationBanner() {
      return this.query('.govuk-notification-banner');
    }

    get summaryHeader() {
      return this.query<HTMLElement>('h2 span');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PermitApplicationModule, RouterTestingModule],
    }).compileComponents();
  });

  function createComponent() {
    fixture = TestBed.createComponent(AbbreviationsSummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  }

  describe('abbreviation definitions summary', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(mockState);
      router = TestBed.inject(Router);
      jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { notification: true } } } as any);
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display the abbreviation definitions', () => {
      expect(page.abbreviationDefinitions).toHaveLength(4);
      expect(page.abbreviationDefinitions).toEqual([
        ['Abbreviation, acronym or terminology', 'Mr'],
        ['Definition', 'Mister'],
        ['Abbreviation, acronym or terminology', 'Ms'],
        ['Definition', 'Miss'],
      ]);
      expect(page.notificationBanner).toBeTruthy();
      expect(page.summaryHeader.textContent).toEqual('Items that need definition');
      expect(page.summaryHeader.classList).not.toContain('govuk-visually-hidden');
    });

    it('should mention that no abbreviations exist', () => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(mockStateBuild({ abbreviations: { exist: false } }));
      fixture.detectChanges();

      expect(page.abbreviationDefinitions).toHaveLength(1);
      expect(page.abbreviationDefinitions).toEqual([
        [
          'Are you using any abbreviations, acronyms or terminology in your permit application which may require definition?',
          'No',
        ],
      ]);
      expect(page.notificationBanner).toBeTruthy();
      expect(page.summaryHeader.classList).toContain('govuk-visually-hidden');
    });
  });
});
