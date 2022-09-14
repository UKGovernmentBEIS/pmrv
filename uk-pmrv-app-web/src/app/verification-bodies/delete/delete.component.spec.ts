import { HttpErrorResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of, throwError } from 'rxjs';

import { GovukComponentsModule } from 'govuk-components';

import { VerificationBodiesService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, expectToHaveNavigatedTo, MockType, RouterStubComponent } from '../../../testing';
import { ConcurrencyTestingModule, expectConcurrentErrorToBe } from '../../error/testing/concurrency-error';
import { saveNotFoundVerificationBodyError } from '../errors/concurrency-error';
import { DeleteComponent } from './delete.component';

describe('DeleteComponent', () => {
  let component: DeleteComponent;
  let fixture: ComponentFixture<DeleteComponent>;
  const route = new ActivatedRouteStub({ verificationBodyId: 1 });
  let page: Page;

  class Page extends BasePage<DeleteComponent> {
    get confirmButton() {
      return this.query<HTMLButtonElement>('button');
    }

    get cancelButton() {
      return this.query<HTMLAnchorElement>('a');
    }
  }

  const verificationBodiesService: MockType<VerificationBodiesService> = {
    deleteVerificationBodyByIdUsingDELETE: jest.fn().mockReturnValue(of(null)),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [DeleteComponent, RouterStubComponent],
      imports: [
        RouterTestingModule.withRoutes([{ path: 'verification-bodies', component: RouterStubComponent }]),
        GovukComponentsModule,
        ConcurrencyTestingModule,
      ],

      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: VerificationBodiesService, useValue: verificationBodiesService },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DeleteComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should contain cancel and delete buttons', () => {
    expect(page.confirmButton.textContent.trim()).toEqual('Confirm delete');
    expect(page.cancelButton.textContent.trim()).toEqual('Cancel');
  });

  it('should delete verification body on Confirm delete button click', () => {
    page.confirmButton.click();
    fixture.detectChanges();

    expect(verificationBodiesService.deleteVerificationBodyByIdUsingDELETE).toHaveBeenCalledWith(1);
  });

  it('should navigate to verification body list on Cancel button click', () => {
    page.cancelButton.click();
    fixture.detectChanges();

    expectToHaveNavigatedTo('/verification-bodies');
  });

  it('should show the concurrency error page if the body has already been deleted', async () => {
    verificationBodiesService.deleteVerificationBodyByIdUsingDELETE.mockReturnValue(
      throwError(() => new HttpErrorResponse({ status: 404 })),
    );

    page.confirmButton.click();
    fixture.detectChanges();

    await expectConcurrentErrorToBe(saveNotFoundVerificationBodyError);
  });
});
