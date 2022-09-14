import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { ActivatedRouteStub, BasePage } from '../../../../testing';
import { SharedModule } from '../../../shared/shared.module';
import { SharedPermitModule } from '../../shared/shared-permit.module';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockReviewState } from '../../testing/mock-state';
import { MonitoringApproachesComponent } from './monitoring-approaches.component';

describe('MonitoringApproachesComponent', () => {
  let component: MonitoringApproachesComponent;
  let fixture: ComponentFixture<MonitoringApproachesComponent>;
  let page: Page;
  let store: PermitApplicationStore;
  const route = new ActivatedRouteStub(
    {},
    {},
    {
      groupKey: 'DEFINE_MONITORING_APPROACHES',
    },
  );

  class Page extends BasePage<MonitoringApproachesComponent> {
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
    fixture = TestBed.createComponent(MonitoringApproachesComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, SharedPermitModule, RouterTestingModule],
      providers: [{ provide: ActivatedRoute, useValue: route }],
      declarations: [MonitoringApproachesComponent],
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
          section.querySelector('a')?.textContent.trim() ?? section.querySelector('strong').textContent.trim(),
          section.querySelector('govuk-tag').textContent.trim(),
        ]),
      ).toEqual([['Define monitoring approaches', 'cannot start yet']]);

      expect(page.decisionForm).toBeTruthy();
    });
  });

  describe('with review group decision summary', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState({
        ...mockReviewState,
        reviewGroupDecisions: {
          DEFINE_MONITORING_APPROACHES: {
            type: 'ACCEPTED',
            notes: 'notes',
          },
        },
        reviewSectionsCompleted: {
          DEFINE_MONITORING_APPROACHES: true,
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
          section.querySelector('a')?.textContent.trim() ?? section.querySelector('strong').textContent.trim(),
          section.querySelector('govuk-tag').textContent.trim(),
        ]),
      ).toEqual([['Define monitoring approaches', 'cannot start yet']]);

      expect(page.decisionForm).toBeFalsy();
      expect(page.summaryDecisionValues).toEqual([['Accepted', 'notes']]);
    });
  });
});
