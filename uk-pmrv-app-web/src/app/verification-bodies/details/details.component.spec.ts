import { HttpErrorResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { throwError } from 'rxjs';

import { KeycloakService } from 'keycloak-angular';

import { VerificationBodiesService, VerificationBodyUpdateDTO } from 'pmrv-api';

import { ActivatedRouteStub, asyncData, BasePage, CountryServiceStub, mockClass } from '../../../testing';
import { CountryService } from '../../core/services/country.service';
import { expectConcurrentErrorToBe } from '../../error/testing/concurrency-error';
import { SharedModule } from '../../shared/shared.module';
import { SharedUserModule } from '../../shared-user/shared-user.module';
import { ContactsComponent } from '../contacts/contacts.component';
import { saveNotFoundVerificationBodyError } from '../errors/concurrency-error';
import { FormComponent } from '../form/form.component';
import { VerificationBodyTypePipe } from '../form/verification-body-type.pipe';
import { validVerificationBodyUpdate } from '../testing/mock-data';
import { DetailsComponent } from './details.component';

describe('DetailsComponent', () => {
  let component: DetailsComponent;
  let fixture: ComponentFixture<DetailsComponent>;
  let verificationBodiesService: jest.Mocked<VerificationBodiesService>;
  let page: Page;
  const activatedRouteStub = new ActivatedRouteStub({ verificationBodyId: '123' }, undefined, {
    verificationBody: validVerificationBodyUpdate,
  });

  class Page extends BasePage<DetailsComponent> {
    get errorSummary(): HTMLDivElement {
      return this.query('.govuk-error-summary');
    }

    get submitButton(): HTMLButtonElement {
      return this.query('button[type="submit"]');
    }

    get name() {
      return this.getInputValue<string>('#details.name');
    }

    get accreditationReferenceNumber() {
      return this.getInputValue<string>('#details.accreditationRefNum');
    }

    get line1() {
      return this.getInputValue<string>('#details.address.line1');
    }

    get line2() {
      return this.getInputValue<string>('#details.address.line2');
    }

    get city() {
      return this.getInputValue<string>('#details.address.city');
    }

    get country() {
      return this.getInputValue<string>('#details.address.country');
    }

    get postcode() {
      return this.getInputValue<string>('#details.address.postcode');
    }

    get UK_ETS_AVIATION_checked(): boolean {
      return this.query<HTMLInputElement>('#types-2').checked;
    }

    get UK_ETS_INSTALLATIONS_checked(): boolean {
      return this.query<HTMLInputElement>('#types-0').checked;
    }

    set validForm(verificationBody: VerificationBodyUpdateDTO) {
      this.setInputValue('#details.name', verificationBody.name);
      this.setInputValue('#details.accreditationRefNum', verificationBody.accreditationReferenceNumber);
      this.setInputValue('#details.address.line1', verificationBody.address.line1);
      this.setInputValue('#details.address.line2', verificationBody.address.line2);
      this.setInputValue('#details.address.city', verificationBody.address.city);
      this.setInputValue('#details.address.country', verificationBody.address.country);
      this.setInputValue('#details.address.postcode', verificationBody.address.postcode);
    }
  }

  beforeEach(async () => {
    verificationBodiesService = mockClass(VerificationBodiesService);

    await TestBed.configureTestingModule({
      declarations: [DetailsComponent, VerificationBodyTypePipe, FormComponent, ContactsComponent],
      imports: [SharedModule, RouterTestingModule, SharedUserModule],
      providers: [
        { provide: VerificationBodiesService, useValue: verificationBodiesService },
        { provide: CountryService, useClass: CountryServiceStub },
        { provide: ActivatedRoute, useValue: activatedRouteStub },
        { provide: KeycloakService, useValue: mockClass(KeycloakService) },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DetailsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should fill the form with the existing verification body information', () => {
    expect(page.name).toEqual(validVerificationBodyUpdate.name);
    expect(page.accreditationReferenceNumber).toEqual(validVerificationBodyUpdate.accreditationReferenceNumber);
    expect(page.line1).toEqual(validVerificationBodyUpdate.address.line1);
    expect(page.line2).toEqual(validVerificationBodyUpdate.address.line2 ?? '');
    expect(page.city).toEqual(validVerificationBodyUpdate.address.city);
    expect(page.postcode).toEqual(validVerificationBodyUpdate.address.postcode);
    expect(page.country).toContain(validVerificationBodyUpdate.address.country);
    expect(page.UK_ETS_AVIATION_checked).toBeFalsy();
    expect(page.UK_ETS_INSTALLATIONS_checked).toBeTruthy();
  });

  it('should submit a valid form and redirect back to the list', () => {
    const navigateSpy = jest.spyOn(TestBed.inject(Router), 'navigate');
    verificationBodiesService.updateVerificationBodyUsingPUT.mockReturnValue(asyncData(null));

    page.validForm = validVerificationBodyUpdate;

    page.submitButton.click();
    fixture.detectChanges();

    expect(verificationBodiesService.updateVerificationBodyUsingPUT).toHaveBeenCalledWith(validVerificationBodyUpdate);
    expect(navigateSpy).toHaveBeenCalledTimes(1);
    expect(navigateSpy).toHaveBeenCalledWith(['..'], { relativeTo: activatedRouteStub });
  });

  it('should handle already existing accreditation reference number', () => {
    verificationBodiesService.updateVerificationBodyUsingPUT.mockReturnValueOnce(
      throwError(() => new HttpErrorResponse({ status: 400, error: { code: 'VERBODY1001' } })),
    );

    page.validForm = validVerificationBodyUpdate;

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeTruthy();
    expect(page.errorSummary.textContent).toContain('Enter a unique Accreditation reference number');
  });

  it('should display the error page if the verification body no longer exists on submission', async () => {
    verificationBodiesService.updateVerificationBodyUsingPUT.mockReturnValue(
      throwError(() => new HttpErrorResponse({ status: 404 })),
    );

    page.submitButton.click();
    fixture.detectChanges();

    await expectConcurrentErrorToBe(saveNotFoundVerificationBodyError);
  });
});
