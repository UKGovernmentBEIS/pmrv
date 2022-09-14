import { Component, Inject } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormGroup } from '@angular/forms';
import { By } from '@angular/platform-browser';

import { VerificationBodyDTO } from 'pmrv-api';

import { BasePage, CountryServiceStub } from '../../../testing';
import { CountryService } from '../../core/services/country.service';
import { SharedModule } from '../../shared/shared.module';
import { verificationBody } from '../testing/mock-data';
import { FormComponent } from './form.component';
import { VERIFICATION_BODY_FORM, verificationBodyFormFactory } from './form.factory';
import { VerificationBodyTypePipe } from './verification-body-type.pipe';

describe('FormComponent', () => {
  let component: FormComponent;
  let testComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let page: Page;

  class Page extends BasePage<TestComponent> {
    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }

    set validForm(verificationBody: Omit<Required<VerificationBodyDTO>, 'id' | 'status'>) {
      this.setInputValue('#details.name', verificationBody.name);
      this.setInputValue('#details.accreditationRefNum', verificationBody.accreditationReferenceNumber);
      this.setInputValue('#details.address.line1', verificationBody.address.line1);
      this.setInputValue('#details.address.line2', verificationBody.address.line2);
      this.setInputValue('#details.address.city', verificationBody.address.city);
      this.setInputValue('#details.address.country', verificationBody.address.country);
      this.setInputValue('#details.address.postcode', verificationBody.address.postcode);
      this.query<HTMLInputElement>('#types-0').click();
    }

    set accrediationRefNum(value: string) {
      this.setInputValue('#details.accreditationRefNum', value);
    }

    set UK_ETS_AVIATION_checked(value: boolean) {
      this.check('#types-2', value);
    }

    set UK_ETS_INSTALLATIONS_checked(value: boolean) {
      this.check('#types-0', value);
    }

    private check(selector: string, value): void {
      const checkbox = this.query<HTMLInputElement>(selector);
      if ((value && !checkbox.checked) || (!value && checkbox.checked)) {
        checkbox.click();
      }
    }
  }

  @Component({
    template: `
      <form [formGroup]="form" (ngSubmit)="submit()">
        <app-form></app-form>
        <button type="submit">Submit</button>
      </form>
    `,
  })
  class TestComponent {
    submit = jest.fn();

    constructor(@Inject(VERIFICATION_BODY_FORM) readonly form: FormGroup) {}
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TestComponent, FormComponent, VerificationBodyTypePipe],
      imports: [SharedModule],
      providers: [verificationBodyFormFactory, { provide: CountryService, useClass: CountryServiceStub }],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    testComponent = fixture.componentInstance;
    component = fixture.debugElement.query(By.directive(FormComponent)).componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
    expect(testComponent).toBeTruthy();
  });

  it('should require at least one type checked', () => {
    page.validForm = verificationBody;
    page.submitButton.click();
    fixture.detectChanges();

    expect(component.formGroupDirective.form.valid).toBeTruthy();

    page.UK_ETS_INSTALLATIONS_checked = false;
    page.submitButton.click();
    fixture.detectChanges();

    expect(component.formGroupDirective.form.valid).toBeFalsy();

    page.UK_ETS_INSTALLATIONS_checked = true;
    page.submitButton.click();
    fixture.detectChanges();

    expect(component.formGroupDirective.form.valid).toBeTruthy();
  });

  it('should not accept an invalid accreditation reference number', () => {
    page.validForm = verificationBody;

    page.accrediationRefNum = 'The Ref num is larger Than 25 Characters';
    page.submitButton.click();
    fixture.detectChanges();

    expect(component.formGroupDirective.form.valid).toBeFalsy();

    page.accrediationRefNum = 'Accreditation ref num';
    page.submitButton.click();
    fixture.detectChanges();

    expect(component.formGroupDirective.form.valid).toBeTruthy();
  });
});
