import { HttpErrorResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of, throwError } from 'rxjs';

import { GovukComponentsModule } from 'govuk-components';

import { VerificationBodiesService } from 'pmrv-api';

import { ActivatedRouteStub, asyncData, BasePage, MockType } from '../../testing';
import { ConcurrencyTestingModule, expectConcurrentErrorToBe } from '../error/testing/concurrency-error';
import { SharedModule } from '../shared/shared.module';
import { savePartiallyNotFoundVerificationBodyError } from './errors/concurrency-error';
import { mockVerificationBodies } from './testing/mock-data';
import { VerificationBodiesComponent } from './verification-bodies.component';

describe('VerificationBodiesComponent', () => {
  let component: VerificationBodiesComponent;
  let fixture: ComponentFixture<VerificationBodiesComponent>;
  let page: Page;
  let router: Router;

  const activatedRouteStub = new ActivatedRouteStub(null, null, { verificationBodies: mockVerificationBodies });

  let verificationBodiesService: MockType<VerificationBodiesService>;

  class Page extends BasePage<VerificationBodiesComponent> {
    get verificationBodiesForm() {
      return this.query<HTMLFormElement>('form[id="verification-bodies-form"]');
    }

    get addVerificationBodyBtn() {
      return this.query<HTMLButtonElement>('#add-verification-body');
    }

    get submitVerificationBodiesButton() {
      return this.verificationBodiesForm.querySelector<HTMLButtonElement>('button[type="submit"]');
    }

    get rows() {
      return Array.from(this.verificationBodiesForm.querySelectorAll<HTMLTableRowElement>('tbody tr'));
    }

    get verificationBodyValues() {
      return this.queryAll<HTMLSelectElement>('select[id^="verificationBodies."][id$=".status"]').map((element) =>
        this.getInputValue(`#${element.id}`),
      );
    }

    set verificationBodyValues(values: string[]) {
      values.forEach((value, index) => this.setInputValue(`#verificationBodies.${index}.status`, value));
    }

    get deleteLinks() {
      return this.rows.map((row) => row.querySelectorAll('a')[1]);
    }
  }

  beforeEach(async () => {
    verificationBodiesService = {
      getVerificationBodiesUsingGET: jest.fn().mockReturnValue(asyncData(mockVerificationBodies)),
      updateVerificationBodiesStatusUsingPATCH: jest.fn().mockReturnValue(of(null)),
    };

    await TestBed.configureTestingModule({
      imports: [
        ReactiveFormsModule,
        GovukComponentsModule,
        SharedModule,
        RouterTestingModule,
        ConcurrencyTestingModule,
      ],
      declarations: [VerificationBodiesComponent],
      providers: [
        VerificationBodiesComponent,
        { provide: ActivatedRoute, useValue: activatedRouteStub },
        { provide: VerificationBodiesService, useValue: verificationBodiesService },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(VerificationBodiesComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    router = TestBed.inject(Router);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('for admin operators', () => {
    beforeEach(() => {
      activatedRouteStub.setResolveMap({ verificationBodies: { ...mockVerificationBodies, editable: true } });
      fixture.detectChanges();
    });

    it('should display the add new verification body button', () => {
      expect(page.addVerificationBodyBtn).toBeTruthy();
    });

    it('should display an editable list of verification bodies', () => {
      expect(page.rows.length).toEqual(3);

      mockVerificationBodies.verificationBodies.map((verificationBody, index) => {
        expect(page.rows[index].querySelectorAll('td a')[0].textContent.trim()).toEqual(verificationBody.name);
        expect(page.rows[index].querySelectorAll('td a')[1].textContent.trim()).toEqual('Delete');
      });

      expect(page.verificationBodyValues).toEqual(['1: DISABLED', '0: ACTIVE']);
      expect(page.rows[2].querySelectorAll('td')[1].textContent.trim()).toEqual('Awaiting confirmation');
    });

    it('should display the submission buttons', () => {
      expect(page.submitVerificationBodiesButton).toBeTruthy();
    });

    it('should update the verification body status only with the updated values on save', () => {
      page.verificationBodyValues = ['ACTIVE'];
      fixture.detectChanges();

      page.submitVerificationBodiesButton.click();
      fixture.detectChanges();

      expect(verificationBodiesService.updateVerificationBodiesStatusUsingPATCH).toHaveBeenCalledWith([
        { id: 4, status: 'ACTIVE' },
      ]);
    });

    it('should navigate to delete confirmation page on delete click', () => {
      const navigateSpy = jest.spyOn(router, 'navigateByUrl').mockImplementation();

      page.deleteLinks[0].click();
      fixture.detectChanges();

      expect(navigateSpy).toHaveBeenCalled();
    });

    it('should display error page on concurrency error of status update', async () => {
      verificationBodiesService.updateVerificationBodiesStatusUsingPATCH.mockReturnValue(
        throwError(() => new HttpErrorResponse({ status: 400, error: { code: 'VERBODY1002' } })),
      );

      page.verificationBodyValues = ['ACTIVE'];
      page.submitVerificationBodiesButton.click();

      await expectConcurrentErrorToBe(savePartiallyNotFoundVerificationBodyError);
    });
  });

  describe('for non-admin operators', () => {
    beforeEach(() => {
      activatedRouteStub.setResolveMap({ verificationBodies: { ...mockVerificationBodies, editable: false } });
      fixture.detectChanges();
    });

    it('should not display the add new verification body button', () => {
      expect(page.addVerificationBodyBtn).toBeFalsy();
    });

    it('should not display the submission buttons', () => {
      expect(page.submitVerificationBodiesButton).toBeFalsy();
    });

    it('should display a readonly list of verification bodies', () => {
      expect(page.rows.length).toEqual(3);

      mockVerificationBodies.verificationBodies.map((verificationBody, index) => {
        expect(page.rows[index].querySelectorAll('td')[0].textContent.trim()).toEqual(verificationBody.name);
      });

      expect(page.rows[0].querySelectorAll('td')[1].textContent.trim()).toEqual('Disabled');
      expect(page.rows[1].querySelectorAll('td')[1].textContent.trim()).toEqual('Active');
      expect(page.rows[2].querySelectorAll('td')[1].textContent.trim()).toEqual('Awaiting confirmation');
    });
  });
});
