import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { EnvironmentalPermitsAndLicences, TasksService } from 'pmrv-api';

import { BasePage, mockClass } from '../../../testing';
import { PermitApplicationModule } from '../permit-application.module';
import { PermitApplicationStore } from '../store/permit-application.store';
import { mockPermitApplyPayload } from '../testing/mock-permit-apply-action';
import { mockPostBuild, mockStateBuild } from '../testing/mock-state';
import { OtherPermitsComponent } from './other-permits.component';

describe('OtherPermitsComponent', () => {
  let component: OtherPermitsComponent;
  let fixture: ComponentFixture<OtherPermitsComponent>;
  let page: Page;
  let store: PermitApplicationStore;
  const tasksService = mockClass(TasksService);

  class Page extends BasePage<OtherPermitsComponent> {
    get existRadios() {
      return this.queryAll<HTMLInputElement>('input[name$="exist"]');
    }

    get permitTypes() {
      return this.queryAll<HTMLInputElement>('input[name$="type"]');
    }

    get permitTypeValues() {
      return this.permitTypes.map((input) => input.value);
    }

    set permitTypeValues(values: string[]) {
      this.permitTypes.forEach((input, index) => this.setInputValue(`#${input.id}`, values[index]));
    }

    get numbers() {
      return this.queryAll<HTMLInputElement>('input[name$="num"]');
    }

    get numberValues() {
      return this.numbers.map((input) => input.value);
    }

    set numberValues(values: string[]) {
      this.numbers.forEach((input, index) => this.setInputValue(`#${input.id}`, values[index]));
    }

    get issuingAuthorities() {
      return this.queryAll<HTMLInputElement>('input[name$="issuingAuthority"]');
    }

    get issuingAuthoritiesValues() {
      return this.issuingAuthorities.map((input) => input.value);
    }

    set issuingAuthoritiesValues(values: string[]) {
      this.issuingAuthorities.forEach((input, index) => this.setInputValue(`#${input.id}`, values[index]));
    }

    get permitHolders() {
      return this.queryAll<HTMLInputElement>('input[name$="permitHolder"]');
    }

    get permitHoldersValues() {
      return this.permitHolders.map((input) => input.value);
    }

    set permitHoldersValues(values: string[]) {
      this.permitHolders.forEach((input, index) => this.setInputValue(`#${input.id}`, values[index]));
    }

    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get errorsList() {
      return this.queryAll<HTMLLIElement>('.govuk-error-summary__list > li');
    }

    get errors() {
      return Array.from(this.errorSummary.querySelectorAll('a'));
    }

    get addAnotherButton() {
      const secondaryButtons = this.queryAll<HTMLButtonElement>('button[type="button"]');

      return secondaryButtons[secondaryButtons.length - 1];
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  const createComponent = (state?: EnvironmentalPermitsAndLicences) => {
    store = TestBed.inject(PermitApplicationStore);
    store.setState(
      mockStateBuild({
        ...mockPermitApplyPayload.permit,
        environmentalPermitsAndLicences: state ?? mockPermitApplyPayload.permit.environmentalPermitsAndLicences,
      }),
    );

    fixture = TestBed.createComponent(OtherPermitsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PermitApplicationModule, RouterTestingModule],
      providers: [{ provide: TasksService, useValue: tasksService }],
    }).compileComponents();
  });

  beforeEach(() => createComponent());

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display an non empty form', () => {
    expect(page.existRadios.some((radio) => radio.checked)).toBeTruthy();
    expect(page.permitTypes).toHaveLength(2);
  });

  it('should display one other permit if yes is selected', () => {
    createComponent({ envPermitOrLicences: undefined, exist: false });
    expect(page.permitHolders).toHaveLength(1);
  });

  it('should clear permits if no is submitted', () => {
    tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));

    page.existRadios[1].click();
    fixture.detectChanges();
    page.submitButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
      mockPostBuild(
        {
          ...mockPermitApplyPayload.permit,
          environmentalPermitsAndLicences: { envPermitOrLicences: undefined, exist: false },
        },
        {
          ...mockPermitApplyPayload.permitSectionsCompleted,
          environmentalPermitsAndLicences: [true],
        },
      ),
    );
  });

  it('should require at least one field to be populated', () => {
    page.permitTypeValues = ['', page.permitTypeValues[0]];
    page.numberValues = ['', page.numberValues[0]];
    page.permitHoldersValues = ['', page.permitHoldersValues[0]];
    page.issuingAuthoritiesValues = ['', page.issuingAuthoritiesValues[0]];
    fixture.detectChanges();

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorsList).toBeTruthy();
    expect(page.errorsList.map((error) => error.textContent.trim())).toEqual([
      'Enter the type or the number or the issuing authority or the permit holder of the permit or licence',
    ]);
  });

  it('should display the existing data', () => {
    expect(page.existRadios[0].checked).toBeTruthy();
    expect(page.permitTypeValues).toEqual(['Environmental Protection Regulations', 'Seaside Regulations']);
    expect(page.numberValues).toEqual(['1234', '1253']);
    expect(page.issuingAuthoritiesValues).toEqual(['FWW', 'Peacegreen']);
    expect(page.permitHoldersValues).toEqual(['GovUK', 'GovUK']);
  });

  it('should add another permit', () => {
    page.addAnotherButton.click();
    fixture.detectChanges();

    expect(page.permitTypes).toHaveLength(3);
    expect(page.numbers).toHaveLength(3);
    expect(page.issuingAuthoritiesValues).toHaveLength(3);
    expect(page.permitHolders).toHaveLength(3);

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeTruthy();
  });
});
