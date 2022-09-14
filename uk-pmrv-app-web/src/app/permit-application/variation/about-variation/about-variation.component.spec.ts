import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { BasePage, mockClass } from '../../../../testing';
import { SharedModule } from '../../../shared/shared.module';
import { PermitApplicationModule } from '../../permit-application.module';
import { SharedPermitModule } from '../../shared/shared-permit.module';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockVariationSubmitState } from '../../testing/mock-state';
import { AboutVariationComponent } from './about-variation.component';

describe('AboutVariationComponent', () => {
  let page: Page;
  let store: PermitApplicationStore;
  let component: AboutVariationComponent;
  let fixture: ComponentFixture<AboutVariationComponent>;

  const tasksService = mockClass(TasksService);
  class Page extends BasePage<AboutVariationComponent> {
    get reason() {
      return this.getInputValue('#reason');
    }

    set reason(value: string) {
      this.setInputValue('#reason', value);
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }

    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get errorSummaryErrorList() {
      return Array.from(this.query<HTMLDivElement>('.govuk-error-summary').querySelectorAll('a')).map((anchor) =>
        anchor.textContent.trim(),
      );
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(AboutVariationComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);

    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule, SharedPermitModule, PermitApplicationModule],
      providers: [{ provide: TasksService, useValue: tasksService }],
    }).compileComponents();
  });

  describe('for new about variation', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState({
        ...mockVariationSubmitState,
        permitVariationDetails: null,
        permitVariationDetailsCompleted: false,
      });
    });

    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should submit a valid form, update the store and navigate to summary', () => {
      expect(page.errorSummary).toBeFalsy();

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryErrorList).toEqual(['Enter details of the change']);

      page.reason = 'Reason';

      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith({
        requestTaskActionType: 'PERMIT_VARIATION_OPERATOR_SAVE_APPLICATION',
        requestTaskId: mockVariationSubmitState.requestTaskId,
        requestTaskActionPayload: {
          payloadType: 'PERMIT_VARIATION_OPERATOR_SAVE_APPLICATION_PAYLOAD',
          permit: mockVariationSubmitState.permit,
          permitSectionsCompleted: mockVariationSubmitState.permitSectionsCompleted,
          permitVariationDetails: {
            reason: 'Reason',
          },
          permitVariationDetailsCompleted: false,
          reviewSectionsCompleted: { ...mockVariationSubmitState.reviewSectionsCompleted, determination: false },
        },
      });
    });
  });

  describe('for existing about variation', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState({
        ...mockVariationSubmitState,
        permitVariationDetails: { reason: 'Reason' },
        permitVariationDetailsCompleted: false,
      });
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should fill the form from the store', () => {
      expect(page.reason).toEqual(store.getState().permitVariationDetails.reason);
    });

    it('should submit a valid form', () => {
      expect(page.errorSummary).toBeFalsy();

      page.reason = '';

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryErrorList).toEqual(['Enter details of the change']);

      page.reason = 'New reason';

      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);

      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith({
        requestTaskActionType: 'PERMIT_VARIATION_OPERATOR_SAVE_APPLICATION',
        requestTaskId: mockVariationSubmitState.requestTaskId,
        requestTaskActionPayload: {
          payloadType: 'PERMIT_VARIATION_OPERATOR_SAVE_APPLICATION_PAYLOAD',
          permit: mockVariationSubmitState.permit,
          permitSectionsCompleted: mockVariationSubmitState.permitSectionsCompleted,
          permitVariationDetails: {
            reason: 'New reason',
          },
          permitVariationDetailsCompleted: false,
          reviewSectionsCompleted: { ...mockVariationSubmitState.reviewSectionsCompleted, determination: false },
        },
      });
    });
  });
});
