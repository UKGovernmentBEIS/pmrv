import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage } from '@testing';

import { SharedPermitModule } from '../../shared/shared-permit.module';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockPermitCompletePayload } from '../../testing/mock-permit-apply-action';
import { mockReviewState } from '../../testing/mock-state';
import { UncertaintyAnalysisComponent } from './uncertainty-analysis.component';

describe('UncertaintyAnalysisComponent', () => {
  let component: UncertaintyAnalysisComponent;
  let fixture: ComponentFixture<UncertaintyAnalysisComponent>;
  let page: Page;

  const route = new ActivatedRouteStub(
    {},
    {},
    {
      groupKey: 'UNCERTAINTY_ANALYSIS',
    },
  );

  class Page extends BasePage<UncertaintyAnalysisComponent> {
    get staticSections(): HTMLAnchorElement[] {
      return Array.from(this.queryAll<HTMLAnchorElement>('span.app-task-list__task-name > a'));
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

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, SharedPermitModule, RouterTestingModule],
      providers: [{ provide: ActivatedRoute, useValue: route }],
      declarations: [UncertaintyAnalysisComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    const store = TestBed.inject(PermitApplicationStore);
    store.setState({
      ...mockReviewState,
      permit: mockPermitCompletePayload.permit,
      permitSectionsCompleted: {
        uncertaintyAnalysis: [true],
      },
      reviewGroupDecisions: {
        UNCERTAINTY_ANALYSIS: {
          type: 'ACCEPTED',
          notes: 'notes',
        },
      },
      reviewSectionsCompleted: {
        UNCERTAINTY_ANALYSIS: true,
      },
    });
    fixture = TestBed.createComponent(UncertaintyAnalysisComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the sections', () => {
    expect(page.staticSections.map((el) => el.textContent.trim())).toEqual(
      expect.arrayContaining(['Uncertainty analysis']),
    );
  });

  it('should display appropriate sections for review', () => {
    expect(page.decisionForm).toBeFalsy();
    expect(page.summaryDecisionValues).toEqual([['Accepted', 'notes']]);
  });
});
