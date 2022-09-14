import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { BasePage, mockClass } from '../../../testing';
import { PermitApplicationModule } from '../permit-application.module';
import { PermitApplicationStore } from '../store/permit-application.store';
import { mockPermitApplyPayload } from '../testing/mock-permit-apply-action';
import { mockPostBuild, mockState, mockStateBuild } from '../testing/mock-state';
import { AbbreviationsComponent } from './abbreviations.component';

describe('AbbreviationsComponent', () => {
  let page: Page;
  let router: Router;
  let store: PermitApplicationStore;
  let activatedRoute: ActivatedRoute;
  let component: AbbreviationsComponent;
  let fixture: ComponentFixture<AbbreviationsComponent>;

  const tasksService = mockClass(TasksService);
  const mockedDefinitionValues = mockPermitApplyPayload.permit.abbreviations.abbreviationDefinitions.map(
    (e) => e.definition,
  );
  const mockedAbbreviationValues = mockPermitApplyPayload.permit.abbreviations.abbreviationDefinitions.map(
    (e) => e.abbreviation,
  );

  class Page extends BasePage<AbbreviationsComponent> {
    get existRadioButtons() {
      return this.queryAll<HTMLInputElement>('input[name$="exist"]');
    }

    get abbreviations() {
      return this.queryAll<HTMLInputElement>('input[id^="abbreviationDefinitions"][id$="abbreviation"]');
    }

    get abbreviationValues() {
      return this.abbreviations.map((input) => input.value);
    }

    set abbreviationValues(values: string[]) {
      values.forEach((value, index) => this.setInputValue(`#abbreviationDefinitions.${index}.abbreviation`, value));
    }

    get definitions() {
      return this.queryAll<HTMLInputElement>('input[id^="abbreviationDefinitions"][id$="definition"]');
    }

    get definitionValues() {
      return this.definitions.map((input) => input.value);
    }

    set definitionValues(values: string[]) {
      values.forEach((value, index) => this.setInputValue(`#abbreviationDefinitions.${index}.definition`, value));
    }

    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get errorSummaryList() {
      return Array.from(this.errorSummary.querySelectorAll('a')).map((anchor) => anchor.textContent.trim());
    }

    get addAnotherButton() {
      return this.query<HTMLButtonElement>('button.govuk-button--secondary:not(.moj-add-another__remove-button)');
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(AbbreviationsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PermitApplicationModule, RouterTestingModule],
      providers: [{ provide: TasksService, useValue: tasksService }],
    }).compileComponents();
  });

  describe('for new abbreviations', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(mockStateBuild({ abbreviations: null }));
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display an empty form', () => {
      expect(page.existRadioButtons.every((radio) => !radio.checked)).toBeTruthy();
      expect(page.abbreviations).toHaveLength(0);
      expect(page.errorSummary).toBeFalsy();
    });

    it('should submit a valid form without definitions for not exist option', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));

      page.existRadioButtons[1].click();
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.existRadioButtons[1].checked).toBeTruthy();
      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
        mockPostBuild(
          { abbreviations: { abbreviationDefinitions: undefined, exist: false } },
          { abbreviations: [true] },
        ),
      );
      expect(navigateSpy).toHaveBeenCalledWith(['summary'], {
        relativeTo: activatedRoute,
        state: { notification: true },
      });
    });

    it('should submit a valid form with definitions for exist option', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));

      page.existRadioButtons[0].click();
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryList).toEqual(['Enter an abbreviation, acronym or term', 'Enter a definition']);

      page.addAnotherButton.click();
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryList).toEqual([
        'Enter an abbreviation, acronym or term',
        'Enter a definition',
        'Enter an abbreviation, acronym or term',
        'Enter a definition',
      ]);

      page.abbreviationValues = mockedAbbreviationValues;
      page.definitionValues = mockedDefinitionValues;
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();

      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
        mockPostBuild(undefined, { abbreviations: [true] }),
      );
      expect(navigateSpy).toHaveBeenCalledWith(['summary'], {
        relativeTo: activatedRoute,
        state: { notification: true },
      });
    });
  });

  describe('for existing abbreviations', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(mockState);
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should fill the form from the store', () => {
      expect(page.abbreviations).toHaveLength(2);
      expect(page.abbreviationValues).toEqual(mockedAbbreviationValues);
      expect(page.definitionValues).toEqual(mockedDefinitionValues);
      expect(page.errorSummary).toBeFalsy();
    });

    it('should clear definitions and submit form if not exist option', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));

      page.existRadioButtons[1].click();
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
        mockPostBuild(
          { abbreviations: { abbreviationDefinitions: undefined, exist: false } },
          { abbreviations: [true] },
        ),
      );
      expect(navigateSpy).toHaveBeenCalledWith(['summary'], {
        relativeTo: activatedRoute,
        state: { notification: true },
      });
    });
  });
});
