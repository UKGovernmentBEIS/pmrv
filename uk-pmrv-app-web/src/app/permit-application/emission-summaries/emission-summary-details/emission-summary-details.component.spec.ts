import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../../testing';
import { PermitApplicationModule } from '../../permit-application.module';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockPermitApplyPayload } from '../../testing/mock-permit-apply-action';
import { mockPostBuild, mockState, mockStateBuild } from '../../testing/mock-state';
import { EmissionSummaryDetailsComponent } from './emission-summary-details.component';

describe('EmissionSummaryDetailsComponent', () => {
  let component: EmissionSummaryDetailsComponent;
  let fixture: ComponentFixture<EmissionSummaryDetailsComponent>;
  let page: Page;
  let router: Router;
  let store: PermitApplicationStore;

  const tasksService = mockClass(TasksService);
  let activatedRoute: ActivatedRouteStub;

  class Page extends BasePage<EmissionSummaryDetailsComponent> {
    get sourceStream(): string {
      return this.getInputValue('#sourceStream');
    }

    set sourceStream(value: string) {
      this.setInputValue('#sourceStream', value);
    }

    get emissionSources() {
      return this.fixture.componentInstance.form.get('emissionSources').value;
    }

    set emissionSources(value: string[]) {
      this.fixture.componentInstance.form.get('emissionSources').setValue(value);
    }

    get emissionPoints() {
      return this.fixture.componentInstance.form.get('emissionPoints').value;
    }

    set emissionPoints(value: string[]) {
      this.fixture.componentInstance.form.get('emissionPoints').setValue(value);
    }

    get regulatedActivity() {
      return this.getInputValue('#regulatedActivity');
    }

    set regulatedActivity(value: string) {
      this.setInputValue('#regulatedActivity', value);
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }

    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get errorSummaryLinks() {
      return Array.from(this.query<HTMLDivElement>('.govuk-error-summary').querySelectorAll('a')).map((item) =>
        item.textContent.trim(),
      );
    }
  }

  beforeEach(async () => {
    activatedRoute = new ActivatedRouteStub();
    await TestBed.configureTestingModule({
      imports: [PermitApplicationModule, RouterTestingModule],
      providers: [
        { provide: ActivatedRoute, useValue: activatedRoute },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();
  });

  const createComponent = () => {
    fixture = TestBed.createComponent(EmissionSummaryDetailsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  describe('for adding new emission summary', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(mockState);
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should submit a valid form, update the store and navigate back to task', () => {
      expect(page.errorSummary).toBeFalsy();

      const expectedSourceStream = mockPermitApplyPayload.permit.sourceStreams[0];
      const expectedEmissionSources = [
        mockPermitApplyPayload.permit.emissionSources[0].id,
        mockPermitApplyPayload.permit.emissionSources[1].id,
      ];
      const expectedEmissionPoints = [
        mockPermitApplyPayload.permit.emissionPoints[0].id,
        mockPermitApplyPayload.permit.emissionPoints[1].id,
      ];
      const expectedRegActivity = mockPermitApplyPayload.permit.regulatedActivities[0];
      const expectedEmissionSummaries = [
        ...mockPermitApplyPayload.permit.emissionSummaries,
        {
          sourceStream: expectedSourceStream.id,
          emissionSources: expectedEmissionSources,
          emissionPoints: expectedEmissionPoints,
          regulatedActivity: expectedRegActivity.id,
        },
      ];
      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryLinks).toEqual([
        'Select a source stream',
        'Select an emission source',
        'Select an emission point',
        'Select a related activity',
      ]);

      page.sourceStream = expectedSourceStream.id;
      page.emissionSources = expectedEmissionSources;
      page.emissionPoints = expectedEmissionPoints;
      page.regulatedActivity = expectedRegActivity.id;
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();

      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
        mockPostBuild({ emissionSummaries: expectedEmissionSummaries }, { emissionSummaries: [false] }),
      );
      expect(store.permit.emissionSummaries).toEqual(expectedEmissionSummaries);
      expect(navigateSpy).toHaveBeenCalledWith(['..'], { relativeTo: activatedRoute });
    });
  });

  describe('for editing existing emission point', () => {
    beforeEach(() => {
      activatedRoute.setParamMap({ emissionSummaryIndex: '0' });
      store = TestBed.inject(PermitApplicationStore);
      store.setState(mockState);
    });
    beforeEach(createComponent);

    it('should fill the form from the store', () => {
      const expectedSummary = mockPermitApplyPayload.permit.emissionSummaries[0];

      expect(page.sourceStream).toEqual(expectedSummary.sourceStream);
      expect(page.emissionSources).toEqual(expectedSummary.emissionSources);
      expect(page.emissionPoints).toEqual(expectedSummary.emissionPoints);
      expect(page.regulatedActivity).toEqual(expectedSummary.regulatedActivity);
    });

    it('should submit a valid form, update the store and navigate to summary', () => {
      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');
      const permit = mockPermitApplyPayload.permit;
      const summary = {
        sourceStream: permit.sourceStreams[1].id,
        emissionSources: [permit.emissionSources[0].id],
        emissionPoints: [permit.emissionPoints[1].id],
        regulatedActivity: permit.regulatedActivities[1].id,
      };

      page.sourceStream = summary.sourceStream;
      page.emissionSources = summary.emissionSources;
      page.emissionPoints = summary.emissionPoints;
      page.regulatedActivity = summary.regulatedActivity;
      page.submitButton.click();

      expect(store.permit.emissionSummaries).toEqual([summary]);
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['..'], { relativeTo: activatedRoute });
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
        mockPostBuild({ emissionSummaries: [summary] }, { emissionSummaries: [false] }),
      );
    });
  });

  describe('for editing existing emission point with missing dependencies', () => {
    beforeEach(() => {
      activatedRoute.setParamMap({ emissionSummaryIndex: '1' });
      store = TestBed.inject(PermitApplicationStore);
      store.setState(
        mockStateBuild({
          emissionSummaries: [
            ...mockPermitApplyPayload.permit.emissionSummaries,
            {
              sourceStream: 'not existing',
              emissionPoints: ['16363790610230.8369404469603225'],
              emissionSources: ['nan'],
              regulatedActivity: 'not existings',
            },
          ],
        }),
      );
    });

    beforeEach(createComponent);

    it('should fill the form from the store', () => {
      expect(page.sourceStream).toEqual('null');
      expect(page.emissionPoints).toEqual(['16363790610230.8369404469603225']);
      expect(page.emissionSources).toEqual([]);
      expect(page.regulatedActivity).toEqual('null');
    });
  });
});
