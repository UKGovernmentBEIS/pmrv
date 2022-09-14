import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';

import { RequestDetailsSearchResults, RequestsService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../testing';
import { SharedUserModule } from '../../shared-user/shared-user.module';
import { mockedAccount, mockWorkflowResults } from '../testing/mock-data';
import { workflowTypesMap } from './workflowMap';
import { WorkflowsComponent } from './workflows.component';

describe('WorkflowsComponent', () => {
  let component: WorkflowsComponent;
  let fixture: ComponentFixture<WorkflowsComponent>;
  let page: Page;

  const requestsService = mockClass(RequestsService);

  const activatedRouteStub = new ActivatedRouteStub(undefined, undefined, {
    account: mockedAccount,
  });

  class Page extends BasePage<WorkflowsComponent> {
    get accountCreationTypeCheckbox() {
      return this.query<HTMLInputElement>('input#workflowTypes-0');
    }
    get permitApplicationTypeCheckbox() {
      return this.query<HTMLInputElement>('input#workflowTypes-1');
    }

    get closedStatusCheckbox() {
      return this.query<HTMLInputElement>('input#workflowStatuses-0');
    }
    get openStatusCheckbox() {
      return this.query<HTMLInputElement>('input#workflowStatuses-1');
    }

    get workflowNames() {
      return this.queryAll<HTMLLIElement>('ul.govuk-list > li a');
    }

    get workflowStatusNames() {
      return this.queryAll<HTMLLIElement>('ul.govuk-list > li .govuk-tag');
    }
  }

  const createComponent = async () => {
    fixture = TestBed.createComponent(WorkflowsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [WorkflowsComponent],
      imports: [RouterTestingModule, SharedModule, SharedUserModule],
      providers: [
        { provide: ActivatedRoute, useValue: activatedRouteStub },
        { provide: RequestsService, useValue: requestsService },
      ],
    }).compileComponents();
  });

  describe('search filtering by type', () => {
    beforeEach(async () => {
      requestsService.getRequestDetailsByAccountIdUsingPOST.mockReturnValue(
        of({ requestDetails: [mockWorkflowResults.requestDetails[0]], total: 1 } as RequestDetailsSearchResults),
      );
    });

    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should filter results upon checking type', () => {
      page.permitApplicationTypeCheckbox.click();
      fixture.detectChanges();

      expect(requestsService.getRequestDetailsByAccountIdUsingPOST).toHaveBeenCalledTimes(1);
      expect(requestsService.getRequestDetailsByAccountIdUsingPOST).toHaveBeenLastCalledWith({
        accountId: mockedAccount.id,
        category: 'PERMIT',
        requestTypes: ['PERMIT_NOTIFICATION'],
        status: undefined,
        page: 0,
        pageSize: 30,
      });

      expect(page.workflowNames.map((workflowName) => workflowName.textContent.trim())).toEqual([
        '2 ' + workflowTypesMap.PERMIT_ISSUANCE,
      ]);
      expect(page.workflowStatusNames.map((tag) => tag.textContent.trim())).toEqual(['IN PROGRESS']);
    });
  });

  describe('search filtering by status', () => {
    beforeEach(async () => {
      requestsService.getRequestDetailsByAccountIdUsingPOST.mockReturnValue(
        of({ requestDetails: [mockWorkflowResults.requestDetails[0]], total: 1 } as RequestDetailsSearchResults),
      );
    });

    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should filter results upon checking status', () => {
      page.openStatusCheckbox.click();
      fixture.detectChanges();

      expect(requestsService.getRequestDetailsByAccountIdUsingPOST).toHaveBeenCalledTimes(1);
      expect(requestsService.getRequestDetailsByAccountIdUsingPOST).toHaveBeenLastCalledWith({
        accountId: mockedAccount.id,
        category: 'PERMIT',
        requestTypes: [],
        status: 'OPEN',
        page: 0,
        pageSize: 30,
      });

      expect(page.workflowNames.map((workflowName) => workflowName.textContent.trim())).toEqual([
        '2 ' + workflowTypesMap.PERMIT_ISSUANCE,
      ]);
      expect(page.workflowStatusNames.map((tag) => tag.textContent.trim())).toEqual(['IN PROGRESS']);
    });
  });
});
