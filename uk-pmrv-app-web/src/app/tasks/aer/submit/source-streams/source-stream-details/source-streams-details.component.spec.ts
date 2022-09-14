import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { AerModule } from '@tasks/aer/aer.module';
import { AerService } from '@tasks/aer/core/aer.service';
import { mockAerApplyPayload, mockState } from '@tasks/aer/submit/testing/mock-permit-apply-action';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteSnapshotStub, ActivatedRouteStub, BasePage, MockType } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { SourceStream, TasksService } from 'pmrv-api';

import { SourceStreamDetailsComponent } from './source-streams-details.component';

describe('SourceStreamDetailsComponent', () => {
  let component: SourceStreamDetailsComponent;
  let fixture: ComponentFixture<SourceStreamDetailsComponent>;
  let page: Page;
  let router: Router;
  let route: ActivatedRoute;
  let store: CommonTasksStore;
  let aerService: AerService;

  const activatedRoute = new ActivatedRouteStub();
  const tasksService: MockType<TasksService> = {
    processRequestTaskActionUsingPOST: jest.fn().mockReturnValue(of(null)),
  };

  class Page extends BasePage<SourceStreamDetailsComponent> {
    get reference() {
      return this.getInputValue('#reference');
    }

    set reference(value: string) {
      this.setInputValue('#reference', value);
    }

    get description() {
      return this.getInputValue('#description');
    }

    set description(value: SourceStream['description']) {
      this.setInputValue('#description', value);
    }

    get type() {
      return this.getInputValue('#type');
    }

    set type(value: SourceStream['type']) {
      this.setInputValue('#type', value);
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
      imports: [RouterTestingModule, AerModule],
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
    fixture = TestBed.createComponent(SourceStreamDetailsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    aerService = TestBed.inject(AerService);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  describe('for adding new source stream', () => {
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display add title', () => {
      expect(page.title).toEqual('Add a source stream');
    });

    it('should raise validation error for OTHER description', () => {
      page.reference = 'a random reference';
      page.description = 'OTHER';
      page.type = 'PULP_PAPER_MAKE_UP_CHEMICALS';
      fixture.detectChanges();
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryLinks).toEqual(['Enter a description']);
    });

    it('should submit a valid form, update the store and navigate back', () => {
      expect(page.errorSummary).toBeFalsy();

      const expectedSourceStreams = [
        ...mockAerApplyPayload.aer.sourceStreams,
        {
          description: 'COAL_TAR',
          id: expect.any(String),
          reference: 'a random reference',
          type: 'PULP_PAPER_MAKE_UP_CHEMICALS',
        },
      ];
      const navigateSpy = jest.spyOn(router, 'navigate');
      const postTaskSaveSpy = jest.spyOn(aerService, 'postTaskSave');
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryLinks).toEqual(['Enter a reference', 'Select a description', 'Select a type']);

      page.reference = 'a random reference';
      page.description = 'COAL_TAR';
      page.type = 'PULP_PAPER_MAKE_UP_CHEMICALS';
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();

      page.submitButton.click();
      fixture.detectChanges();
      expect(page.errorSummary).toBeFalsy();

      expect(postTaskSaveSpy).toHaveBeenCalledTimes(1);
      expect(postTaskSaveSpy).toHaveBeenCalledWith(
        { sourceStreams: expectedSourceStreams },
        undefined,
        false,
        'sourceStreams',
      );
      expect(navigateSpy).toHaveBeenCalledWith(['..'], { relativeTo: route });
    });
  });

  describe('for editing existing source stream', () => {
    beforeEach(() => {
      route.snapshot = new ActivatedRouteSnapshotStub({
        streamId: mockAerApplyPayload.aer.sourceStreams[0].id,
      });
    });
    beforeEach(createComponent);

    it('should display edit title', () => {
      expect(page.title).toEqual('Edit source stream');
    });

    it('should fill the form from the store', () => {
      expect(page.reference).toEqual(mockAerApplyPayload.aer.sourceStreams[0].reference);
      expect(page.description).toContain(mockAerApplyPayload.aer.sourceStreams[0].description);
      expect(page.type).toContain(mockAerApplyPayload.aer.sourceStreams[0].type);
    });

    it('should submit a valid form, update the store and navigate back', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      const postTaskSaveSpy = jest.spyOn(aerService, 'postTaskSave');
      const expectedSourceStreams = [
        {
          description: 'ANTHRACITE',
          id: mockAerApplyPayload.aer.sourceStreams[0].id,
          reference: 'edited reference test',
          type: 'AMMONIA_FUEL_AS_PROCESS_INPUT',
        },
        ...mockAerApplyPayload.aer.sourceStreams.filter(
          (stream) => stream.id !== mockAerApplyPayload.aer.sourceStreams[0].id,
        ),
      ];

      page.reference = 'edited reference test';
      page.submitButton.click();

      expect(postTaskSaveSpy).toHaveBeenCalledWith(
        { sourceStreams: expectedSourceStreams },
        undefined,
        false,
        'sourceStreams',
      );
      expect(navigateSpy).toHaveBeenCalledWith(['..'], { relativeTo: route });
    });
  });
});
