import { HttpErrorResponse } from '@angular/common/http';
import { ComponentFixture, fakeAsync, TestBed, tick } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { throwError } from 'rxjs';

import { GovukComponentsModule } from 'govuk-components';

import { CaExternalContactDTO, CaExternalContactsService } from 'pmrv-api';

import {
  ActivatedRouteStub,
  asyncData,
  BasePage,
  expectToHaveNavigatedTo,
  RouterStubComponent,
} from '../../../../testing';
import { ConcurrencyTestingModule, expectConcurrentErrorToBe } from '../../../error/testing/concurrency-error';
import { saveNotFoundExternalContactError } from '../../errors/concurrency-error';
import { DeleteComponent } from './delete.component';

describe('DeleteComponent', () => {
  let component: DeleteComponent;
  let fixture: ComponentFixture<DeleteComponent>;
  let page: Page;
  let externalContactsService: Partial<jest.Mocked<CaExternalContactsService>>;

  const contact: CaExternalContactDTO = {
    id: 1,
    name: 'Bob Squarepants',
    email: 'bob_squarepants@test.gr',
    description: 'Bikini bottom contact',
    lastUpdatedDate: new Date('2021-01-08').toISOString(),
  };

  class Page extends BasePage<DeleteComponent> {
    get confirmButton() {
      return this.queryAll<HTMLButtonElement>('button')[0];
    }

    get cancelLink() {
      return this.queryAll<HTMLAnchorElement>('a').find((element) => element.textContent.trim() === 'Cancel');
    }

    get panelTitle() {
      return this.query<HTMLDivElement>('.govuk-panel__title');
    }

    get panelLink() {
      return this.query<HTMLAnchorElement>('a');
    }
  }

  beforeEach(async () => {
    externalContactsService = { deleteCaExternalContactByIdUsingDELETE: jest.fn().mockReturnValue(asyncData(null)) };
    const activatedRoute = new ActivatedRouteStub(null, null, { contact });

    await TestBed.configureTestingModule({
      imports: [
        GovukComponentsModule,
        ConcurrencyTestingModule,
        RouterTestingModule.withRoutes([
          { path: 'user', children: [{ path: 'regulators', component: RouterStubComponent }] },
        ]),
      ],
      declarations: [DeleteComponent, RouterStubComponent],
      providers: [
        { provide: CaExternalContactsService, useValue: externalContactsService },
        { provide: ActivatedRoute, useValue: activatedRoute },
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

  it('should display the confirmation dialog', () => {
    expect(page.cancelLink).toBeTruthy();
    expect(page.confirmButton).toBeTruthy();
    expect(page.confirmButton.disabled).toBeFalsy();
  });

  it('should cancel the deletion', () => {
    page.cancelLink.click();

    expectToHaveNavigatedTo('/user/regulators#external-contacts');
  });

  it('should delete the contact', fakeAsync(() => {
    page.confirmButton.click();
    fixture.detectChanges();

    expect(externalContactsService.deleteCaExternalContactByIdUsingDELETE).toHaveBeenCalledWith(contact.id);

    tick();
    fixture.detectChanges();

    expect(page.panelTitle.textContent).toEqual('The external contact Bob Squarepants has been deleted');
    expect(page.panelLink.textContent.trim()).toEqual('Return to the Regulator users and contacts page');
    expect(page.panelLink.href).toContain('/user/regulators#external-contacts');
  }));

  it('should dismiss with a message if error', async () => {
    externalContactsService.deleteCaExternalContactByIdUsingDELETE.mockReturnValue(
      throwError(() => new HttpErrorResponse({ error: { code: 'EXTCONTACT1000' }, status: 400 })),
    );

    page.confirmButton.click();

    await expectConcurrentErrorToBe(saveNotFoundExternalContactError);
  });
});
