import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { AccountUpdateService } from 'pmrv-api';

import { ActivatedRouteStub, asyncData, BasePage, MockType } from '../../../../testing';
import { SharedModule } from '../../../shared/shared.module';
import { mockedAccount } from '../../testing/mock-data';
import { RegistryIdComponent } from './registry-id.component';

describe('RegistryIdComponent', () => {
  let component: RegistryIdComponent;
  let fixture: ComponentFixture<RegistryIdComponent>;
  let accountUpdateService: MockType<AccountUpdateService>;
  let page: Page;
  let route: ActivatedRouteStub;

  class Page extends BasePage<RegistryIdComponent> {
    set registryIdValue(value: string) {
      this.setInputValue('#registryId', value);
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
      updateAccountRegistryIdUsingPOST: jest.fn().mockReturnValue(asyncData(null)),
    };

    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule],
      declarations: [RegistryIdComponent],
      providers: [
        { provide: AccountUpdateService, useValue: accountUpdateService },
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RegistryIdComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should change Registry Id on form submit', () => {
    page.registryIdValue = '1111111';
    fixture.detectChanges();

    page.confirmButton.click();
    fixture.detectChanges();

    expect(accountUpdateService.updateAccountRegistryIdUsingPOST).toHaveBeenCalled();
    expect(accountUpdateService.updateAccountRegistryIdUsingPOST).toHaveBeenCalledWith(1, { registryId: '1111111' });
  });
});
