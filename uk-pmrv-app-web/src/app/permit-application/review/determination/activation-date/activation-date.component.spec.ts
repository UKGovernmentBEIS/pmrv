import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { RequestTaskAttachmentsHandlingService, TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../../../testing';
import { SharedModule } from '../../../../shared/shared.module';
import { SharedPermitModule } from '../../../shared/shared-permit.module';
import { PermitApplicationStore } from '../../../store/permit-application.store';
import { mockState } from '../../../testing/mock-state';
import { ActivationDateComponent } from './activation-date.component';

describe('ActivationDateComponent', () => {
  let page: Page;
  let router: Router;
  let store: PermitApplicationStore;
  let component: ActivationDateComponent;
  let fixture: ComponentFixture<ActivationDateComponent>;
  const route = new ActivatedRouteStub({}, {}, { statusKey: 'determination' });

  const tasksService = mockClass(TasksService);
  const attachmentService = mockClass(RequestTaskAttachmentsHandlingService);

  class Page extends BasePage<ActivationDateComponent> {
    set activationDateDay(value: string) {
      this.setInputValue('#activationDate-day', value);
    }

    set activationDateMonth(value: string) {
      this.setInputValue('#activationDate-month', value);
    }

    set activationDateYear(value: string) {
      this.setInputValue('#activationDate-year', value);
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
    fixture = TestBed.createComponent(ActivationDateComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule, SharedPermitModule],
      declarations: [ActivationDateComponent],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksService, useValue: tasksService },
        { provide: RequestTaskAttachmentsHandlingService, useValue: attachmentService },
      ],
    }).compileComponents();
  });

  describe('for activation date input', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(mockState);
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should submit a valid form', () => {
      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');

      expect(page.errorSummary).toBeFalsy();

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryErrorList).toEqual(['Enter a date']);

      page.activationDateYear = new Date().getFullYear() - 1 + '';
      page.activationDateMonth = '1';
      page.activationDateDay = '1';
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryErrorList[0]).toMatch(new RegExp('^The date must be today or in the future?'));

      page.activationDateYear = new Date().getFullYear() + 1 + '';
      page.activationDateMonth = '1';
      page.activationDateDay = '1';
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);

      expect(navigateSpy).toHaveBeenCalledWith(['../answers'], { relativeTo: route });
    });
  });
});
