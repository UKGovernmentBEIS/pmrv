import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../../../../testing';
import { SharedModule } from '../../../../../shared/shared.module';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { mockPermitApplyPayload } from '../../../../testing/mock-permit-apply-action';
import { mockPostBuild, mockState } from '../../../../testing/mock-state';
import { TransferredCO2Module } from '../../transferred-co2.module';
import { DetailsComponent } from './details.component';

describe('DetailsComponent', () => {
  let component: DetailsComponent;
  let fixture: ComponentFixture<DetailsComponent>;
  let page: Page;
  let router: Router;
  let store: PermitApplicationStore;

  const activatedRoute = new ActivatedRouteStub(
    {},
    {},
    {
      taskKey: 'monitoringApproaches.TRANSFERRED_CO2.receivingTransferringInstallations',
      statusKey: 'TRANSFERRED_CO2_Installations',
    },
  );
  const tasksService = mockClass(TasksService);

  class Page extends BasePage<DetailsComponent> {
    get typeReceivingValue() {
      return this.query<HTMLInputElement>('#type-option0');
    }

    get identificationCode() {
      return this.getInputValue('#installationIdentificationCode');
    }
    set identificationCode(value: string) {
      this.setInputValue('#installationIdentificationCode', value);
    }

    get operator() {
      return this.getInputValue('#operator');
    }
    set operator(value: string) {
      this.setInputValue('#operator', value);
    }

    get name() {
      return this.getInputValue('#installationName');
    }
    set name(value: string) {
      this.setInputValue('#installationName', value);
    }

    get co2source() {
      return this.getInputValue('#co2source');
    }
    set co2source(value: string) {
      this.setInputValue('#co2source', value);
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }

    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get errorSummaryLinks() {
      return Array.from(this.query<HTMLDivElement>('.govuk-error-summary').querySelectorAll('a')).map((item) =>
        item.textContent.trim(),
      );
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule, TransferredCO2Module],
      providers: [
        { provide: ActivatedRoute, useValue: activatedRoute },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();
  });

  const createComponent = () => {
    store = TestBed.inject(PermitApplicationStore);
    store.setState(mockState);
    router = TestBed.inject(Router);
    fixture = TestBed.createComponent(DetailsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  it('should create', () => {
    createComponent();
    expect(component).toBeTruthy();
  });

  describe('for adding new installation', () => {
    beforeEach(createComponent);

    it('should show validation error when submit empty form', () => {
      expect(page.errorSummary).toBeFalsy();

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryLinks).toEqual([
        'Select a type',
        'Enter an installation code',
        'Enter an operator',
        'Enter an installation name',
        'Enter a source of CO2',
      ]);
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(0);
    });

    it('should submit a valid form, update the store and navigate back to task', () => {
      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');

      page.typeReceivingValue.click();
      page.identificationCode = 'code2';
      page.name = 'installationName2';
      page.operator = 'operator2';
      page.co2source = 'source2';

      const expectedInstallations = [
        ...mockPermitApplyPayload.permit.monitoringApproaches.TRANSFERRED_CO2['receivingTransferringInstallations'],
        {
          type: 'RECEIVING',
          co2source: 'source2',
          installationIdentificationCode: 'code2',
          installationName: 'installationName2',
          operator: 'operator2',
        },
      ];

      fixture.detectChanges();
      page.submitButton.click();
      fixture.detectChanges();

      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
        mockPostBuild(
          {
            ...mockPermitApplyPayload.permit,
            monitoringApproaches: {
              ...mockPermitApplyPayload.permit.monitoringApproaches,
              TRANSFERRED_CO2: {
                ...mockPermitApplyPayload.permit.monitoringApproaches.TRANSFERRED_CO2,
                receivingTransferringInstallations: expectedInstallations,
              },
            },
          },
          {
            ...mockPermitApplyPayload.permitSectionsCompleted,
            TRANSFERRED_CO2_Installations: [false],
          },
        ),
      );

      expect(navigateSpy).toHaveBeenCalledWith(['..'], { relativeTo: activatedRoute, state: { notification: true } });
    });
  });

  describe('for editing existing installation', () => {
    beforeEach(() => {
      activatedRoute.setParamMap({ index: '0' });
    });
    beforeEach(createComponent);

    it('should fill the form from the store', () => {
      const installation =
        mockPermitApplyPayload.permit.monitoringApproaches.TRANSFERRED_CO2['receivingTransferringInstallations'][0];
      expect(page.typeReceivingValue.checked).toBeTruthy();
      expect(page.identificationCode).toEqual(installation.installationIdentificationCode);
      expect(page.operator).toEqual(installation.operator);
      expect(page.name).toEqual(installation.installationName);
      expect(page.co2source).toEqual(installation.co2source);
    });

    it('should submit a valid form, update the store and navigate back to task', () => {
      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');

      page.identificationCode = 'code3';

      fixture.detectChanges();
      page.submitButton.click();
      fixture.detectChanges();

      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
        mockPostBuild(
          {
            ...mockPermitApplyPayload.permit,
            monitoringApproaches: {
              ...mockPermitApplyPayload.permit.monitoringApproaches,
              TRANSFERRED_CO2: {
                ...mockPermitApplyPayload.permit.monitoringApproaches.TRANSFERRED_CO2,
                receivingTransferringInstallations: [
                  {
                    type: 'RECEIVING',
                    co2source: 'source1',
                    installationIdentificationCode: 'code3',
                    installationName: 'name1',
                    operator: 'operator1',
                  },
                ],
              },
            },
          },
          {
            ...mockPermitApplyPayload.permitSectionsCompleted,
            TRANSFERRED_CO2_Installations: [false],
          },
        ),
      );
      expect(navigateSpy).toHaveBeenCalledWith(['..'], { relativeTo: activatedRoute, state: { notification: true } });
    });
  });
});
