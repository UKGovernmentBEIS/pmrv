import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BehaviorSubject, take } from 'rxjs';

import {
  ItemDTOResponse,
  ItemsAssignedToMeService,
  ItemsAssignedToOthersService,
  UnassignedItemsService,
  UserStatusDTO,
} from 'pmrv-api';

import { asyncData, BasePage } from '../../testing';
import { AuthService } from '../core/services/auth.service';
import { SharedModule } from '../shared/shared.module';
import { DashboardComponent } from './dashboard.component';
import { ItemTypePipe } from './item-type.pipe';

class Page extends BasePage<DashboardComponent> {
  get assignedToOthersTabLink() {
    return this.query<HTMLAnchorElement>('#tab_assigned-to-others');
  }

  get unassignedTabLink() {
    return this.query<HTMLAnchorElement>('#tab_unassigned');
  }

  get assignedToMeTab() {
    return this.query<HTMLDivElement>('#assigned-to-me');
  }

  get assignedToOthersTab() {
    return this.query<HTMLDivElement>('#assigned-to-others');
  }

  get unassignedTab() {
    return this.query<HTMLDivElement>('#unassigned');
  }
}

describe('DashboardComponent', () => {
  let component: DashboardComponent;
  let fixture: ComponentFixture<DashboardComponent>;
  let page: Page;
  let itemsAssignedToMeService: Partial<jest.Mocked<ItemsAssignedToMeService>>;
  let itemsAssignedToOthersService: Partial<jest.Mocked<ItemsAssignedToOthersService>>;
  let unassignedItemsService: Partial<jest.Mocked<UnassignedItemsService>>;
  const authService: Partial<jest.Mocked<AuthService>> = {
    userStatus: new BehaviorSubject<UserStatusDTO>({
      loginStatus: 'ENABLED',
      roleType: 'OPERATOR',
      userId: 'opTestId',
    }),
  };

  const mockTasks: ItemDTOResponse = {
    items: [
      {
        taskType: 'INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW',
        requestType: 'INSTALLATION_ACCOUNT_OPENING',
        taskAssigneeType: 'REGULATOR',
        daysRemaining: null,
        leName: 'Pixar',
        accountId: 1,
        taskAssignee: null,
        accountName: 'Monster inc',
        competentAuthority: 'ENGLAND',
        creationDate: new Date('2020-11-13T13:00:00Z').toISOString(),
        requestId: '1',
        taskId: 2,
        isNew: true,
        permitId: '11',
      },
      {
        taskType: 'INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW',
        requestType: 'INSTALLATION_ACCOUNT_OPENING',
        taskAssigneeType: 'OPERATOR',
        daysRemaining: 13,
        leName: 'Netflix',
        accountId: 2,
        taskAssignee: { firstName: 'Sasha', lastName: 'Baron Cohen' },
        accountName: 'Ali G records',
        competentAuthority: 'ENGLAND',
        creationDate: new Date('2020-11-13T15:00:00Z').toISOString(),
        requestId: '3',
        taskId: 4,
        isNew: false,
        permitId: '22',
      },
    ],
    totalItems: 1,
  };
  const unassignedItems: ItemDTOResponse = {
    items: [
      {
        taskType: 'INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW',
        requestType: 'INSTALLATION_ACCOUNT_OPENING',
        taskAssigneeType: 'REGULATOR',
        leName: 'Vans',
        accountId: 18,
        accountName: 'Vans F',
        competentAuthority: 'ENGLAND',
        creationDate: new Date('2020-11-27T10:13:49Z').toISOString(),
        requestId: '40',
        taskId: 19,
        permitId: '33',
      },
    ],
    totalItems: 1,
  };

  const createComponent = () => {
    fixture = TestBed.createComponent(DashboardComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  };

  beforeEach(async () => {
    itemsAssignedToMeService = {
      getAssignedItemsUsingGET: jest.fn().mockReturnValue(asyncData(mockTasks)),
    };
    itemsAssignedToOthersService = {
      getAssignedToOthersItemsUsingGET: jest
        .fn()
        .mockReturnValue(asyncData({ items: mockTasks.items.slice(1, 2), totalPages: mockTasks.totalItems })),
    };
    unassignedItemsService = {
      getUnassignedItemsUsingGET: jest.fn().mockReturnValue(asyncData(unassignedItems)),
    };
    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule.withRoutes([], { paramsInheritanceStrategy: 'always' })],
      providers: [
        { provide: ItemsAssignedToMeService, useValue: itemsAssignedToMeService },
        { provide: ItemsAssignedToOthersService, useValue: itemsAssignedToOthersService },
        { provide: UnassignedItemsService, useValue: unassignedItemsService },
        { provide: AuthService, useValue: authService },
      ],
      declarations: [DashboardComponent, ItemTypePipe],
    }).compileComponents();
  });

  beforeEach(createComponent);

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render assigned to me table rows', () => {
    fixture.detectChanges();

    const cells = Array.from(page.assignedToMeTab.querySelectorAll('td'));
    const anchors = Array.from(page.assignedToMeTab.querySelectorAll('td'))
      .map((cell) => cell.querySelector('a'))
      .filter((anchor) => !!anchor);
    expect(anchors.map((anchor) => anchor.href)).toEqual([
      expect.stringContaining('/installation-account/2'),
      expect.stringContaining('/installation-account/4'),
    ]);
    expect(anchors.map((anchor) => anchor.textContent)).toEqual(['Application', 'Application']);
    expect(cells.map((cell) => cell.textContent.trim())).toEqual([
      ...['ApplicationNew', '', '11', 'Monster inc', 'Pixar'],
      ...['Application', '13', '22', 'Ali G records', 'Netflix'],
    ]);
  });

  it('should render assigned to others table rows', () => {
    page.assignedToOthersTabLink.click();
    fixture.detectChanges();

    const cells = Array.from(page.assignedToOthersTab.querySelectorAll('td'));
    const anchors = Array.from(page.assignedToOthersTab.querySelectorAll('td'))
      .map((cell) => cell.querySelector('a'))
      .filter((anchor) => !!anchor);
    expect(anchors.map((anchor) => anchor.href)).toEqual([expect.stringContaining('/installation-account/4')]);
    expect(anchors.map((anchor) => anchor.textContent)).toEqual(['Application']);
    expect(cells.map((cell) => cell.textContent.trim())).toEqual([
      ...['Application', '13', '22', 'Ali G records', 'Netflix'],
    ]);
  });

  describe('for operators', () => {
    beforeEach(() => {
      authService.userStatus.next({ roleType: 'OPERATOR', userId: '331' });
      fixture.detectChanges();
    });

    it('should not display the unassigned items', () => {
      expect(page.unassignedTabLink).toBeFalsy();
    });
  });

  describe('for regulators', () => {
    beforeEach(() => {
      authService.userStatus.next({ roleType: 'REGULATOR', userId: '332' });
      fixture.detectChanges();
    });

    it('should display the unassigned items', async () => {
      authService.userStatus.pipe(take(1)).subscribe(() => {
        expect(page.unassignedTabLink).toBeTruthy();
        page.unassignedTabLink.click();
        fixture.detectChanges();

        const cells = Array.from(page.unassignedTab.querySelectorAll('td'));
        const anchors = Array.from(page.unassignedTab.querySelectorAll('td'))
          .map((cell) => cell.querySelector('a'))
          .filter((anchor) => !!anchor);
        expect(anchors.map((anchor) => anchor.href)).toEqual([expect.stringContaining('/installation-account/19')]);
        expect(anchors.map((anchor) => anchor.textContent)).toEqual(['Application']);
        expect(cells.map((cell) => cell.textContent.trim())).toEqual([
          ...['Application', 'Installation account', 'Unassigned', '', '33', 'Vans F', 'Vans'],
        ]);
      });
    });
  });

  describe('for verifiers', () => {
    beforeEach(() => {
      authService.userStatus.next({ roleType: 'VERIFIER', userId: '332' });
      fixture.detectChanges();
    });

    it('should allow view of unassigned items', () => {
      expect(page.unassignedTabLink).toBeTruthy();
    });
  });
});
