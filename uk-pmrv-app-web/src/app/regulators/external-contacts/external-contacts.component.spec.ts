import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { GovukComponentsModule } from 'govuk-components';

import { CaExternalContactsDTO, CaExternalContactsService } from 'pmrv-api';

import { asyncData, BasePage, expectToHaveNavigatedTo, RouterStubComponent } from '../../../testing';
import { ExternalContactsComponent } from './external-contacts.component';

describe('ExternalContactsComponent', () => {
  let component: ExternalContactsComponent;
  let fixture: ComponentFixture<ExternalContactsComponent>;
  let page: Page;
  let externalContactsService: jest.Mocked<Partial<CaExternalContactsService>>;

  const caExternalContacts: CaExternalContactsDTO = {
    caExternalContacts: [
      {
        id: 1,
        name: 'Bob Squarepants',
        email: 'bob_squarepants@test.gr',
        description: 'Bikini bottom contact',
        lastUpdatedDate: new Date('2021-01-08').toISOString(),
      },
      {
        id: 2,
        name: 'Patrick Star',
        email: 'patrick_star@test.gr',
        description: 'Best friend contact',
        lastUpdatedDate: new Date('2021-01-10').toISOString(),
      },
      {
        id: 3,
        name: 'Gary Snail',
        email: 'gary_snail@test.gr',
        description: 'A snail pet',
        lastUpdatedDate: new Date('2021-01-05').toISOString(),
      },
    ],
    isEditable: true,
  };

  class Page extends BasePage<ExternalContactsComponent> {
    get rows() {
      return this.queryAll<HTMLTableRowElement>('tbody > tr');
    }

    get rowCells() {
      return this.rows.map((row) => Array.from(row.querySelectorAll('td')));
    }

    get rowHeaders() {
      return this.rows.map((row) => row.querySelector('th'));
    }

    get rowHeaderLinks() {
      return this.rows.map((row) => row.querySelector('th > a'));
    }

    get sortButtons() {
      return this.queryAll<HTMLButtonElement>('thead button');
    }

    get deleteButtons() {
      return this.rowCells.map((cells) => cells[cells.length - 1].querySelector('a'));
    }
  }

  const expectUserOrderToBe = (indexes: number[]) => {
    expect(page.rowHeaders.map((header) => header.textContent.trim())).toEqual(
      indexes.map((index) => caExternalContacts.caExternalContacts[index].name),
    );

    expect(page.rowCells.map((row) => row.map((cell) => cell.textContent.trim()))).toEqual(
      indexes.map((index) => [
        caExternalContacts.caExternalContacts[index].email,
        caExternalContacts.caExternalContacts[index].description,
        'Delete',
      ]),
    );
  };

  const createComponent = async () => {
    jest.clearAllMocks();
    fixture = TestBed.createComponent(ExternalContactsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    await fixture.whenStable();
    fixture.detectChanges();
  };

  beforeEach(async () => {
    externalContactsService = {
      getCaExternalContactsUsingGET: jest.fn().mockReturnValue(asyncData(caExternalContacts)),
    };

    await TestBed.configureTestingModule({
      imports: [
        GovukComponentsModule,
        RouterTestingModule.withRoutes([{ path: 'external-contacts/:userId/delete', component: RouterStubComponent }]),
      ],
      declarations: [ExternalContactsComponent, RouterStubComponent],
      providers: [{ provide: CaExternalContactsService, useValue: externalContactsService }],
    }).compileComponents();
  });

  beforeEach(createComponent);

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should list the external contacts', () => {
    expectUserOrderToBe([2, 0, 1]);
  });

  it('should sort by name', () => {
    page.sortButtons[0].click();
    fixture.detectChanges();

    expectUserOrderToBe([0, 2, 1]);

    page.sortButtons[0].click();
    fixture.detectChanges();

    expectUserOrderToBe([1, 2, 0]);
  });

  it('should sort by email', () => {
    page.sortButtons[1].click();
    fixture.detectChanges();

    expectUserOrderToBe([0, 2, 1]);

    page.sortButtons[1].click();
    fixture.detectChanges();

    expectUserOrderToBe([1, 2, 0]);
  });

  it('should delete a user', () => {
    page.deleteButtons[0].click();

    expectToHaveNavigatedTo('external-contacts/3/delete');
  });

  it('should hide delete and name links if non editable view', async () => {
    page.deleteButtons.forEach((button) => expect(button).not.toBeNull());
    page.rowHeaderLinks.forEach((link) => expect(link).not.toBeNull());

    externalContactsService.getCaExternalContactsUsingGET.mockReturnValue(
      asyncData({ ...caExternalContacts, isEditable: false }),
    );

    await createComponent();

    page.deleteButtons.forEach((button) => expect(button).toBeNull());
    page.rowHeaderLinks.forEach((link) => expect(link).toBeNull());
  });
});
