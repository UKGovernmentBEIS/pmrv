import { HttpErrorResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { of, throwError } from 'rxjs';

import { VerificationBodiesService, VerificationBodyCreationDTO } from 'pmrv-api';

import { BasePage, CountryServiceStub } from '../../../testing';
import { CountryService } from '../../core/services/country.service';
import { SharedModule } from '../../shared/shared.module';
import { FormComponent } from '../form/form.component';
import { VerificationBodyTypePipe } from '../form/verification-body-type.pipe';
import { validVerificationBodyCreation } from '../testing/mock-data';
import { AddComponent } from './add.component';

describe('AddComponent', () => {
  let component: AddComponent;
  let fixture: ComponentFixture<AddComponent>;
  let verificationBodiesService: VerificationBodiesService;
  let page: Page;

  class Page extends BasePage<AddComponent> {
    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }

    get panelTitle() {
      return this.query<HTMLHeadingElement>('h1.govuk-panel__title');
    }

    set validForm(verificationBody: VerificationBodyCreationDTO) {
      this.setInputValue('#details.name', verificationBody.name);
      this.setInputValue('#details.accreditationRefNum', verificationBody.accreditationReferenceNumber);
      this.setInputValue('#details.address.line1', verificationBody.address.line1);
      this.setInputValue('#details.address.line2', verificationBody.address.line2);
      this.setInputValue('#details.address.city', verificationBody.address.city);
      this.setInputValue('#details.address.country', verificationBody.address.country);
      this.setInputValue('#details.address.postcode', verificationBody.address.postcode);
      this.query<HTMLInputElement>('#types-0').click();
      this.setInputValue(
        '#adminVerifierUserInvitation.firstName',
        verificationBody.adminVerifierUserInvitation.firstName,
      );
      this.setInputValue(
        '#adminVerifierUserInvitation.lastName',
        verificationBody.adminVerifierUserInvitation.lastName,
      );
      this.setInputValue(
        '#adminVerifierUserInvitation.phoneNumber',
        verificationBody.adminVerifierUserInvitation.phoneNumber,
      );
      this.setInputValue('#adminVerifierUserInvitation.email', verificationBody.adminVerifierUserInvitation.email);
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AddComponent, VerificationBodyTypePipe, FormComponent],
      imports: [SharedModule, RouterTestingModule],
      providers: [{ provide: CountryService, useClass: CountryServiceStub }],
    }).compileComponents();
    verificationBodiesService = TestBed.inject(VerificationBodiesService);
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AddComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should submit a valid form and show confirmation', () => {
    const postSpy = jest
      .spyOn(verificationBodiesService, 'createVerificationBodyUsingPOST')
      .mockReturnValueOnce(of(null));

    page.validForm = validVerificationBodyCreation;

    page.submitButton.click();
    fixture.detectChanges();
    expect(postSpy).toHaveBeenCalledTimes(1);
    expect(postSpy).toHaveBeenCalledWith(validVerificationBodyCreation);

    expect(page.panelTitle.textContent).toEqual('Verification body Body test name has been added');
  });

  it('should handle already existing user', () => {
    jest
      .spyOn(verificationBodiesService, 'createVerificationBodyUsingPOST')
      .mockReturnValueOnce(throwError(() => new HttpErrorResponse({ status: 400, error: { code: 'USER1001' } })));

    page.validForm = validVerificationBodyCreation;

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeTruthy();
    expect(page.errorSummary.textContent).toContain('This user email already exists in PMRV');
  });

  it('should handle already existing accreditation reference number', () => {
    jest
      .spyOn(verificationBodiesService, 'createVerificationBodyUsingPOST')
      .mockReturnValueOnce(throwError(() => new HttpErrorResponse({ status: 400, error: { code: 'VERBODY1001' } })));

    page.validForm = validVerificationBodyCreation;

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeTruthy();
    expect(page.errorSummary.textContent).toContain('Enter a unique Accreditation reference number');
  });
});
