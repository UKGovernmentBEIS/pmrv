import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { AccountUpdateService } from 'pmrv-api';

import { ActivatedRouteStub, asyncData, BasePage, MockType } from '../../../../testing';
import { SharedModule } from '../../../shared/shared.module';
import { mockedAccount } from '../../testing/mock-data';
import { SiteNameComponent } from './site-name.component';

describe('SiteNameComponent', () => {
  let component: SiteNameComponent;
  let fixture: ComponentFixture<SiteNameComponent>;
  let accountUpdateService: MockType<AccountUpdateService>;
  let page: Page;
  let route: ActivatedRouteStub;
  class Page extends BasePage<SiteNameComponent> {
    set siteNameValue(value: string) {
      this.setInputValue('#siteName', value);
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
      updateAccountSiteNameUsingPOST: jest.fn().mockReturnValue(asyncData(null)),
    };

    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule],
      declarations: [SiteNameComponent],
      providers: [
        { provide: AccountUpdateService, useValue: accountUpdateService },
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SiteNameComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should change site name on form submit', () => {
    page.siteNameValue = 'Instaccount 1';
    fixture.detectChanges();

    page.confirmButton.click();
    fixture.detectChanges();

    expect(accountUpdateService.updateAccountSiteNameUsingPOST).toHaveBeenCalled();
    expect(accountUpdateService.updateAccountSiteNameUsingPOST).toHaveBeenCalledWith(1, { siteName: 'Instaccount 1' });
  });
});
