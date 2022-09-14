import { HttpErrorResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { throwError } from 'rxjs';

import { GovukComponentsModule } from 'govuk-components';

import { AccountVerificationBodyService, VerificationBodyNameInfoDTO } from 'pmrv-api';

import { ActivatedRouteStub, asyncData, BasePage } from '../../../../../testing';
import { ConcurrencyTestingModule, expectConcurrentErrorToBe } from '../../../../error/testing/concurrency-error';
import { SharedModule } from '../../../../shared/shared.module';
import {
  appointedVerificationBodyError,
  saveNotFoundVerificationBodyError,
  savePartiallyNotFoundOperatorError,
} from '../../errors/concurrency-error';
import { ConfirmationComponent } from '../confirmation/confirmation.component';
import { AppointComponent } from './appoint.component';

describe('AppointComponent', () => {
  let component: AppointComponent;
  let fixture: ComponentFixture<AppointComponent>;

  const activeBodies: VerificationBodyNameInfoDTO[] = [
    { id: 1, name: 'Verifying company 1' },
    { id: 2, name: 'Verifying company 2' },
  ];

  const accountVerificationBodyService: Partial<jest.Mocked<AccountVerificationBodyService>> = {
    appointVerificationBodyToAccountUsingPOST: jest.fn(),
    replaceVerificationBodyToAccountUsingPATCH: jest.fn(),
    getActiveVerificationBodiesUsingGET: jest.fn().mockReturnValue(asyncData(activeBodies)),
  };

  let page: Page;
  const activatedRoute = new ActivatedRouteStub({ accountId: '1' });

  class Page extends BasePage<AppointComponent> {
    get options() {
      return this.queryAll<HTMLOptionElement>('option');
    }

    get verificationBodyValue() {
      return this.getInputValue('#verificationBodyId');
    }

    set verificationBodyValue(id: number) {
      this.setInputValue('#verificationBodyId', id);
    }

    get submit() {
      return this.query<HTMLButtonElement>('button');
    }

    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get errors() {
      return this.queryAll<HTMLAnchorElement>('.govuk-error-summary li a');
    }
  }

  const createComponent = async () => {
    fixture = TestBed.createComponent(AppointComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    await fixture.whenStable();
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        GovukComponentsModule,
        SharedModule,
        ReactiveFormsModule,
        RouterTestingModule,
        ConcurrencyTestingModule,
      ],
      declarations: [AppointComponent, ConfirmationComponent],
      providers: [
        { provide: AccountVerificationBodyService, useValue: accountVerificationBodyService },
        { provide: ActivatedRoute, useValue: activatedRoute },
      ],
    }).compileComponents();
  });

  describe('for appoint verification body', () => {
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should populate the form with active verification bodies', () => {
      expect(page.options.map((option) => option.textContent.trim())).toEqual([
        'Verifying company 1',
        'Verifying company 2',
      ]);
    });

    it('should display the errors when trying to submit without selecting', () => {
      page.submit.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errors.map((error) => error.textContent.trim())).toEqual(['Select a verification body']);
    });

    it('should appoint the body and navigate to confirmation page', async () => {
      accountVerificationBodyService.appointVerificationBodyToAccountUsingPOST.mockReturnValue(asyncData(null));

      page.verificationBodyValue = 1;
      fixture.detectChanges();

      page.submit.click();
      fixture.detectChanges();

      await fixture.whenStable();
      fixture.detectChanges();

      expect(accountVerificationBodyService.appointVerificationBodyToAccountUsingPOST).toHaveBeenCalledWith(1, {
        verificationBodyId: 1,
      });
      expect(fixture.debugElement.query(By.directive(ConfirmationComponent))).toBeTruthy();
    });

    it('should redirect to the already appointed page on a submission related error', async () => {
      accountVerificationBodyService.appointVerificationBodyToAccountUsingPOST.mockReturnValue(
        throwError(() => new HttpErrorResponse({ status: 400, error: { code: 'ACCOUNT1006' } })),
      );

      page.verificationBodyValue = 1;
      fixture.detectChanges();

      page.submit.click();
      fixture.detectChanges();

      await expectConcurrentErrorToBe(appointedVerificationBodyError(1));
    });

    it('should redirect to the missing page on a submission related error', async () => {
      accountVerificationBodyService.appointVerificationBodyToAccountUsingPOST.mockReturnValue(
        throwError(() => new HttpErrorResponse({ status: 404 })),
      );

      page.verificationBodyValue = 1;
      fixture.detectChanges();

      page.submit.click();
      fixture.detectChanges();

      await expectConcurrentErrorToBe(saveNotFoundVerificationBodyError(1));
    });
  });

  describe('for replace verification body', () => {
    beforeEach(() => {
      activatedRoute.setResolveMap({ verificationBody: { id: 1, name: 'test' } });
    });

    beforeEach(createComponent);

    it('should assign verification body if provided', () => {
      expect(page.verificationBodyValue).toEqual('0: 1');
    });

    it('should not allow replacing with the same verification body', () => {
      expect(page.errorSummary).toBeFalsy();

      const navigateSpy = jest.spyOn(TestBed.inject(Router), 'navigate');
      page.submit.click();
      fixture.detectChanges();

      expect(navigateSpy).not.toHaveBeenCalled();
      expect(page.errorSummary).toBeTruthy();
      expect(page.errors.map((error) => error.textContent.trim())).toEqual([
        'This verification body is already appointed. Please select another one.',
      ]);
    });

    it('should call to replace if provided with current notification body', async () => {
      accountVerificationBodyService.replaceVerificationBodyToAccountUsingPATCH.mockReturnValue(asyncData(null));

      page.verificationBodyValue = 2;
      fixture.detectChanges();

      page.submit.click();
      fixture.detectChanges();

      await fixture.whenStable();
      fixture.detectChanges();

      expect(accountVerificationBodyService.appointVerificationBodyToAccountUsingPOST).not.toHaveBeenCalled();
      expect(accountVerificationBodyService.replaceVerificationBodyToAccountUsingPATCH).toHaveBeenCalledTimes(1);
      expect(accountVerificationBodyService.replaceVerificationBodyToAccountUsingPATCH).toHaveBeenCalledWith(1, {
        verificationBodyId: 2,
      });
      expect(fixture.debugElement.query(By.directive(ConfirmationComponent))).toBeTruthy();
    });

    it('should redirect to error verification body was not found', async () => {
      accountVerificationBodyService.replaceVerificationBodyToAccountUsingPATCH.mockReturnValue(
        throwError(() => new HttpErrorResponse({ status: 404 })),
      );

      page.verificationBodyValue = 2;
      fixture.detectChanges();

      page.submit.click();
      fixture.detectChanges();

      await expectConcurrentErrorToBe(savePartiallyNotFoundOperatorError(1));
    });

    it('should redirect to error verification body is no longer appointed', async () => {
      accountVerificationBodyService.replaceVerificationBodyToAccountUsingPATCH.mockReturnValue(
        throwError(() => new HttpErrorResponse({ status: 400, error: { code: 'ACCOUNT1007' } })),
      );

      page.verificationBodyValue = 2;
      fixture.detectChanges();

      page.submit.click();
      fixture.detectChanges();

      await expectConcurrentErrorToBe(savePartiallyNotFoundOperatorError(1));
    });
  });
});
