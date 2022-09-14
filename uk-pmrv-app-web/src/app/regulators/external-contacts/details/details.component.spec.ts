import { HttpErrorResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of, throwError } from 'rxjs';

import { GovukComponentsModule } from 'govuk-components';

import { CaExternalContactDTO, CaExternalContactsService } from 'pmrv-api';

import { ActivatedRouteStub, changeInputValue } from '../../../../testing';
import { ConcurrencyTestingModule, expectConcurrentErrorToBe } from '../../../error/testing/concurrency-error';
import { SharedModule } from '../../../shared/shared.module';
import { SharedUserModule } from '../../../shared-user/shared-user.module';
import { saveNotFoundExternalContactError } from '../../errors/concurrency-error';
import { DetailsComponent } from './details.component';

describe('DetailsComponent', () => {
  let component: DetailsComponent;
  let fixture: ComponentFixture<DetailsComponent>;
  let router: Router;
  let route: ActivatedRouteStub;

  const mockExternalContact: CaExternalContactDTO = {
    id: 3,
    description: 'Description',
    email: 'external@contact.com',
    name: 'External Contact',
  };

  const caExternalContactsService: Partial<jest.Mocked<CaExternalContactsService>> = {
    createCaExternalContactUsingPOST: jest.fn(),
    editCaExternalContactUsingPATCH: jest.fn().mockReturnValue(of(null)),
  };
  const submitButton = () => fixture.nativeElement.querySelector('button[type="submit"]');
  const errorSummary = () => fixture.nativeElement.querySelector('govuk-error-summary');

  beforeEach(async () => {
    route = new ActivatedRouteStub();
    await TestBed.configureTestingModule({
      declarations: [DetailsComponent],
      imports: [
        ReactiveFormsModule,
        GovukComponentsModule,
        SharedModule,
        RouterTestingModule,
        SharedUserModule,
        ConcurrencyTestingModule,
      ],
      providers: [
        { provide: CaExternalContactsService, useValue: caExternalContactsService },
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    router = TestBed.inject(Router);
  });

  afterEach(() => jest.clearAllMocks());

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render add contact title and submit button if route contains no data', () => {
    expect(fixture.nativeElement.querySelector('h1').textContent.trim()).toEqual('Add an external contact');
    expect(submitButton().textContent.trim()).toEqual('Submit');
  });

  it('should show correct backend errors', () => {
    caExternalContactsService.createCaExternalContactUsingPOST.mockReturnValue(
      throwError(() => new HttpErrorResponse({ error: { code: 'EXTCONTACT1001' }, status: 400 })),
    );

    changeInputValue(fixture, '#name', 'Test name');
    changeInputValue(fixture, '#email', 'test@test.com');
    changeInputValue(fixture, '#description', 'Test');
    fixture.detectChanges();

    submitButton().click();
    fixture.detectChanges();

    expect(component.form.valid).toBeFalsy();
    expect(component.form.get('name').getError('uniqueName')).toEqual('Enter a unique displayed name');

    expect(errorSummary()).toBeTruthy();
    expect(errorSummary().textContent).toContain('Enter a unique displayed name');

    changeInputValue(fixture, '#name', 'Test name');
    fixture.detectChanges();

    caExternalContactsService.createCaExternalContactUsingPOST.mockReturnValue(
      throwError(() => new HttpErrorResponse({ error: { code: 'EXTCONTACT1002' }, status: 400 })),
    );

    submitButton().click();
    fixture.detectChanges();

    expect(component.form.valid).toBeFalsy();
    expect(component.form.get('email').getError('uniqueEmail')).toEqual('Email address already exists');

    expect(errorSummary()).toBeTruthy();
    expect(errorSummary().textContent).toContain('Email address already exists');

    changeInputValue(fixture, '#email', 'test@email.test');
    fixture.detectChanges();

    caExternalContactsService.createCaExternalContactUsingPOST.mockReturnValue(
      throwError(() => new HttpErrorResponse({ error: { code: 'EXTCONTACT1003' }, status: 400 })),
    );

    submitButton().click();
    fixture.detectChanges();

    expect(component.form.valid).toBeFalsy();
    expect(component.form.get('name').getError('uniqueName')).toEqual('Enter a unique displayed name');
    expect(component.form.get('email').getError('uniqueEmail')).toEqual('Email address already exists');

    expect(errorSummary()).toBeTruthy();
    expect(errorSummary().textContent).toContain('Email address already exists');
    expect(errorSummary().textContent).toContain('Enter a unique displayed name');
  });

  it('should navigate to external contacts on correct form submission', () => {
    caExternalContactsService.createCaExternalContactUsingPOST.mockReturnValue(of(null));
    const navigateSpy = jest.spyOn(router, 'navigate');

    changeInputValue(fixture, '#name', 'Test name');
    changeInputValue(fixture, '#email', 'test@test.com');
    changeInputValue(fixture, '#description', 'Test');
    fixture.detectChanges();

    submitButton().click();

    expect(caExternalContactsService.createCaExternalContactUsingPOST).toHaveBeenCalledTimes(1);
    expect(caExternalContactsService.createCaExternalContactUsingPOST).toHaveBeenCalledWith({
      name: 'Test name',
      email: 'test@test.com',
      description: 'Test',
    });

    expect(navigateSpy).toHaveBeenCalledTimes(1);
    expect(navigateSpy).toHaveBeenCalledWith(['../..'], { relativeTo: route, fragment: 'external-contacts' });
  });

  it('should not post form if invalid', () => {
    caExternalContactsService.createCaExternalContactUsingPOST.mockReturnValue(of(null));
    const navigateSpy = jest.spyOn(router, 'navigate');
    const postExternalContactSpy = jest.spyOn(caExternalContactsService, 'createCaExternalContactUsingPOST');

    changeInputValue(fixture, '#name', 'Test name');
    changeInputValue(fixture, '#email', 'test');
    changeInputValue(fixture, '#description', 'Test');
    fixture.detectChanges();

    submitButton().click();
    fixture.detectChanges();

    expect(postExternalContactSpy).not.toHaveBeenCalled();
    expect(fixture.nativeElement.querySelector('govuk-error-summary')).toBeTruthy();
    expect(fixture.nativeElement.querySelector('govuk-error-summary').textContent).toContain(
      'Enter an email address in the correct format, like name@example.com',
    );

    changeInputValue(fixture, '#email', 'test@test.com');
    fixture.detectChanges();

    submitButton().click();

    expect(postExternalContactSpy).toHaveBeenCalledWith({
      name: 'Test name',
      email: 'test@test.com',
      description: 'Test',
    });

    expect(navigateSpy).toHaveBeenCalledTimes(1);
    expect(navigateSpy).toHaveBeenCalledWith(['../..'], { relativeTo: route, fragment: 'external-contacts' });
  });

  describe('Edit external contact', () => {
    beforeEach(() => {
      route.setParamMap({ userId: 1 });
      route.setResolveMap({ contact: mockExternalContact });
      fixture.detectChanges();
    });

    it('should change title and button if route contains data', () => {
      expect(fixture.nativeElement.querySelector('h1').textContent.trim()).toEqual('External contact details');
      expect(submitButton().textContent.trim()).toEqual('Save');
    });

    it('should patch changed contact', () => {
      changeInputValue(fixture, '#name', 'New name');
      fixture.detectChanges();

      submitButton().click();
      fixture.detectChanges();

      expect(caExternalContactsService.editCaExternalContactUsingPATCH).toHaveBeenCalledTimes(1);
      expect(caExternalContactsService.editCaExternalContactUsingPATCH).toHaveBeenCalledWith(1, {
        description: 'Description',
        email: 'external@contact.com',
        name: 'New name',
      });
    });

    it('should redirect with error if contact deleted', async () => {
      caExternalContactsService.editCaExternalContactUsingPATCH.mockReturnValue(
        throwError(() => new HttpErrorResponse({ error: { code: 'EXTCONTACT1000' }, status: 400 })),
      );

      changeInputValue(fixture, '#name', 'New name');
      fixture.detectChanges();

      submitButton().click();
      fixture.detectChanges();

      await expectConcurrentErrorToBe(saveNotFoundExternalContactError);
    });
  });
});
