import { HttpErrorResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { throwError } from 'rxjs';

import { AccountUpdateService } from 'pmrv-api';

import { ActivatedRouteStub, asyncData, BasePage, mockClass } from '../../../../testing';
import { SharedModule } from '../../../shared/shared.module';
import { mockedAccount } from '../../testing/mock-data';
import { OperatorNameComponent } from './operator-name.component';

describe('OperatorNameComponent', () => {
  let component: OperatorNameComponent;
  let fixture: ComponentFixture<OperatorNameComponent>;
  let accountUpdateService: jest.Mocked<AccountUpdateService>;
  let page: Page;
  let route: ActivatedRouteStub;

  class Page extends BasePage<OperatorNameComponent> {
    set nameValue(value: string) {
      this.setInputValue('#operatorName', value);
    }

    get confirmButton() {
      return this.query<HTMLButtonElement>('.govuk-button');
    }

    get errorSummary(): HTMLDivElement {
      return this.query('.govuk-error-summary');
    }
  }

  beforeEach(async () => {
    route = new ActivatedRouteStub(undefined, undefined, {
      account: mockedAccount,
    });

    accountUpdateService = mockClass(AccountUpdateService);

    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule],
      declarations: [OperatorNameComponent],
      providers: [
        { provide: AccountUpdateService, useValue: accountUpdateService },
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(OperatorNameComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should change operator name on form submit', () => {
    const navigateSpy = jest.spyOn(TestBed.inject(Router), 'navigate');
    accountUpdateService.updateLegalEntityNameUsingPOST.mockReturnValue(asyncData(null));
    page.nameValue = 'operator name';
    fixture.detectChanges();

    page.confirmButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeFalsy();
    expect(accountUpdateService.updateLegalEntityNameUsingPOST).toHaveBeenCalledWith(1, {
      legalEntityName: 'operator name',
    });
    expect(navigateSpy).toHaveBeenCalledTimes(1);
    expect(navigateSpy).toHaveBeenCalledWith(['../..'], { relativeTo: route });
  });

  it('should handle already existing operator name error', () => {
    accountUpdateService.updateLegalEntityNameUsingPOST.mockReturnValueOnce(
      throwError(() => new HttpErrorResponse({ status: 400, error: { code: 'ACCOUNT1002' } })),
    );

    page.nameValue = 'operator name';
    fixture.detectChanges();

    page.confirmButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeTruthy();
    expect(page.errorSummary.textContent).toContain('This operator name already exists. Enter a new operator name.');
  });
});
