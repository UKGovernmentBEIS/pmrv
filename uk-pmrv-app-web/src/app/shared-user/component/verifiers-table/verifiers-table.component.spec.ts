import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormArray, FormGroup } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';

import { BehaviorSubject, Observable, of } from 'rxjs';

import { UsersAuthoritiesInfoDTO, UserStatusDTO } from 'pmrv-api';

import { BasePage } from '../../../../testing';
import { AuthService } from '../../../core/services/auth.service';
import { SharedModule } from '../../../shared/shared.module';
import { SharedUserModule } from '../../shared-user.module';
import { mockEditableVerifiersAuthorities, mockNonEditableVerifiersAuthorities } from './testing/mock-data';

describe('VerifiersTableComponent', () => {
  let page: Page;
  let component: TestComponent;
  let fixture: ComponentFixture<TestComponent>;

  const authService: Partial<jest.Mocked<AuthService>> = {
    userStatus: new BehaviorSubject<UserStatusDTO>({
      loginStatus: 'ENABLED',
      roleType: 'VERIFIER',
      userId: 'verifier1',
    }),
  };

  @Component({
    template: `
      <app-verifiers-table
        [verifiersAuthorities]="verifiersAuthorities$"
        (verifiersFormSubmitted)="onVerifiersFormSubmitted($event)"
        (discard)="onDiscard()"
      ></app-verifiers-table>
    `,
  })
  class TestComponent {
    verifiersAuthorities$: Observable<UsersAuthoritiesInfoDTO>;
    verifiersForm = new FormGroup({
      verifiersArray: new FormArray([]),
    });

    onVerifiersFormSubmitted(_: any): void {
      this.verifiersForm = _;
    }
    // eslint-disable-next-line @typescript-eslint/no-empty-function
    onDiscard(): void {}
  }

  class Page extends BasePage<TestComponent> {
    get tableHeader() {
      return this.query<HTMLTableCellElement>('form[id="verifiers"] thead tr');
    }
    get rows() {
      return this.queryAll<HTMLTableRowElement>('form[id="verifiers"] tbody tr');
    }
    get authorityStatuses() {
      return this.rows.map((row) => row.querySelector<HTMLSelectElement>('select[name$=".authorityStatus"]'));
    }
    set authorityStatusValues(value: string[]) {
      this.authorityStatuses.forEach((select, index) => {
        if (select && value[index] !== undefined) {
          this.setInputValue(`#${select.id}`, value[index]);
        }
      });
    }
    get tableNameButton() {
      return this.queryAll<HTMLButtonElement>('form[id="verifiers"] button[type="button"]')[0];
    }
    get saveBtn() {
      return this.query<HTMLButtonElement>('form[id="verifiers"] button[type="submit"]');
    }
    get discardChangesBtn() {
      return this.queryAll<HTMLButtonElement>('form[id="verifiers"] button[type="button"]')[1];
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, SharedUserModule, RouterTestingModule],
      providers: [{ provide: AuthService, useValue: authService }],
      declarations: [TestComponent],
    }).compileComponents();
  });

  describe('for editable verifier authorities', () => {
    beforeEach(() => {
      fixture = TestBed.createComponent(TestComponent);
      component = fixture.componentInstance;
      component.verifiersAuthorities$ = of(mockEditableVerifiersAuthorities);
      page = new Page(fixture);
      fixture.detectChanges();
      jest.clearAllMocks();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should show all editable columns in verifiers table', () => {
      expect(page.tableHeader.querySelectorAll('th')).toHaveLength(4);
      expect(page.tableHeader.querySelectorAll('th')[0].textContent.trim()).toEqual('Name');
      expect(page.tableHeader.querySelectorAll('th')[1].textContent.trim()).toEqual('User Type');
      expect(page.tableHeader.querySelectorAll('th')[2].textContent.trim()).toEqual('Account status');
      expect(page.tableHeader.querySelectorAll('th')[3].textContent.trim()).toEqual('');
    });

    it('should initialize verifiers table with default sorting by creation date', () => {
      expect(page.rows[0].querySelector('td').textContent.trim()).toEqual(
        `${mockEditableVerifiersAuthorities.authorities[1].firstName} ${mockEditableVerifiersAuthorities.authorities[1].lastName}`,
      );
      expect(page.rows[1].querySelector('td').textContent.trim()).toEqual(
        `${mockEditableVerifiersAuthorities.authorities[0].firstName} ${mockEditableVerifiersAuthorities.authorities[0].lastName}`,
      );
      expect(page.rows[2].querySelector('td').textContent.trim()).toEqual(
        `${mockEditableVerifiersAuthorities.authorities[3].firstName} ${mockEditableVerifiersAuthorities.authorities[2].lastName}`,
      );
      expect(page.rows[3].querySelector('td').textContent.trim()).toEqual(
        `${mockEditableVerifiersAuthorities.authorities[2].firstName} ${mockEditableVerifiersAuthorities.authorities[2].lastName}`,
      );
    });

    it('should sort verifiers table by name', () => {
      page.tableNameButton.click();
      fixture.detectChanges();

      expect(page.rows[0].querySelector('td').textContent.trim()).toEqual(
        `${mockEditableVerifiersAuthorities.authorities[0].firstName} ${mockEditableVerifiersAuthorities.authorities[0].lastName}`,
      );
      expect(page.rows[1].querySelector('td').textContent.trim()).toEqual(
        `${mockEditableVerifiersAuthorities.authorities[1].firstName} ${mockEditableVerifiersAuthorities.authorities[1].lastName}`,
      );
      expect(page.rows[2].querySelector('td').textContent.trim()).toEqual(
        `${mockEditableVerifiersAuthorities.authorities[3].firstName} ${mockEditableVerifiersAuthorities.authorities[3].lastName}`,
      );
      expect(page.rows[3].querySelector('td').textContent.trim()).toEqual(
        `${mockEditableVerifiersAuthorities.authorities[2].firstName} ${mockEditableVerifiersAuthorities.authorities[2].lastName}`,
      );

      page.tableNameButton.click();
      fixture.detectChanges();

      expect(page.rows[0].querySelector('td').textContent.trim()).toEqual(
        `${mockEditableVerifiersAuthorities.authorities[2].firstName} ${mockEditableVerifiersAuthorities.authorities[2].lastName}`,
      );
      expect(page.rows[1].querySelector('td').textContent.trim()).toEqual(
        `${mockEditableVerifiersAuthorities.authorities[3].firstName} ${mockEditableVerifiersAuthorities.authorities[3].lastName}`,
      );
      expect(page.rows[2].querySelector('td').textContent.trim()).toEqual(
        `${mockEditableVerifiersAuthorities.authorities[1].firstName} ${mockEditableVerifiersAuthorities.authorities[1].lastName}`,
      );
      expect(page.rows[3].querySelector('td').textContent.trim()).toEqual(
        `${mockEditableVerifiersAuthorities.authorities[0].firstName} ${mockEditableVerifiersAuthorities.authorities[0].lastName}`,
      );
    });

    it('should render the list of verifier users as links', () => {
      expect(page.rows[0].querySelectorAll('td')[0].querySelector('a')).toBeTruthy();
      expect(page.rows[1].querySelectorAll('td')[0].querySelector('a')).toBeTruthy();
      expect(page.rows[2].querySelectorAll('td')[0].querySelector('a')).toBeTruthy();
      expect(page.rows[3].querySelectorAll('td')[0].querySelector('a')).toBeNull();
    });

    it('should show locked status sign for locked users', () => {
      expect(page.rows[0].querySelectorAll('td')[2].querySelectorAll('.locked')).toHaveLength(0);
      expect(page.rows[1].querySelectorAll('td')[2].querySelectorAll('.locked')).toHaveLength(0);
      expect(page.rows[2].querySelectorAll('td')[2].querySelectorAll('.locked')).toHaveLength(1);
      expect(page.rows[3].querySelectorAll('td')[2].querySelectorAll('.locked')).toHaveLength(0);
    });

    it('should hide select options for users with status pending', () => {
      expect(page.rows[3].querySelectorAll('td')[2].textContent).toEqual('Awaiting confirmation');
    });

    it('should render a save and a discard changes button', () => {
      expect(page.saveBtn).toBeTruthy();
      expect(page.saveBtn.innerHTML.trim()).toEqual('Save');

      expect(page.discardChangesBtn).toBeTruthy();
      expect(page.discardChangesBtn.innerHTML.trim()).toEqual('Discard changes');
    });

    it('should emit on discard changes', () => {
      const onDiscardSpy = jest.spyOn(component, 'onDiscard');

      page.authorityStatusValues = ['ACTIVE', 'DISABLED', 'DISABLED', 'PENDING'];
      page.discardChangesBtn.click();
      expect(onDiscardSpy).toHaveBeenCalledTimes(1);
    });

    it('should not emit on save when no changes occured in verifiers table', () => {
      const onVerifiersFormSubmittedSpy = jest.spyOn(component, 'onVerifiersFormSubmitted');

      page.saveBtn.click();
      expect(onVerifiersFormSubmittedSpy).toHaveBeenCalledTimes(0);
    });

    it('should emit on save when verifiers table has changed', () => {
      const onVerifiersFormSubmittedSpy = jest.spyOn(component, 'onVerifiersFormSubmitted');

      page.authorityStatusValues = ['ACTIVE', 'DISABLED', 'DISABLED', 'PENDING'];
      page.saveBtn.click();
      expect(onVerifiersFormSubmittedSpy).toHaveBeenCalledTimes(1);
      expect(onVerifiersFormSubmittedSpy).toHaveBeenCalledWith(component.verifiersForm);
    });
  });

  describe('for non editable verifier authorities', () => {
    beforeEach(() => {
      fixture = TestBed.createComponent(TestComponent);
      component = fixture.componentInstance;
      component.verifiersAuthorities$ = of(mockNonEditableVerifiersAuthorities);
      page = new Page(fixture);
      fixture.detectChanges();
      jest.clearAllMocks();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should show only non editable columns in verifier tables', () => {
      expect(page.tableHeader.querySelectorAll('th')).toHaveLength(2);
      expect(page.tableHeader.querySelectorAll('th')[0].textContent.trim()).toEqual('Name');
      expect(page.tableHeader.querySelectorAll('th')[1].textContent.trim()).toEqual('User Type');
    });

    it('should initialize verifiers table with default sorting by creation date', () => {
      expect(page.rows[0].querySelector('td').textContent.trim()).toEqual(
        `${mockEditableVerifiersAuthorities.authorities[1].firstName} ${mockEditableVerifiersAuthorities.authorities[1].lastName}`,
      );
      expect(page.rows[1].querySelector('td').textContent.trim()).toEqual(
        `${mockEditableVerifiersAuthorities.authorities[0].firstName} ${mockEditableVerifiersAuthorities.authorities[0].lastName}`,
      );
      expect(page.rows[2].querySelector('td').textContent.trim()).toEqual(
        `${mockEditableVerifiersAuthorities.authorities[3].firstName} ${mockEditableVerifiersAuthorities.authorities[2].lastName}`,
      );
      expect(page.rows[3].querySelector('td').textContent.trim()).toEqual(
        `${mockEditableVerifiersAuthorities.authorities[2].firstName} ${mockEditableVerifiersAuthorities.authorities[2].lastName}`,
      );
    });

    it('should sort verifiers table by name', () => {
      page.tableNameButton.click();
      fixture.detectChanges();

      expect(page.rows[0].querySelector('td').textContent.trim()).toEqual(
        `${mockEditableVerifiersAuthorities.authorities[0].firstName} ${mockEditableVerifiersAuthorities.authorities[0].lastName}`,
      );
      expect(page.rows[1].querySelector('td').textContent.trim()).toEqual(
        `${mockEditableVerifiersAuthorities.authorities[1].firstName} ${mockEditableVerifiersAuthorities.authorities[1].lastName}`,
      );
      expect(page.rows[2].querySelector('td').textContent.trim()).toEqual(
        `${mockEditableVerifiersAuthorities.authorities[3].firstName} ${mockEditableVerifiersAuthorities.authorities[3].lastName}`,
      );
      expect(page.rows[3].querySelector('td').textContent.trim()).toEqual(
        `${mockEditableVerifiersAuthorities.authorities[2].firstName} ${mockEditableVerifiersAuthorities.authorities[2].lastName}`,
      );

      page.tableNameButton.click();
      fixture.detectChanges();

      expect(page.rows[0].querySelector('td').textContent.trim()).toEqual(
        `${mockEditableVerifiersAuthorities.authorities[2].firstName} ${mockEditableVerifiersAuthorities.authorities[2].lastName}`,
      );
      expect(page.rows[1].querySelector('td').textContent.trim()).toEqual(
        `${mockEditableVerifiersAuthorities.authorities[3].firstName} ${mockEditableVerifiersAuthorities.authorities[3].lastName}`,
      );
      expect(page.rows[2].querySelector('td').textContent.trim()).toEqual(
        `${mockEditableVerifiersAuthorities.authorities[1].firstName} ${mockEditableVerifiersAuthorities.authorities[1].lastName}`,
      );
      expect(page.rows[3].querySelector('td').textContent.trim()).toEqual(
        `${mockEditableVerifiersAuthorities.authorities[0].firstName} ${mockEditableVerifiersAuthorities.authorities[0].lastName}`,
      );
    });

    it('should render as link only the row that corresponds to the logged in user', () => {
      expect(page.rows[0].querySelectorAll('td')[0].querySelector('a')).toBeTruthy();
      expect(page.rows[1].querySelectorAll('td')[0].querySelector('a')).toBeNull();
      expect(page.rows[2].querySelectorAll('td')[0].querySelector('a')).toBeNull();
      expect(page.rows[3].querySelectorAll('td')[0].querySelector('a')).toBeNull();
    });

    it('should not render save and discard changes button', () => {
      expect(page.saveBtn).toBeFalsy();
      expect(page.discardChangesBtn).toBeFalsy();
    });
  });
});
