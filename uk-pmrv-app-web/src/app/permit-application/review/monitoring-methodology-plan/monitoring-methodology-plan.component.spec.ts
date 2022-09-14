import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { ActivatedRouteStub, BasePage } from '../../../../testing';
import { SharedModule } from '../../../shared/shared.module';
import { SharedPermitModule } from '../../shared/shared-permit.module';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockReviewState } from '../../testing/mock-state';
import { MonitoringMethodologyPlanComponent } from './monitoring-methodology-plan.component';

describe('MonitoringMethodologyPlanComponent', () => {
  let component: MonitoringMethodologyPlanComponent;
  let fixture: ComponentFixture<MonitoringMethodologyPlanComponent>;
  let page: Page;
  let store: PermitApplicationStore;
  const route = new ActivatedRouteStub(
    {},
    {},
    {
      groupKey: 'MONITORING_METHODOLOGY_PLAN',
    },
  );

  class Page extends BasePage<MonitoringMethodologyPlanComponent> {
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
    get file() {
      const roles = this.queryAll<HTMLDListElement>('dd');
      return roles.slice(roles.length - 1)[0];
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(MonitoringMethodologyPlanComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule, SharedPermitModule],
      providers: [{ provide: ActivatedRoute, useValue: route }],
      declarations: [MonitoringMethodologyPlanComponent],
    }).compileComponents();
  });

  describe('without review group decision', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState({ ...mockReviewState });
    });

    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should mention the attachment', () => {
      expect(page.file.textContent.trim()).toEqual('No');
      expect(
        page.reviewSections.map((section) => [
          section.querySelector('a')?.textContent.trim() ?? section.querySelector('strong').textContent.trim(),
          section.querySelector('govuk-tag').textContent.trim(),
        ]),
      ).toEqual([['Monitoring methodology plan', 'not started']]);

      expect(page.decisionForm).toBeTruthy();
    });
  });

  describe('with review group decision summary', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState({
        ...mockReviewState,
        reviewGroupDecisions: {
          MONITORING_METHODOLOGY_PLAN: {
            type: 'ACCEPTED',
            notes: 'notes',
          },
        },
        reviewSectionsCompleted: {
          MONITORING_METHODOLOGY_PLAN: true,
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
      ).toEqual([['Monitoring methodology plan', 'not started']]);

      expect(page.decisionForm).toBeFalsy();
      expect(page.summaryDecisionValues).toEqual([['Accepted', 'notes']]);
    });
  });
});
