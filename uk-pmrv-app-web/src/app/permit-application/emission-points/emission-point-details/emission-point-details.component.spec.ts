import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteSnapshotStub, ActivatedRouteStub, BasePage, mockClass } from '../../../../testing';
import { PermitApplicationModule } from '../../permit-application.module';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockPermitApplyPayload } from '../../testing/mock-permit-apply-action';
import { mockPostBuild, mockState } from '../../testing/mock-state';
import { EmissionPointDetailsComponent } from './emission-point-details.component';

describe('EmissionPointDetailsComponent', () => {
  let component: EmissionPointDetailsComponent;
  let fixture: ComponentFixture<EmissionPointDetailsComponent>;
  let page: Page;
  let router: Router;
  let store: PermitApplicationStore;

  const tasksService = mockClass(TasksService);
  const activatedRoute = new ActivatedRouteStub();

  class Page extends BasePage<EmissionPointDetailsComponent> {
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
      imports: [PermitApplicationModule, RouterTestingModule],
      providers: [
        { provide: ActivatedRoute, useValue: activatedRoute },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();
  });

  const createComponent = () => {
    store = TestBed.inject(PermitApplicationStore);
    store.setState(mockState);
    fixture = TestBed.createComponent(EmissionPointDetailsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  describe('for adding new emission points', () => {
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display add title', () => {
      expect(page.title).toEqual('Add an emission point');
    });

    it('should submit a valid form, update the store and navigate back to task', () => {
      expect(page.errorSummary).toBeFalsy();

      const expectedEmissionPoints = [
        ...mockPermitApplyPayload.permit.emissionPoints,
        {
          description: 'a random description',
          id: expect.any(String),
          reference: 'a random reference',
        },
      ];
      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryLinks).toEqual(['Enter a reference', 'Enter a description']);

      page.reference = expectedEmissionPoints[expectedEmissionPoints.length - 1].reference;
      page.description = expectedEmissionPoints[expectedEmissionPoints.length - 1].description;

      page.submitButton.click();
      fixture.detectChanges();
      expect(page.errorSummary).toBeFalsy();

      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
        mockPostBuild({ emissionPoints: expectedEmissionPoints }, { emissionPoints: [false] }),
      );
      expect(store.permit.emissionPoints).toEqual(expectedEmissionPoints);
      expect(navigateSpy).toHaveBeenCalledWith(['..'], { relativeTo: activatedRoute });
    });
  });

  describe('for editing existing emission point', () => {
    beforeEach(() => {
      TestBed.inject(ActivatedRoute).snapshot = new ActivatedRouteSnapshotStub({
        emissionPointId: mockPermitApplyPayload.permit.emissionPoints[0].id,
      });
    });
    beforeEach(createComponent);

    it('should display edit title', () => {
      expect(page.title).toEqual('Edit emission point');
    });

    it('should fill the form from the store', () => {
      expect(page.reference).toEqual('The big Ref');
      expect(page.description).toContain('Emission point 1');
    });

    it('should submit a valid form, update the store and navigate to summary', () => {
      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');
      const expectedEmissionPoints = [
        {
          description: 'descr edited',
          id: mockPermitApplyPayload.permit.emissionPoints[0].id,
          reference: 'ref edited',
        },
        ...mockPermitApplyPayload.permit.emissionPoints.filter(
          (stream) => stream.id !== mockPermitApplyPayload.permit.emissionPoints[0].id,
        ),
      ];

      page.reference = 'ref edited';
      page.description = 'descr edited';
      page.submitButton.click();

      expect(store.permit.emissionPoints).toEqual(expectedEmissionPoints);
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['..'], { relativeTo: activatedRoute });
    });
  });
});
