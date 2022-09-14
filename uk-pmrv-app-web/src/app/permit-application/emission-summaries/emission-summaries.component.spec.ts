import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { BasePage, mockClass } from '../../../testing';
import { PermitApplicationModule } from '../permit-application.module';
import { PermitApplicationStore } from '../store/permit-application.store';
import { mockPermitApplyPayload } from '../testing/mock-permit-apply-action';
import { mockPostBuild, mockState } from '../testing/mock-state';
import { EmissionSummariesComponent } from './emission-summaries.component';

describe('EmissionSummariesComponent', () => {
  let component: EmissionSummariesComponent;
  let fixture: ComponentFixture<EmissionSummariesComponent>;
  let page: Page;
  let store: PermitApplicationStore;
  let router: Router;
  let route: ActivatedRoute;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<EmissionSummariesComponent> {
    get submitButton(): HTMLButtonElement {
      return this.queryAll<HTMLButtonElement>('button[type="button"]').find(
        (button) => button.textContent.trim() === 'Confirm and complete',
      );
    }

    get addEmissionSummaryBtn(): HTMLButtonElement {
      return this.queryAll<HTMLButtonElement>('button[type="button"]').find(
        (button) => button.textContent.trim() === 'Add an emission summary',
      );
    }

    get addAnotherEmissionSummaryBtn(): HTMLButtonElement {
      return this.queryAll<HTMLButtonElement>('button[type="button"]').find(
        (button) => button.textContent.trim() === 'Add another emission summary',
      );
    }

    get emissionSummaries(): HTMLDListElement[] {
      return this.queryAll<HTMLDListElement>('dl');
    }

    get emissionSummariesTextContents(): string[][] {
      return this.emissionSummaries.map((emissionSummary) =>
        Array.from(emissionSummary.querySelectorAll('dd')).map((dd) => dd.textContent.trim()),
      );
    }

    get errorSummaryLinks() {
      return Array.from(this.query<HTMLDivElement>('.govuk-error-summary')?.querySelectorAll('a') ?? []).map((item) =>
        item.textContent.trim(),
      );
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PermitApplicationModule, RouterTestingModule],
      providers: [{ provide: TasksService, useValue: tasksService }],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EmissionSummariesComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    store = TestBed.inject(PermitApplicationStore);
    router = TestBed.inject(Router);
    route = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should not allow actions if emission summaries task cannot start yet', () => {
    expect(page.submitButton).toBeFalsy();
    expect(page.addEmissionSummaryBtn).toBeFalsy();
    expect(page.addAnotherEmissionSummaryBtn).toBeFalsy();
    expect(page.emissionSummaries).toHaveLength(0);
  });

  it('should show add new emission summary button and hide complete button if task can start', () => {
    store.setState({
      ...store.getState(),
      permitSectionsCompleted: {
        sourceStreams: [true],
        emissionPoints: [true],
        emissionSources: [true],
        regulatedActivities: [true],
      },
    });
    fixture.detectChanges();

    expect(page.submitButton).toBeFalsy();
    expect(page.addEmissionSummaryBtn).toBeFalsy();
    expect(page.addAnotherEmissionSummaryBtn).toBeFalsy();
    expect(page.emissionSummaries).toHaveLength(0);

    store.setState({
      ...store.getState(),
      isEditable: true,
    });
    fixture.detectChanges();

    expect(page.addEmissionSummaryBtn).toBeTruthy();
  });

  it('should display the emission summaries', () => {
    store.setState(mockState);
    store.setState({
      ...store.getState(),
      permitSectionsCompleted: {
        sourceStreams: [true],
        emissionPoints: [true],
        emissionSources: [true],
        regulatedActivities: [true],
      },
    });
    fixture.detectChanges();

    expect(page.emissionSummariesTextContents).toEqual([
      [
        '13123124 White Spirit & SBP',
        'Change | Delete',
        'S1 Boiler  S2 Boiler 2',
        'The big Ref Emission point 1  Yet another reference Point taken!',
        'Combustion',
      ],
    ]);
  });

  it('should submit the emission summary task and navigate to summary', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
    store.setState(mockState);
    store.setState({
      ...store.getState(),
      permitSectionsCompleted: {
        ...store.getState().permitSectionsCompleted,
        sourceStreams: [true],
        emissionPoints: [true],
        emissionSources: [true],
        regulatedActivities: [true],
      },
    });
    fixture.detectChanges();

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummaryLinks).toEqual([
      '‘Mineral oil refining’ must be included in your emission summaries',
      '‘33334 Lignite’ must be included in your emission summaries',
    ]);
    expect(navigateSpy).toHaveBeenCalledTimes(0);

    const newSummary = {
      sourceStream: '16236830126010.5957932377356623',
      emissionPoints: ['16236830126010.5957932377356623'],
      emissionSources: ['16245287634950.8123179433871819'],
      regulatedActivity: '16236817394240.1574963093314665',
    };
    store.setState({
      ...store.getState(),
      permit: {
        ...store.permit,
        emissionSummaries: [...store.permit.emissionSummaries, newSummary],
      },
    });
    fixture.detectChanges();

    expect(page.errorSummaryLinks).toEqual([]);

    page.submitButton.click();

    expect(navigateSpy).toHaveBeenCalledTimes(1);
    expect(navigateSpy).toHaveBeenCalledWith(['summary'], { relativeTo: route, state: { notification: true } });
    expect(store.payload.permitSectionsCompleted.emissionPoints).toEqual([true]);
    expect(store.permit.emissionPoints).toEqual(mockPermitApplyPayload.permit.emissionPoints);

    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
      mockPostBuild(
        {
          ...mockPermitApplyPayload.permit,
          emissionSummaries: [...mockPermitApplyPayload.permit.emissionSummaries, newSummary],
        },
        {
          ...mockPermitApplyPayload.permitSectionsCompleted,
          ...{
            sourceStreams: [true],
            emissionPoints: [true],
            emissionSources: [true],
            regulatedActivities: [true],
            emissionSummaries: [true],
          },
        },
      ),
    );
  });

  it('should show errors when it needs review or is being submitted', () => {
    store.setState(mockState);
    fixture.detectChanges();

    expect(page.errorSummaryLinks).toEqual([]);

    store.setState({
      ...store.getState(),
      permit: {
        ...store.permit,
        emissionPoints: [],
        emissionSummaries: [
          {
            ...store.permit.emissionSummaries[0],
            emissionPoints: [store.permit.emissionSummaries[0].emissionPoints[0]],
          },
        ],
      },
    });
    fixture.detectChanges();

    expect(page.errorSummaryLinks).toEqual([
      'Select at least one emission point',
      '‘Mineral oil refining’ must be included in your emission summaries',
      '‘33334 Lignite’ must be included in your emission summaries',
    ]);
    expect(page.emissionSummariesTextContents).toEqual([
      [
        '13123124 White Spirit & SBP',
        'Change | Delete',
        'S1 Boiler  S2 Boiler 2',
        'Select at least one emission point',
        'Combustion',
      ],
    ]);

    store.setState({
      ...store.getState(),
      permit: {
        ...store.permit,
        emissionSources: [],
        emissionSummaries: [
          {
            ...store.permit.emissionSummaries[0],
            emissionSources: [store.permit.emissionSummaries[0].emissionSources[0]],
          },
        ],
      },
    });
    fixture.detectChanges();

    expect(page.errorSummaryLinks).toEqual([
      'Select at least one emission point',
      'Select at least one emission source',
      '‘Mineral oil refining’ must be included in your emission summaries',
      '‘33334 Lignite’ must be included in your emission summaries',
    ]);
    expect(page.emissionSummariesTextContents).toEqual([
      [
        '13123124 White Spirit & SBP',
        'Change | Delete',
        'Select at least one emission source',
        'Select at least one emission point',
        'Combustion',
      ],
    ]);

    store.setState(mockState);
    fixture.detectChanges();

    expect(page.errorSummaryLinks).toEqual([]);

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummaryLinks).toEqual([
      '‘Mineral oil refining’ must be included in your emission summaries',
      '‘33334 Lignite’ must be included in your emission summaries',
    ]);
  });
});
