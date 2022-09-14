import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { AerModule } from '@tasks/aer/aer.module';
import { mockAerApplyPayload, mockState } from '@tasks/aer/submit/testing/mock-permit-apply-action';
import { mockPostBuild } from '@tasks/aer/submit/testing/mock-state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteSnapshotStub, ActivatedRouteStub, BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { AerApplicationSubmitRequestTaskPayload, TasksService } from 'pmrv-api';

import { EmissionSourceDetailsComponent } from './emission-source-details.component';

describe('EmissionSourceDetailsComponent', () => {
  let component: EmissionSourceDetailsComponent;
  let fixture: ComponentFixture<EmissionSourceDetailsComponent>;
  let page: Page;
  let router: Router;
  let route: ActivatedRoute;
  let store: CommonTasksStore;

  const tasksService = mockClass(TasksService);
  const activatedRoute = new ActivatedRouteStub();

  class Page extends BasePage<EmissionSourceDetailsComponent> {
    get reference() {
      return this.getInputValue('#reference');
    }

    set reference(value: string) {
      this.setInputValue('#reference', value);
    }

    get description() {
      return this.getInputValue('#description');
    }

    set description(value: string) {
      this.setInputValue('#description', value);
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

    get title() {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AerModule, RouterTestingModule],
      providers: [
        KeycloakService,
        { provide: ActivatedRoute, useValue: activatedRoute },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();
  });

  const createComponent = () => {
    store = TestBed.inject(CommonTasksStore);
    store.setState(mockState);
    route = TestBed.inject(ActivatedRoute);
    fixture = TestBed.createComponent(EmissionSourceDetailsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  describe('for adding new emission source', () => {
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display add title', () => {
      expect(page.title).toEqual('Add an emission source');
    });

    it('should submit a valid form, update the store and navigate back to task', () => {
      expect(page.errorSummary).toBeFalsy();

      const expectedEmissionSources = [
        ...mockAerApplyPayload.aer.emissionSources,
        {
          description: 'test description',
          id: expect.any(String),
          reference: 'test reference',
        },
      ];
      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryLinks).toEqual(['Enter a reference', 'Enter a description']);

      page.reference = 'test reference';
      page.description = 'test description';

      page.submitButton.click();
      fixture.detectChanges();
      expect(page.errorSummary).toBeFalsy();

      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
        mockPostBuild({ emissionSources: expectedEmissionSources }, { emissionSources: [false] }),
      );
      expect(
        (store.getState().requestTaskItem.requestTask.payload as AerApplicationSubmitRequestTaskPayload).aer
          .emissionSources,
      ).toEqual(expectedEmissionSources);
      expect(navigateSpy).toHaveBeenCalledWith(['..'], { relativeTo: route });
    });

    describe('for editing existing emission source', () => {
      beforeEach(() => {
        route.snapshot = new ActivatedRouteSnapshotStub({
          sourceId: mockAerApplyPayload.aer.emissionSources[0].id,
        });
      });
      beforeEach(createComponent);

      it('should display edit title', () => {
        expect(page.title).toEqual('Edit emission source');
      });

      it('should fill the form from the store', () => {
        expect(page.reference).toEqual(mockAerApplyPayload.aer.emissionSources[0].reference);
        expect(page.description).toContain(mockAerApplyPayload.aer.emissionSources[0].description);
      });

      it('should submit a valid form, update the store and navigate to summary', () => {
        tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
        const navigateSpy = jest.spyOn(router, 'navigate');
        const expectedEmissionSources = [
          {
            description: 'edit description',
            id: mockAerApplyPayload.aer.emissionSources[0].id,
            reference: 'edit reference',
          },
          ...mockAerApplyPayload.aer.emissionSources.filter(
            (source) => source.id !== mockAerApplyPayload.aer.emissionSources[0].id,
          ),
        ];

        page.description = 'edit description';
        page.reference = 'edit reference';
        page.submitButton.click();

        expect(navigateSpy).toHaveBeenCalledWith(['..'], { relativeTo: route });
        expect(
          (store.getState().requestTaskItem.requestTask.payload as AerApplicationSubmitRequestTaskPayload)
            .aerSectionsCompleted.emissionSources,
        ).toEqual([false]);
        expect(
          (store.getState().requestTaskItem.requestTask.payload as AerApplicationSubmitRequestTaskPayload).aer
            .emissionSources,
        ).toEqual(expectedEmissionSources);
        expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
          mockPostBuild({ emissionSources: expectedEmissionSources }, { emissionSources: [false] }),
        );
      });
    });
  });
});
