import { ComponentFixture, inject, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { BehaviorSubject, of } from 'rxjs';

import { AuthService } from '@core/services/auth.service';
import { ItemLinkPipe } from '@shared/pipes/item-link.pipe';
import { SharedModule } from '@shared/shared.module';

import {
  ItemDTO,
  ItemDTOResponse,
  RequestCreateActionProcessResponseDTO,
  RequestItemsService,
  RequestsService,
  UserStatusDTO,
} from 'pmrv-api';

import { ActivatedRouteStub, BasePage } from '../../../testing';
import { mockedAccount } from '../testing/mock-data';
import { ProcessActionsComponent } from './process-actions.component';
import { WorkflowMap } from './process-actions-map';

let component: ProcessActionsComponent;
let fixture: ComponentFixture<ProcessActionsComponent>;
let authService: Partial<jest.Mocked<AuthService>>;
let activatedRouteStub: ActivatedRouteStub;
let userStatus$: BehaviorSubject<UserStatusDTO>;
let requestService: Partial<jest.Mocked<RequestsService>>;
let requestItemsService: Partial<jest.Mocked<RequestItemsService>>;
const processRequestCreateActionResponse: RequestCreateActionProcessResponseDTO = { requestId: '1234' };
const taskId = 1;
let operatorsWorkflowMessagesMap: Partial<WorkflowMap>;
let regulatorsWorkflowMessagesMap: Partial<WorkflowMap>;
let availableItemsResponse: ItemDTOResponse;
let page: Page;
//TODO should update with more tasks as they are added
const mockAvailableTasksOperator = {
  PERMIT_SURRENDER: { valid: false, requests: ['PERMIT_SURRENDER'] },
  PERMIT_NOTIFICATION: { valid: true },
};
const taskKeysOperator = Object.keys(mockAvailableTasksOperator);
const validTaskKeysOperator = Object.entries(mockAvailableTasksOperator)
  .filter(([, value]) => value.valid)
  .map(([key]) => key);
const mockAvailableTasksRegulator = {
  PERMIT_REVOCATION: { valid: true },
};
const taskKeysRegulator = Object.keys(mockAvailableTasksRegulator);
const validTaskKeysRegulator = Object.entries(mockAvailableTasksRegulator)
  .filter(([, value]) => value.valid)
  .map(([key]) => key);
class Page extends BasePage<ProcessActionsComponent> {
  get taskContainers() {
    return this.queryAll<HTMLDivElement>('.govuk-grid-column-full');
  }
  get buttons() {
    return this.queryAll<HTMLButtonElement>('button');
  }
  getSectionStaticTextElements(parent: HTMLDivElement) {
    return Array.from(parent.querySelectorAll('.govuk-heading-m,.govuk-details__summary-text,.govuk-details__text'));
  }
  getSectionContainerButton(parent: HTMLDivElement) {
    return parent.querySelector('button');
  }
  getSectionErrorListItems(parent: HTMLDivElement) {
    return Array.from(parent.querySelectorAll('li'));
  }
}
const createRequestPayload = (requestType) => ({
  requestCreateActionType: requestType,
  requestCreateActionPayload: {
    payloadType: 'EMPTY_PAYLOAD',
  },
});
const createComponent = () => {
  fixture = TestBed.createComponent(ProcessActionsComponent);
  component = fixture.componentInstance;
  operatorsWorkflowMessagesMap = component['operatorsWorkflowMessagesMap'];
  regulatorsWorkflowMessagesMap = component['regulatorsWorkflowMessagesMap'];
  page = new Page(fixture);
  fixture.detectChanges();
};
const compileComponents = async () => {
  await TestBed.configureTestingModule({
    declarations: [ProcessActionsComponent],
    imports: [RouterTestingModule, SharedModule],
    providers: [
      { provide: ActivatedRoute, useValue: activatedRouteStub },
      { provide: RequestsService, useValue: requestService },
      { provide: RequestItemsService, useValue: requestItemsService },
      { provide: AuthService, useValue: authService },
      ItemLinkPipe,
    ],
  }).compileComponents();
  createComponent();
};

describe('ProcessActionsComponent', () => {
  beforeEach(async () => {
    activatedRouteStub = new ActivatedRouteStub(undefined, undefined, {
      account: mockedAccount,
    });
    requestItemsService = {
      getItemsByRequestUsingGET: jest.fn().mockReturnValue(of(availableItemsResponse)),
    };
  });
  describe('for operator', () => {
    beforeEach(async () => {
      userStatus$ = new BehaviorSubject<UserStatusDTO>({
        loginStatus: 'ENABLED',
        roleType: 'OPERATOR',
        userId: 'opTestId',
      });
      authService = {
        userStatus: userStatus$,
        loadUserStatus: jest.fn(),
      };
      requestService = {
        getAvailableWorkflowsUsingGET: jest.fn().mockReturnValue(of(mockAvailableTasksOperator)),
        processRequestCreateActionUsingPOST: jest.fn().mockReturnValue(of(processRequestCreateActionResponse)),
      };
      await compileComponents();
    });
    it('should create', () => {
      expect(component).toBeTruthy();
    });
    it('should retrieve available workflows for operator and display them as expected', () => {
      const taskContainers: HTMLDivElement[] = page.taskContainers;
      expect(taskContainers.length > 0).toBeTruthy();
      taskContainers.forEach((container, index) => {
        const currentTaskType = taskKeysOperator[index];
        const currentTask = mockAvailableTasksOperator[currentTaskType];
        const expectedMessagesMap = operatorsWorkflowMessagesMap[currentTaskType];
        const expectedMessagesValues: string[] = Object.values(expectedMessagesMap);
        const staticElementText = page
          .getSectionStaticTextElements(container)
          .map((element) => element.textContent.trim());
        expect(staticElementText.every((element) => expectedMessagesValues.includes(element))).toBeTruthy();
        if (mockAvailableTasksOperator[currentTaskType].valid) {
          const buttonText = page.getSectionContainerButton(container).textContent.trim();
          expect(expectedMessagesValues.includes(buttonText)).toBeTruthy();
        } else {
          const elementTextErrorList = page
            .getSectionErrorListItems(container)
            .map((element) => element.textContent.trim());
          const expectedErrorMessages: string[] = component['createErrorMessages'](currentTaskType, currentTask);
          expect(elementTextErrorList.every((element) => expectedErrorMessages.includes(element))).toBeTruthy();
        }
      });
    });
    it(
      'should processRequestCreateActionUsingPOST on button click for operator' +
        ' and then navigate to the task item page, when a single Task Item is received',
      inject([Router], (router: Router) => {
        const sendCreateActionRequestButtons: HTMLButtonElement[] = page.buttons;
        expect(sendCreateActionRequestButtons.length > 0).toBeTruthy();
        sendCreateActionRequestButtons.forEach((button, index) => {
          const requestType = validTaskKeysOperator[index] as ItemDTO['requestType'];
          //spies
          const onRequestButtonClickSpy = jest.spyOn(component, 'onRequestButtonClick');
          const navigateSpy = jest.spyOn(router, 'navigate');
          //test getItemsByRequestUsingGET returning single value
          const getItemsResponse: ItemDTOResponse = { items: [{ requestType, taskId }] };
          requestItemsService.getItemsByRequestUsingGET.mockReturnValueOnce(of(getItemsResponse));
          button.click();
          fixture.detectChanges();
          //expect button handler to be called
          expect(onRequestButtonClickSpy).toHaveBeenCalledTimes(1);
          expect(onRequestButtonClickSpy).toHaveBeenCalledWith(requestType);
          //expect processRequestCreateActionUsingPOST request to be handled
          expect(requestService.processRequestCreateActionUsingPOST).toHaveBeenCalledTimes(1);
          expect(requestService.processRequestCreateActionUsingPOST).toHaveBeenCalledWith(
            0,
            createRequestPayload(requestType),
          );
          //expect getItemsByRequestUsingGET to be called with POST requestId
          expect(requestItemsService.getItemsByRequestUsingGET).toHaveBeenCalledTimes(1);
          expect(requestItemsService.getItemsByRequestUsingGET).toHaveBeenCalledWith(
            processRequestCreateActionResponse.requestId,
          );
          //expect navigation to be triggered
          const link = component['itemLinkPipe'].transform(getItemsResponse.items[0]);
          expect(navigateSpy).toHaveBeenCalledTimes(1);
          expect(navigateSpy).toHaveBeenCalledWith(link);
        });
      }),
    );
    it(
      'should processRequestCreateActionUsingPOST on button click for operator' +
        ' and then navigate to dashboard, when multiple or 0 task Items are received',
      inject([Router], (router: Router) => {
        const sendCreateActionRequestButtons: HTMLButtonElement[] = page.buttons;
        expect(sendCreateActionRequestButtons.length > 0).toBeTruthy();
        sendCreateActionRequestButtons.forEach((button, index) => {
          const requestType = validTaskKeysOperator[index] as ItemDTO['requestType'];
          //spies
          const navigateSpy = jest.spyOn(router, 'navigate');
          //test getItemsByRequestUsingGET returning no values
          let getItemsResponse: ItemDTOResponse = { items: [] };
          requestItemsService.getItemsByRequestUsingGET.mockReturnValueOnce(of(getItemsResponse));
          button.click();
          expect(navigateSpy).toHaveBeenCalledTimes(1);
          expect(navigateSpy).toHaveBeenLastCalledWith(['/dashboard']);
          //change mock result to multiple values
          getItemsResponse = {
            items: [
              { requestType, taskId },
              { requestType, taskId: taskId + 1 },
            ],
          };
          requestItemsService.getItemsByRequestUsingGET.mockReturnValueOnce(of(getItemsResponse));
          button.click();
          expect(navigateSpy).toHaveBeenCalledTimes(2);
          expect(navigateSpy).toHaveBeenLastCalledWith(['/dashboard']);
        });
      }),
    );
  });

  describe('for regulator', () => {
    beforeEach(async () => {
      userStatus$ = new BehaviorSubject<UserStatusDTO>({
        loginStatus: 'ENABLED',
        roleType: 'REGULATOR',
        userId: 'opTestId',
      });
      authService = {
        userStatus: userStatus$,
        loadUserStatus: jest.fn(),
      };
      requestService = {
        getAvailableWorkflowsUsingGET: jest.fn().mockReturnValue(of(mockAvailableTasksRegulator)),
        processRequestCreateActionUsingPOST: jest.fn().mockReturnValue(of(processRequestCreateActionResponse)),
      };
      await compileComponents();
    });
    it('should create', () => {
      expect(component).toBeTruthy();
    });
    it('should retrieve available workflows for regulator and display them as expected', () => {
      const taskContainers: HTMLDivElement[] = page.taskContainers;
      expect(taskContainers.length > 0).toBeTruthy();
      taskContainers.forEach((container, index) => {
        const currentTaskType = taskKeysRegulator[index];
        const currentTask = mockAvailableTasksRegulator[currentTaskType];
        const expectedMessagesMap = regulatorsWorkflowMessagesMap[currentTaskType];
        const expectedMessagesValues: string[] = Object.values(expectedMessagesMap);
        const staticElementText = page
          .getSectionStaticTextElements(container)
          .map((element) => element.textContent.trim());
        expect(staticElementText.every((element) => expectedMessagesValues.includes(element))).toBeTruthy();
        if (mockAvailableTasksRegulator[currentTaskType].valid) {
          const buttonText = page.getSectionContainerButton(container).textContent.trim();
          expect(expectedMessagesValues.includes(buttonText)).toBeTruthy();
        } else {
          const elementTextErrorList = page
            .getSectionErrorListItems(container)
            .map((element) => element.textContent.trim());
          const expectedErrorMessages: string[] = component['createErrorMessages'](currentTaskType, currentTask);
          expect(elementTextErrorList.every((element) => expectedErrorMessages.includes(element))).toBeTruthy();
        }
      });
    });
    it(
      'should processRequestCreateActionUsingPOST on button click for regulator' +
        ' and then navigate to the task item page, when a single Task Item is received',
      inject([Router], (router: Router) => {
        const sendCreateActionRequestButtons: HTMLButtonElement[] = page.buttons;
        expect(sendCreateActionRequestButtons.length > 0).toBeTruthy();
        sendCreateActionRequestButtons.forEach((button, index) => {
          const requestType = validTaskKeysRegulator[index] as ItemDTO['requestType'];
          //spies
          const onRequestButtonClickSpy = jest.spyOn(component, 'onRequestButtonClick');
          const navigateSpy = jest.spyOn(router, 'navigate');
          //test getItemsByRequestUsingGET returning single value
          const getItemsResponse: ItemDTOResponse = { items: [{ requestType, taskId }] };
          requestItemsService.getItemsByRequestUsingGET.mockReturnValueOnce(of(getItemsResponse));
          button.click();
          fixture.detectChanges();
          //expect button handler to be called
          expect(onRequestButtonClickSpy).toHaveBeenCalledTimes(1);
          expect(onRequestButtonClickSpy).toHaveBeenCalledWith(requestType);
          //expect processRequestCreateActionUsingPOST request to be handled
          expect(requestService.processRequestCreateActionUsingPOST).toHaveBeenCalledTimes(1);
          expect(requestService.processRequestCreateActionUsingPOST).toHaveBeenCalledWith(
            0,
            createRequestPayload(requestType),
          );
          //expect getItemsByRequestUsingGET to be called with POST requestId
          expect(requestItemsService.getItemsByRequestUsingGET).toHaveBeenCalledTimes(1);
          expect(requestItemsService.getItemsByRequestUsingGET).toHaveBeenCalledWith(
            processRequestCreateActionResponse.requestId,
          );
          //expect navigation to be triggered
          const link = component['itemLinkPipe'].transform(getItemsResponse.items[0]);
          expect(navigateSpy).toHaveBeenCalledTimes(1);
          expect(navigateSpy).toHaveBeenCalledWith(link);
        });
      }),
    );
    it(
      'should processRequestCreateActionUsingPOST on button click for regulator' +
        ' and then navigate to dashboard, when multiple or 0 task Items are received',
      inject([Router], (router: Router) => {
        const sendCreateActionRequestButtons: HTMLButtonElement[] = page.buttons;
        expect(sendCreateActionRequestButtons.length > 0).toBeTruthy();
        sendCreateActionRequestButtons.forEach((button, index) => {
          const requestType = validTaskKeysRegulator[index] as ItemDTO['requestType'];
          //spies
          const navigateSpy = jest.spyOn(router, 'navigate');
          //test getItemsByRequestUsingGET returning no values
          let getItemsResponse: ItemDTOResponse = { items: [] };
          requestItemsService.getItemsByRequestUsingGET.mockReturnValueOnce(of(getItemsResponse));
          button.click();
          expect(navigateSpy).toHaveBeenCalledTimes(1);
          expect(navigateSpy).toHaveBeenLastCalledWith(['/dashboard']);
          //change mock result to multiple values
          getItemsResponse = {
            items: [
              { requestType, taskId },
              { requestType, taskId: taskId + 1 },
            ],
          };
          requestItemsService.getItemsByRequestUsingGET.mockReturnValueOnce(of(getItemsResponse));
          button.click();
          expect(navigateSpy).toHaveBeenCalledTimes(2);
          expect(navigateSpy).toHaveBeenLastCalledWith(['/dashboard']);
        });
      }),
    );
  });
});
