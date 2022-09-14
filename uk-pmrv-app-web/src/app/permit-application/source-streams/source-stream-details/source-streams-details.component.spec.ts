import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { SourceStream, TasksService } from 'pmrv-api';

import { ActivatedRouteSnapshotStub, ActivatedRouteStub, BasePage, mockClass } from '../../../../testing';
import { PermitApplicationModule } from '../../permit-application.module';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockPermitApplyPayload } from '../../testing/mock-permit-apply-action';
import { mockPostBuild, mockState } from '../../testing/mock-state';
import { SourceStreamDetailsComponent } from './source-streams-details.component';

describe('SourceStreamDetailsComponent', () => {
  let component: SourceStreamDetailsComponent;
  let fixture: ComponentFixture<SourceStreamDetailsComponent>;
  let page: Page;
  let router: Router;
  let route: ActivatedRoute;
  let store: PermitApplicationStore;

  const tasksService = mockClass(TasksService);
  const activatedRoute = new ActivatedRouteStub();

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
      imports: [RouterTestingModule, PermitApplicationModule],
      providers: [
        { provide: ActivatedRoute, useValue: activatedRoute },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();
  });

  const createComponent = () => {
    store = TestBed.inject(PermitApplicationStore);
    store.setState(mockState);
    route = TestBed.inject(ActivatedRoute);
    fixture = TestBed.createComponent(SourceStreamDetailsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
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

    it('should submit a valid form, update the store and navigate back to task', () => {
      expect(page.errorSummary).toBeFalsy();

      const expectedSourceStreams = [
        ...mockPermitApplyPayload.permit.sourceStreams,
        {
          description: 'COAL_TAR',
          id: expect.any(String),
          reference: 'a random reference',
          type: 'PULP_PAPER_MAKE_UP_CHEMICALS',
        },
      ];
      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');
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

      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
        mockPostBuild({ sourceStreams: expectedSourceStreams }, { sourceStreams: [false] }),
      );
      expect(store.permit.sourceStreams).toEqual(expectedSourceStreams);
      expect(navigateSpy).toHaveBeenCalledWith(['..'], { relativeTo: route });
    });
  });

  describe('for editing existing source stream', () => {
    beforeEach(() => {
      route.snapshot = new ActivatedRouteSnapshotStub({
        streamId: mockPermitApplyPayload.permit.sourceStreams[0].id,
      });
    });
    beforeEach(createComponent);

    it('should display edit title', () => {
      expect(page.title).toEqual('Edit source stream');
    });

    it('should fill the form from the store', () => {
      expect(page.reference).toEqual(mockPermitApplyPayload.permit.sourceStreams[0].reference);
      expect(page.description).toContain(mockPermitApplyPayload.permit.sourceStreams[0].description);
      expect(page.type).toContain(mockPermitApplyPayload.permit.sourceStreams[0].type);
    });

    it('should submit a valid form, update the store and navigate to summary', () => {
      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');
      const expectedSourceStreams = [
        {
          description: 'WHITE_SPIRIT_SBP',
          id: mockPermitApplyPayload.permit.sourceStreams[0].id,
          reference: 'edited reference test',
          type: 'REFINERIES_HYDROGEN_PRODUCTION',
        },
        ...mockPermitApplyPayload.permit.sourceStreams.filter(
          (stream) => stream.id !== mockPermitApplyPayload.permit.sourceStreams[0].id,
        ),
      ];

      page.reference = 'edited reference test';
      page.submitButton.click();

      expect(navigateSpy).toHaveBeenCalledWith(['..'], { relativeTo: route });
      expect(store.payload.permitSectionsCompleted.sourceStreams).toEqual(
        mockPermitApplyPayload.permitSectionsCompleted.sourceStreams,
      );
      expect(store.permit.sourceStreams).toEqual(expectedSourceStreams);
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
        mockPostBuild({ sourceStreams: expectedSourceStreams }, { sourceStreams: [false] }),
      );
    });
  });
});
