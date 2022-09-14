import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { AerModule } from '@tasks/aer/aer.module';
import { SummaryComponent } from '@tasks/aer/submit/abbreviations/summary/summary.component';
import { mockState } from '@tasks/aer/submit/testing/mock-permit-apply-action';
import { mockStateBuild } from '@tasks/aer/submit/testing/mock-state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

describe('SummaryComponent', () => {
  let page: Page;
  let router: Router;
  let component: SummaryComponent;
  let fixture: ComponentFixture<SummaryComponent>;
  let store: CommonTasksStore;

  class Page extends BasePage<SummaryComponent> {
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
      imports: [AerModule, RouterTestingModule],
      providers: [KeycloakService],
    }).compileComponents();
  });

  function createComponent() {
    store = TestBed.inject(CommonTasksStore);
    router = TestBed.inject(Router);

    fixture = TestBed.createComponent(SummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  }

  describe('summary with values', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
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

    describe('summary with no values', () => {
      beforeEach(() => {
        store = TestBed.inject(CommonTasksStore);
        store.setState(
          mockStateBuild({
            abbreviations: {
              exist: false,
            },
          }),
        );
        router = TestBed.inject(Router);
        jest
          .spyOn(router, 'getCurrentNavigation')
          .mockReturnValue({ extras: { state: { notification: true } } } as any);
      });
      beforeEach(createComponent);

      it('should create', () => {
        expect(component).toBeTruthy();
      });

      it('should mention that no abbreviations exist', () => {
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
});
