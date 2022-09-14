import { ComponentFixture, TestBed } from '@angular/core/testing';

import { of } from 'rxjs';

import { AccountStatusPipe } from '@shared/pipes/account-status.pipe';
import { SharedModule } from '@shared/shared.module';

import { AccountViewService } from 'pmrv-api';

import { BasePage, MockType } from '../../../testing';
import { mockedAccount } from '../../accounts/testing/mock-data';
import { InstallationCategoryPipe } from '../../permit-application/shared/pipes/installation-category.pipe';
import { SharedState } from '../../shared/store/shared.state';
import { SharedStore } from '../../shared/store/shared.store';
import { TaskSharedModule } from '../../tasks/shared/task-shared-module';
import { IncorporateHeaderComponent } from './incorporate-header.component';

describe('IncorporateHeaderComponent', () => {
  let component: IncorporateHeaderComponent;
  let fixture: ComponentFixture<IncorporateHeaderComponent>;
  let store: SharedStore;
  let page: Page;

  let accountViewService: MockType<AccountViewService>;

  class Page extends BasePage<IncorporateHeaderComponent> {
    get candidateAssigneesSelectInputValue(): HTMLSelectElement {
      return this.query<HTMLSelectElement>('.govuk-phase-banner');
    }
  }

  const mockTaskState: SharedState = { accountId: 1 };

  function createComponent() {
    fixture = TestBed.createComponent(IncorporateHeaderComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  }

  beforeEach(async () => {
    accountViewService = {
      getAccountHeaderInfoByIdUsingGET: jest.fn().mockReturnValue(of(mockedAccount)),
    };
    await TestBed.configureTestingModule({
      providers: [{ provide: AccountViewService, useValue: accountViewService }],
      imports: [SharedModule, TaskSharedModule],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(SharedStore);
    store.setState(mockTaskState);
  });

  afterEach(() => jest.clearAllMocks());

  describe('account id has value', () => {
    beforeEach(() => {
      createComponent();
    });
    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display incorporate header', () => {
      const accountStatus = new AccountStatusPipe();
      const status = accountStatus.transform(mockedAccount.status);

      const installationCategory = new InstallationCategoryPipe();
      const category = installationCategory.transform(mockedAccount.installationCategory);

      expect(page.candidateAssigneesSelectInputValue.textContent).toEqual(
        ` ${mockedAccount.name}  Permit ID:  ${mockedAccount.permitId} / ${status}  Type:  ${mockedAccount.emitterType} / ${category} `,
      );
    });
  });

  describe('account id has no value', () => {
    beforeEach(() => {
      store.setState({ ...store.getState(), accountId: undefined });
      createComponent();
    });
    it('should create', () => {
      expect(component).toBeTruthy();
    });
    it('should not display incorporate header', () => {
      accountViewService.getAccountHeaderInfoByIdUsingGET.mockReturnValue(of(null));

      expect(page.candidateAssigneesSelectInputValue).toEqual(null);
    });
  });
});
