import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { ActivatedRouteStub, BasePage } from '../../../../testing';
import { SharedModule } from '../../../shared/shared.module';
import { SharedPermitModule } from '../../shared/shared-permit.module';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockReviewState } from '../../testing/mock-state';
import { ConfidentialityComponent } from './confidentiality.component';

describe('ConfidentialityComponent', () => {
  let component: ConfidentialityComponent;
  let fixture: ComponentFixture<ConfidentialityComponent>;
  let page: Page;
  let store: PermitApplicationStore;
  const route = new ActivatedRouteStub(
    {},
    {},
    {
      groupKey: 'CONFIDENTIALITY_STATEMENT',
    },
  );

  class Page extends BasePage<ConfidentialityComponent> {
    get notificationBanner() {
      return this.query('.govuk-notification-banner');
    }
    get reviewSections() {
      return this.queryAll<HTMLLIElement>('li[app-task-item]');
    }
    get decisionForm() {
      return this.query<HTMLFormElement>('form');
    }
    get summaryDecisionValues() {
      return this.queryAll<HTMLDListElement>('app-review-group-decision > dl').map((decision) =>
        Array.from(decision.querySelectorAll('dd')).map((dd) => dd.textContent.trim()),
      );
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(ConfidentialityComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, SharedPermitModule, RouterTestingModule],
      providers: [{ provide: ActivatedRoute, useValue: route }],
      declarations: [ConfidentialityComponent],
    }).compileComponents();
  });

  describe('without review group decision', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(mockReviewState);
    });

    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display appropriate sections for review', () => {
      expect(
        page.reviewSections.map((section) => [
          section.querySelector('a').textContent.trim(),
          section.querySelector('govuk-tag').textContent.trim(),
        ]),
      ).toEqual([['Commercially confidential sections', 'not started']]);

      expect(page.decisionForm).toBeTruthy();
    });
  });

  describe('with review group decision summary', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState({
        ...mockReviewState,
        reviewGroupDecisions: {
          CONFIDENTIALITY_STATEMENT: {
            type: 'ACCEPTED',
            notes: 'notes',
          },
        },
        reviewSectionsCompleted: {
          CONFIDENTIALITY_STATEMENT: true,
        },
      });
    });

    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display appropriate sections for review', () => {
      expect(
        page.reviewSections.map((section) => [
          section.querySelector('a').textContent.trim(),
          section.querySelector('govuk-tag').textContent.trim(),
        ]),
      ).toEqual([['Commercially confidential sections', 'not started']]);

      expect(page.decisionForm).toBeFalsy();
      expect(page.summaryDecisionValues).toEqual([['Accepted', 'notes']]);
    });
  });
});
