import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, asyncData, BasePage, MockType } from '@testing';

import { AccountUpdateService, AddressDTO } from 'pmrv-api';

import { mockedAccount } from '../../testing/mock-data';
import { OperatorAddressComponent } from './operator-address.component';

describe('OperatorAddressComponent', () => {
  let component: OperatorAddressComponent;
  let fixture: ComponentFixture<OperatorAddressComponent>;

  let accountUpdateService: MockType<AccountUpdateService>;
  let page: Page;
  let route: ActivatedRouteStub;

  class Page extends BasePage<OperatorAddressComponent> {
    set line2Value(value: string) {
      this.setInputValue('#address.line2', value);
    }

    get line2Value() {
      return this.getInputValue('#address.line2');
    }

    get confirmButton() {
      return this.query<HTMLButtonElement>('.govuk-button');
    }
  }

  beforeEach(async () => {
    route = new ActivatedRouteStub(undefined, undefined, {
      account: mockedAccount,
    });

    accountUpdateService = {
      updateLegalEntityAddressUsingPOST: jest.fn().mockReturnValue(asyncData(null)),
    };

    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule],
      declarations: [OperatorAddressComponent],
      providers: [
        { provide: AccountUpdateService, useValue: accountUpdateService },
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(OperatorAddressComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should change operator address on form submit', () => {
    page.line2Value = 'address line 2';
    fixture.detectChanges();

    page.confirmButton.click();
    fixture.detectChanges();

    const expectedRequestPayload: AddressDTO = {
      ...mockedAccount.legalEntityAddress,
      line2: page.line2Value,
    };
    expect(accountUpdateService.updateLegalEntityAddressUsingPOST).toHaveBeenCalled();
    expect(accountUpdateService.updateLegalEntityAddressUsingPOST).toHaveBeenCalledWith(1, expectedRequestPayload);
  });
});
