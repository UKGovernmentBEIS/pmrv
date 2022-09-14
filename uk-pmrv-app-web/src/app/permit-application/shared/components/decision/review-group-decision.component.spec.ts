import { HttpResponse } from '@angular/common/http';
import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, asyncData, BasePage, mockClass } from '@testing';

import {
  PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload,
  RequestTaskAttachmentsHandlingService,
  TasksService,
} from 'pmrv-api';

import { PermitApplicationStore } from '../../../store/permit-application.store';
import { mockReviewState } from '../../../testing/mock-state';
import { SharedPermitModule } from '../../shared-permit.module';

describe('ReviewGroupDecisionComponent', () => {
  let page: Page;
  let component: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let store: PermitApplicationStore;

  const attachmentService = mockClass(RequestTaskAttachmentsHandlingService);
  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub(
    {},
    {},
    {
      groupKey: 'MONITORING_METHODOLOGY_PLAN',
    },
  );

  @Component({
    template: `
      <app-review-group-decision
        [canEdit]="canEdit"
        [groupKey]="groupKey"
        (notification)="reviewDecisionSubmitted($event)"
      ></app-review-group-decision>
    `,
  })
  class TestComponent {
    canEdit = true;
    groupKey: PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload['group'] = 'MONITORING_METHODOLOGY_PLAN';
    notification = false;

    reviewDecisionSubmitted(_: any): void {
      this.notification = _;
    }
  }

  class Page extends BasePage<TestComponent> {
    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }
    get errorSummaryErrorList() {
      return Array.from(this.errorSummary.querySelectorAll('a')).map((anchor) => anchor.textContent.trim());
    }
    get submitFormButton() {
      return this.queryAll<HTMLButtonElement>('button').slice(-1).pop();
    }
    get changeLink() {
      return this.query<HTMLLinkElement>('a');
    }
    get acceptDecisionOption() {
      return this.query<HTMLInputElement>('#decision-option0');
    }
    get rejectDecisionOption() {
      return this.query<HTMLInputElement>('#decision-option1');
    }
    get operatorAmendsNeededDecisionOption() {
      return this.query<HTMLInputElement>('#decision-option2');
    }
    get reviewNotesValue() {
      return this.getInputValue('#notes');
    }
    set reviewNotesValue(value: string) {
      this.setInputValue('#notes', value);
    }
    get changesRequiredValue() {
      return this.getInputValue('#changesRequired');
    }
    set changesRequiredValue(value: string) {
      this.setInputValue('#changesRequired', value);
    }
    get filesText() {
      return this.queryAll<HTMLDivElement>('.moj-multi-file-upload__message');
    }
    set fileValue(value: any) {
      this.setInputValue('input[type="file"]', value);
    }
    get fileLabel() {
      return this.query<HTMLButtonElement>('label[for="files"]');
    }
    get summaryPairText() {
      return Array.from(this.queryAll<HTMLDivElement>('.govuk-summary-list__row'))
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .filter(([, data]) => !!data)
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
    get changeDecisionLink() {
      return this.query<HTMLAnchorElement>('h2[app-summary-header] a');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(TestComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, SharedPermitModule, RouterTestingModule],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksService, useValue: tasksService },
        { provide: RequestTaskAttachmentsHandlingService, useValue: attachmentService },
      ],
      declarations: [TestComponent],
    }).compileComponents();
  });

  describe('for unsubmitted review decision group without decision data', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState({
        ...mockReviewState,
        permitSectionsCompleted: {
          monitoringMethodologyPlans: [true],
        },
      });
    });

    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should submit a valid form and emit the decision', () => {
      const reviewDecisionSubmittedSpy = jest.spyOn(component, 'reviewDecisionSubmitted');
      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));

      expect(page.errorSummary).toBeFalsy();
      expect(page.acceptDecisionOption.checked).toBe(false);
      expect(page.rejectDecisionOption.checked).toBe(false);
      expect(page.operatorAmendsNeededDecisionOption.checked).toBe(false);

      page.submitFormButton.click();
      fixture.detectChanges();

      expect(reviewDecisionSubmittedSpy).toHaveBeenCalledTimes(0);
      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryErrorList).toEqual([
        'Select a decision for this review group',
        'Enter notes for this review group',
      ]);

      page.operatorAmendsNeededDecisionOption.click();
      page.submitFormButton.click();
      fixture.detectChanges();

      expect(reviewDecisionSubmittedSpy).toHaveBeenCalledTimes(0);
      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryErrorList).toEqual([
        'Enter all the changes required for this review group',
        'Enter notes for this review group',
      ]);

      page.acceptDecisionOption.click();
      page.submitFormButton.click();
      fixture.detectChanges();

      expect(reviewDecisionSubmittedSpy).toHaveBeenCalledTimes(0);
      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryErrorList).toEqual(['Enter notes for this review group']);

      page.reviewNotesValue = 'Review notes';
      page.submitFormButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith({
        requestTaskActionType: 'PERMIT_ISSUANCE_SAVE_REVIEW_GROUP_DECISION',
        requestTaskId: 237,
        requestTaskActionPayload: {
          payloadType: 'PERMIT_ISSUANCE_SAVE_REVIEW_GROUP_DECISION_PAYLOAD',
          group: 'MONITORING_METHODOLOGY_PLAN',
          decision: {
            type: 'ACCEPTED',
            notes: 'Review notes',
          },
          reviewSectionsCompleted: {
            MONITORING_METHODOLOGY_PLAN: true,
          },
        },
      });
      expect(page.summaryPairText).toEqual([
        ['Decision status', 'Accepted'],
        ['Notes', 'Review notes'],
      ]);
      expect(reviewDecisionSubmittedSpy).toHaveBeenCalledTimes(1);
      expect(reviewDecisionSubmittedSpy).toHaveBeenCalledWith(true);
    });

    it('should submit a valid form with file and emit the decision', async () => {
      const reviewDecisionSubmittedSpy = jest.spyOn(component, 'reviewDecisionSubmitted');
      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
      attachmentService.uploadRequestTaskAttachmentUsingPOST.mockReturnValue(
        asyncData<any>(new HttpResponse({ body: { uuid: 'xzy' } })),
      );

      page.operatorAmendsNeededDecisionOption.click();
      fixture.detectChanges();

      page.reviewNotesValue = 'Review notes';
      page.changesRequiredValue = 'Changes Required';

      const file = new File(['some content'], 'new-file.txt');
      page.fileValue = [file];
      fixture.detectChanges();
      await fixture.whenStable();
      fixture.detectChanges();

      expect(page.filesText.map((row) => row.textContent.trim())).toEqual(['new-file.txt has been uploaded']);
      expect(attachmentService.uploadRequestTaskAttachmentUsingPOST).toHaveBeenCalledWith(
        file,
        {
          requestTaskActionType: 'PERMIT_ISSUANCE_UPLOAD_REVIEW_GROUP_DECISION_ATTACHMENT',
          requestTaskId: mockReviewState.requestTaskId,
        },
        'events',
        true,
      );
      expect(page.fileLabel.textContent).toBe('Upload a different file');

      page.submitFormButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith({
        requestTaskActionType: 'PERMIT_ISSUANCE_SAVE_REVIEW_GROUP_DECISION',
        requestTaskId: 237,
        requestTaskActionPayload: {
          payloadType: 'PERMIT_ISSUANCE_SAVE_REVIEW_GROUP_DECISION_PAYLOAD',
          group: 'MONITORING_METHODOLOGY_PLAN',
          decision: {
            type: 'OPERATOR_AMENDS_NEEDED',
            changesRequired: 'Changes Required',
            files: ['xzy'],
            notes: 'Review notes',
          },
          reviewSectionsCompleted: {
            MONITORING_METHODOLOGY_PLAN: true,
          },
        },
      });

      expect(page.summaryPairText).toEqual([
        ['Decision status', 'Operator amends needed'],
        ['Notes to operator (external)', 'Changes Required'],
        ['Files for operator (external)', 'new-file.txt'],
        ['Notes', 'Review notes'],
      ]);
      expect(reviewDecisionSubmittedSpy).toHaveBeenCalledTimes(1);
      expect(reviewDecisionSubmittedSpy).toHaveBeenCalledWith(true);
    });
  });

  describe('for submitted review decision group with decision data', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState({
        ...mockReviewState,
        reviewGroupDecisions: {
          MONITORING_METHODOLOGY_PLAN: {
            type: 'ACCEPTED',
            notes: 'Review notes',
          },
        },
        reviewSectionsCompleted: {
          MONITORING_METHODOLOGY_PLAN: true,
        },
        permitSectionsCompleted: {
          monitoringMethodologyPlans: [true],
        },
      });
    });

    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display change link', () => {
      expect(page.changeDecisionLink).toBeTruthy();
    });

    it('should change and submit new decision', () => {
      const reviewDecisionSubmittedSpy = jest.spyOn(component, 'reviewDecisionSubmitted');
      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));

      expect(page.errorSummary).toBeFalsy();

      page.changeLink.click();
      fixture.detectChanges();

      page.rejectDecisionOption.click();
      page.submitFormButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith({
        requestTaskActionType: 'PERMIT_ISSUANCE_SAVE_REVIEW_GROUP_DECISION',
        requestTaskId: 237,
        requestTaskActionPayload: {
          payloadType: 'PERMIT_ISSUANCE_SAVE_REVIEW_GROUP_DECISION_PAYLOAD',
          group: 'MONITORING_METHODOLOGY_PLAN',
          decision: {
            type: 'REJECTED',
            notes: 'Review notes',
          },
          reviewSectionsCompleted: {
            MONITORING_METHODOLOGY_PLAN: true,
          },
        },
      });

      expect(page.summaryPairText).toEqual([
        ['Decision status', 'Rejected'],
        ['Notes', 'Review notes'],
      ]);
      expect(reviewDecisionSubmittedSpy).toHaveBeenCalledTimes(1);
      expect(reviewDecisionSubmittedSpy).toHaveBeenCalledWith(true);
    });

    it('should show decision summary list', () => {
      expect(page.errorSummary).toBeFalsy();

      expect(page.summaryPairText).toEqual([
        ['Decision status', 'Accepted'],
        ['Notes', 'Review notes'],
      ]);
    });
  });

  describe('for submitted review decision group with decision data', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState({
        ...mockReviewState,
        reviewGroupDecisions: {
          MONITORING_METHODOLOGY_PLAN: {
            type: 'ACCEPTED',
            notes: 'Review notes',
          },
        },
        reviewSectionsCompleted: {
          MONITORING_METHODOLOGY_PLAN: true,
        },
        permitSectionsCompleted: {
          monitoringMethodologyPlans: [true],
        },
      });
    });

    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should not display change link', () => {
      component.canEdit = false;
      fixture.detectChanges();
      expect(page.changeDecisionLink).toBeNull();
    });
  });

  describe('for submitted review decision group with tasks not completed', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState({
        ...mockReviewState,
        reviewGroupDecisions: {
          MONITORING_METHODOLOGY_PLAN: {
            type: 'ACCEPTED',
            notes: 'Review notes',
          },
        },
        reviewSectionsCompleted: {
          MONITORING_METHODOLOGY_PLAN: true,
        },
        permitSectionsCompleted: {
          monitoringMethodologyPlans: [false],
        },
      });
    });

    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display error message', () => {
      expect(page.errorSummary).toBeFalsy();

      page.changeLink.click();
      fixture.detectChanges();

      page.rejectDecisionOption.click();
      page.submitFormButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummary.textContent.trim()).toEqual('There is a problem All sections must be completed');
    });
  });
});
