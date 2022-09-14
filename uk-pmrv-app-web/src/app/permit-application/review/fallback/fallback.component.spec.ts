import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { ActivatedRouteStub, BasePage } from '../../../../testing';
import { SharedModule } from '../../../shared/shared.module';
import { SharedPermitModule } from '../../shared/shared-permit.module';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockPermitCompletePayload } from '../../testing/mock-permit-apply-action';
import { mockReviewState } from '../../testing/mock-state';
import { FallbackComponent } from './fallback.component';

describe('FallbackComponent', () => {
  let component: FallbackComponent;
  let fixture: ComponentFixture<FallbackComponent>;
  let page: Page;

  const route = new ActivatedRouteStub(
    {},
    {},
    {
      groupKey: 'FALLBACK',
    },
  );

  class Page extends BasePage<FallbackComponent> {
    get tierRows(): HTMLTableRowElement[] {
      return Array.from(this.queryAll<HTMLTableRowElement>('table tbody tr'));
    }

    get staticSections(): HTMLAnchorElement[] {
      return Array.from(this.queryAll<HTMLAnchorElement>('ul > li > span.app-task-list__task-name > a'));
    }

    get decisionForm() {
      return this.query<HTMLFormElement>('app-review-group-decision > form');
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
      declarations: [FallbackComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    const store = TestBed.inject(PermitApplicationStore);
    store.setState({
      ...mockReviewState,
      permit: mockPermitCompletePayload.permit,
      permitSectionsCompleted: mockPermitCompletePayload.permitSectionsCompleted,
      reviewGroupDecisions: {
        FALLBACK: {
          type: 'REJECTED',
          notes: 'notes',
        },
      },
      reviewSectionsCompleted: {
        FALLBACK: true,
      },
    });
    fixture = TestBed.createComponent(FallbackComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render source category names', () => {
    expect(page.tierRows.map((row) => row.cells[0].textContent.trim())).toEqual(
      expect.arrayContaining(['S1  Biogasoline: Major']),
    );
  });

  it('should render emissions info', () => {
    expect(page.tierRows.map((row) => row.cells[1].textContent.trim())).toEqual(expect.arrayContaining(['23.8 t']));
  });

  it('should render the static sections', () => {
    expect(page.staticSections.map((el) => el.textContent.trim())).toEqual(
      expect.arrayContaining(['Approach description and justification', 'Annual uncertainty analysis']),
    );
  });

  it('should display appropriate sections for review', () => {
    expect(page.decisionForm).toBeFalsy();
    expect(page.summaryDecisionValues).toEqual([['Rejected', 'notes']]);
  });
});
