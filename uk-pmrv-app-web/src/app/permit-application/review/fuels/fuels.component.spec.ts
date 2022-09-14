import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { ActivatedRouteStub, BasePage } from '@testing';

import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockReviewState } from '../../testing/mock-state';
import { ReviewModule } from '../review.module';
import { FuelsComponent } from './fuels.component';

describe('FuelsComponent', () => {
  let page: Page;
  let component: FuelsComponent;
  let store: PermitApplicationStore;
  let fixture: ComponentFixture<FuelsComponent>;
  const route = new ActivatedRouteStub(
    {},
    {},
    {
      groupKey: 'FUELS_AND_EQUIPMENT',
    },
  );

  class Page extends BasePage<FuelsComponent> {
    get notificationBanner() {
      return this.query('.govuk-notification-banner');
    }
    get listAndStatuses() {
      return this.queryAll('li')
        .map((row) => [row.querySelector('a'), row.querySelector('strong')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
    get content() {
      return this.queryAll('li')
        .map((row) => [row.querySelector('a'), row.querySelector('strong')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
    get sourceStreams() {
      return this.tableContent('Source streams (fuels and materials)');
    }
    get emissionSources() {
      return this.tableContent('Emission sources');
    }
    get emissionPoints() {
      return this.tableContent('Emission points');
    }
    get emissionSummaries() {
      return this.tableContent('Emission summaries');
    }
    get measurementDevices() {
      return this.tableContent('Measurement devices');
    }

    get decisionForm() {
      return this.query<HTMLFormElement>('form');
    }
    get summaryDecisionValues() {
      return this.queryAll<HTMLDListElement>('app-review-group-decision > dl').map((decision) =>
        Array.from(decision.querySelectorAll('dd')).map((dd) => dd.textContent.trim()),
      );
    }

    tableContent(selector: string) {
      return Array.from(
        this.queryAll('li')
          .find((li) => li.textContent.trim().startsWith(selector))
          .querySelectorAll<HTMLTableRowElement>('tbody tr'),
      ).map((tr) => Array.from(tr.querySelectorAll('td')).map((td) => td.textContent.trim()));
    }
  }

  function completeTasks() {
    store.setState({
      ...mockReviewState,
      permitSectionsCompleted: {
        ...mockReviewState.permitSectionsCompleted,
        sourceStreams: [true],
        emissionSources: [true],
        emissionPoints: [true],
        emissionSummaries: [true],
        measurementDevicesOrMethods: [true],
        regulatedActivities: [true],
      },
    });
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReviewModule, RouterTestingModule],
      providers: [{ provide: ActivatedRoute, useValue: route }],
    }).compileComponents();
  });

  beforeEach(() => {
    const router = TestBed.inject(Router);
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { notification: true } } } as any);
    store = TestBed.inject(PermitApplicationStore);
    store.setState(mockReviewState);
    fixture = TestBed.createComponent(FuelsComponent);
    page = new Page(fixture);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the notification banner', () => {
    expect(page.notificationBanner).toBeTruthy();
  });

  it('should display the list with correct statuses', () => {
    expect(page.listAndStatuses).toEqual([
      ['Source streams (fuels and materials)', 'in progress'],
      ['Emission sources', 'in progress'],
      ['Emission points', 'in progress'],
      ['Emission summaries', 'in progress'],
      ['Measurement devices', 'in progress'],
      ['Site diagram', 'completed'],
    ]);

    completeTasks();
    fixture.detectChanges();

    expect(page.listAndStatuses).toEqual([
      ['Source streams (fuels and materials)', 'completed'],
      ['Emission sources', 'completed'],
      ['Emission points', 'completed'],
      ['Emission summaries', 'needs review'],
      ['Measurement devices', 'completed'],
      ['Site diagram', 'completed'],
    ]);

    expect(page.decisionForm).toBeTruthy();
  });

  it('should display the source streams', () => {
    expect(page.sourceStreams).toEqual([]);

    completeTasks();
    fixture.detectChanges();

    expect(page.sourceStreams).toEqual([
      ['13123124', 'White Spirit & SBP', 'Refineries: Hydrogen production'],
      ['33334', 'Lignite', 'Refineries: Catalytic cracker regeneration'],
    ]);

    expect(page.decisionForm).toBeTruthy();
  });

  it('should display the emission sources', () => {
    expect(page.emissionSources).toEqual([]);

    completeTasks();
    fixture.detectChanges();

    expect(page.emissionSources).toEqual([
      ['S1', 'Boiler'],
      ['S2', 'Boiler 2'],
    ]);

    expect(page.decisionForm).toBeTruthy();
  });

  it('should display the emission points', () => {
    expect(page.emissionPoints).toEqual([]);

    completeTasks();
    fixture.detectChanges();

    expect(page.emissionPoints).toEqual([
      ['The big Ref', 'Emission point 1'],
      ['Yet another reference', 'Point taken!'],
    ]);

    expect(page.decisionForm).toBeTruthy();
  });

  it('should display the measurement devices', () => {
    expect(page.measurementDevices).toEqual([]);

    completeTasks();
    fixture.detectChanges();

    expect(page.measurementDevices).toEqual([
      ['ref1', 'Ultrasonic meter'],
      ['ref2', 'Ultrasonic meter'],
    ]);

    expect(page.decisionForm).toBeTruthy();
  });

  it('should display the emission summaries', () => {
    expect(page.emissionSummaries).toEqual([]);

    completeTasks();
    const state = store.getState();
    store.setState({
      ...state,
      permit: {
        ...state.permit,
        emissionSummaries: [
          {
            sourceStream: '16236817394240.1574963093314663',
            emissionPoints: ['16363790610230.8369404469603225'],
            emissionSources: ['16245246343280.27155194483385103'],
            regulatedActivity: '16236817394240.1574963093314664',
          },
          {
            sourceStream: '16236830126010.5957932377356623',
            emissionPoints: ['16236830126010.5957932377356623'],
            emissionSources: ['16245287634950.8123179433871819'],
            regulatedActivity: '16236817394240.1574963093314665',
          },
        ],
      },
    });
    fixture.detectChanges();

    expect(page.listAndStatuses).toEqual([
      ['Source streams (fuels and materials)', 'completed'],
      ['Emission sources', 'completed'],
      ['Emission points', 'completed'],
      ['Emission summaries', 'completed'],
      ['Measurement devices', 'completed'],
      ['Site diagram', 'completed'],
    ]);
    expect(page.emissionSummaries).toEqual([
      ['13123124 White Spirit & SBP', 'S1 Boiler', 'The big Ref Emission point 1', 'Combustion'],
      ['33334 Lignite', 'S2 Boiler 2', 'Yet another reference Point taken!', 'Mineral oil refining'],
    ]);

    expect(page.decisionForm).toBeTruthy();
  });
});
