import { Component } from '@angular/core';
import { ComponentFixture, fakeAsync, TestBed, tick } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { CountryService } from '@core/services/country.service';
import { LegalEntity } from '@shared/interfaces/legal-entity';
import { SharedModule } from '@shared/shared.module';
import { address, CountryServiceStub } from '@testing';

import { LegalEntityDTO, RequestsService, TasksService } from 'pmrv-api';

import { ApplicationSubmittedComponent } from '../application-submitted/application-submitted.component';
import { Commencement } from '../commencement/commencement';
import { EtsScheme } from '../ets-scheme/ets-scheme';
import { installationFormFactory } from '../factories/installation-form.factory';
import { legalEntityFormFactory } from '../factories/legal-entity-form.factory';
import { OffshoreInstallation, OnshoreInstallation } from '../installation-type/installation';
import { SubmitApplicationActionPayload } from '../pipes/submit-application';
import {
  ApplicationSectionType,
  CommencementSection,
  EtsSchemeSection,
  initialState,
  InstallationAccountApplicationState,
  InstallationSection,
  LegalEntitySection,
} from '../store/installation-account-application.state';
import { InstallationAccountApplicationStore } from '../store/installation-account-application.store';
import { SummaryComponent } from './summary.component';

describe('SummaryComponent', () => {
  let component: SummaryComponent;
  let fixture: ComponentFixture<SummaryComponent>;
  let element: HTMLElement;
  let store: InstallationAccountApplicationStore;
  let tasksService: Partial<jest.Mocked<TasksService>>;
  let requestsService: RequestsService;
  let router: Router;
  let activatedRoute: ActivatedRoute;

  @Component({ template: '<router-outlet></router-outlet>' })
  class RouterComponent {}

  const operator: LegalEntity = {
    detailsGroup: {
      name: 'Operator name',
      address,
      referenceNumber: 'ab123456',
      type: 'PARTNERSHIP',
    },
    selectGroup: { isNew: true },
  };
  const installation: OnshoreInstallation & OffshoreInstallation = {
    onshoreGroup: {
      name: 'Installation name',
      siteName: 'Site name',
      address: { ...address, line2: null },
      gridReference: 'Test reference',
    },
    offshoreGroup: {
      siteName: 'Site name',
      latitude: { cardinalDirection: null, degree: null, minute: null, second: null },
      longitude: { cardinalDirection: null, degree: null, minute: null, second: null },
      name: null,
    },
    locationGroup: { location: 'ENGLAND' },
    installationTypeGroup: { type: 'ONSHORE' },
  };
  const etsScheme: EtsScheme = {
    etsSchemeType: 'UK_ETS_INSTALLATIONS',
  };

  const commencement: Commencement = {
    commencementDate: new Date('2020-07-07'),
  };
  const applicationPayload: SubmitApplicationActionPayload = {
    payloadType: 'INSTALLATION_ACCOUNT_OPENING_SUBMIT_APPLICATION_PAYLOAD',
    accountType: 'INSTALLATION',
    commencementDate: commencement.commencementDate,
    emissionTradingScheme: etsScheme.etsSchemeType,
    competentAuthority: installation.locationGroup.location,
    legalEntity: {
      name: operator.detailsGroup.name,
      address: operator.detailsGroup.address,
      referenceNumber: operator.detailsGroup.referenceNumber,
      type: operator.detailsGroup.type,
    },
    name: installation.onshoreGroup.name,
    siteName: installation.onshoreGroup.siteName,
    location: {
      type: installation.installationTypeGroup.type,
      address: {
        city: installation.onshoreGroup.address.city,
        country: installation.onshoreGroup.address.country,
        line1: installation.onshoreGroup.address.line1,
        postcode: installation.onshoreGroup.address.postcode,
      },
      gridReference: installation.onshoreGroup.gridReference,
    },
  };

  const state: InstallationAccountApplicationState = {
    tasks: [
      {
        ...initialState.tasks.find((task) => task.type === ApplicationSectionType.legalEntity),
        value: operator,
      } as LegalEntitySection,
      {
        ...initialState.tasks.find((task) => task.type === ApplicationSectionType.installation),
        value: installation,
      } as InstallationSection,
      {
        ...initialState.tasks.find((task) => task.type === ApplicationSectionType.etsScheme),
        value: etsScheme,
      } as EtsSchemeSection,
      {
        ...initialState.tasks.find((task) => task.type === ApplicationSectionType.commencement),
        value: commencement,
      } as CommencementSection,
    ],
  };

  const existingLegalEntity: LegalEntityDTO = {
    address: {
      postcode: 'Existing postcode',
      country: 'GR',
      city: 'Existing city',
      line1: 'Existing line 1',
      line2: 'Existing line 2',
    },
    id: 1,
    name: 'Existing name',
    noReferenceNumberReason: '',
    referenceNumber: 'Existing reference number',
    type: 'SOLE_TRADER',
  };

  function getSummaryArrayByIndex(index: number): string[][] {
    const summaryElement = Array.from(element.querySelectorAll<HTMLDivElement>('.govuk-summary-list'))[index];
    return Array.from(summaryElement.querySelectorAll('.govuk-summary-list__key')).map((item: HTMLElement) => [
      item.textContent.trim(),
      item.nextElementSibling.textContent.trim(),
    ]);
  }

  beforeEach(async () => {
    tasksService = { processRequestTaskActionUsingPOST: jest.fn().mockReturnValue(of(null)) };
    await TestBed.configureTestingModule({
      imports: [
        SharedModule,
        RouterTestingModule.withRoutes([
          { path: 'submitted', component: ApplicationSubmittedComponent },
          { path: 'decision', component: ApplicationSubmittedComponent },
        ]),
      ],
      declarations: [SummaryComponent, ApplicationSubmittedComponent, RouterComponent],
      providers: [
        { provide: CountryService, useClass: CountryServiceStub },
        { provide: TasksService, useValue: tasksService },
        installationFormFactory,
        legalEntityFormFactory,
      ],
    }).compileComponents();

    store = TestBed.inject(InstallationAccountApplicationStore);
    store.setState(state);
    requestsService = TestBed.inject(RequestsService);
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
  });

  beforeEach(() => {
    TestBed.createComponent(RouterComponent);
    fixture = TestBed.createComponent(SummaryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    element = fixture.nativeElement;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the date of commencement', () => {
    expect(getSummaryArrayByIndex(6)).toEqual(
      expect.arrayContaining([['What is the date of commencement of Annex 1 activities?', '7 Jul 2020']]),
    );
  });

  it('should display the contact details of the operator', () => {
    expect(getSummaryArrayByIndex(1)).toEqual(
      expect.arrayContaining([
        ['New or existing organisation', 'New'],
        ['Type of the operator', 'Partnership'],
        ['Name of the operator', 'Operator name'],
        ['Company registration number', 'ab123456'],
        ['Address', 'Molos 1, Limani Patras'],
        ['Town or city', 'Patras'],
        ['Country', 'Greece'],
        ['Post code', '26555'],
      ]),
    );
  });

  it('should display the contact details of the operator with no companies ref', () => {
    const [legalEntityTask, ...tasks] = state.tasks;

    TestBed.inject(InstallationAccountApplicationStore).setState({
      ...state,
      tasks: [
        {
          ...(legalEntityTask as LegalEntitySection),
          value: {
            ...legalEntityTask.value,
            detailsGroup: {
              ...(legalEntityTask as LegalEntitySection).value.detailsGroup,
              referenceNumber: null,
              noReferenceNumberReason: 'no reference details',
            },
          } as LegalEntity,
        },
        ...tasks,
      ],
    });
    fixture.detectChanges();
    expect(getSummaryArrayByIndex(1)).toEqual(
      expect.arrayContaining([
        ['New or existing organisation', 'New'],
        ['Type of the operator', 'Partnership'],
        ['Name of the operator', 'Operator name'],
        ['Company registration number', 'no reference details'],
        ['Address', 'Molos 1, Limani Patras'],
        ['Town or city', 'Patras'],
        ['Country', 'Greece'],
        ['Post code', '26555'],
      ]),
    );
  });

  it('should display the installation details for onshore', () => {
    expect(getSummaryArrayByIndex(3)).toEqual(
      expect.arrayContaining([
        ['Installation name', 'Installation name'],
        ['Site name', 'Site name'],
        ['UK Ordnance Survey grid reference', 'Test reference'],
        ['Address', 'Molos 1'],
        ['Town or city', 'Patras'],
        ['Country', 'Greece'],
        ['Post code', '26555'],
      ]),
    );
  });

  it('should display the installation type', () => {
    expect(getSummaryArrayByIndex(2)).toEqual(expect.arrayContaining([['Installation type', 'Onshore']]));
  });

  it('should display the location', () => {
    expect(getSummaryArrayByIndex(4)).toEqual(expect.arrayContaining([['Location of the installation', 'England']]));
  });

  it('should display the trading scheme', () => {
    expect(getSummaryArrayByIndex(5)).toEqual(
      expect.arrayContaining([['Emissions trading scheme which the installation will be part of', 'UK ETS']]),
    );
  });

  it('should display the offshore details', () => {
    const offshoreInstallation: OffshoreInstallation = {
      installationTypeGroup: { type: 'OFFSHORE' },
      offshoreGroup: {
        name: 'Installation name',
        siteName: 'Site name',
        longitude: { degree: 30, minute: 40, second: 50, cardinalDirection: 'NORTH' },
        latitude: { degree: 35, minute: 20, second: 10, cardinalDirection: 'EAST' },
      },
    };

    store.updateTask(ApplicationSectionType.installation, offshoreInstallation);
    fixture.detectChanges();

    expect(getSummaryArrayByIndex(2)).toEqual(expect.arrayContaining([['Installation type', 'Offshore']]));

    expect(getSummaryArrayByIndex(3)).toEqual(
      expect.arrayContaining([
        ['Latitude', '35° 20\' 10" east'],
        ['Longitude', '30° 40\' 50" north'],
      ]),
    );
  });

  it('should submit the operator application', fakeAsync(() => {
    const submitButton = element.querySelector<HTMLButtonElement>('button[title="Submit the application"]');
    const submitRequestSpy = jest
      .spyOn(requestsService, 'processRequestCreateActionUsingPOST')
      .mockReturnValue(of(null));
    const navigateSpy = jest.spyOn(router, 'navigate');

    submitButton.click();
    tick();
    fixture.detectChanges();

    expect(submitButton.disabled).toBeTruthy();
    expect(submitRequestSpy).toHaveBeenCalledWith(null, {
      requestCreateActionType: 'INSTALLATION_ACCOUNT_OPENING_SUBMIT_APPLICATION',
      requestCreateActionPayload: applicationPayload,
    });
    expect(navigateSpy).toHaveBeenCalledWith(['../../submitted'], { relativeTo: activatedRoute });
  }));

  it('should display the contact details of an existing operator', () => {
    const [legalEntityTask, ...tasks] = state.tasks;

    store.setState({
      ...state,
      tasks: [
        {
          ...(legalEntityTask as LegalEntitySection),
          value: {
            selectGroup: { id: 1, isNew: false },
            detailsGroup: {
              name: existingLegalEntity.name,
              address: existingLegalEntity.address,
              type: existingLegalEntity.type,
              noReferenceNumberReason: existingLegalEntity.noReferenceNumberReason,
              referenceNumber: existingLegalEntity.referenceNumber,
            },
          },
        },
        ...tasks,
      ],
    });

    fixture.detectChanges();

    expect(getSummaryArrayByIndex(1)).toEqual(
      expect.arrayContaining([
        ['New or existing organisation', 'Existing'],
        ['Type of the operator', 'Sole trader'],
        ['Name of the operator', 'Existing name'],
        ['Company registration number', 'Existing reference number'],
        ['Address', 'Existing line 1, Existing line 2'],
        ['Town or city', 'Existing city'],
        ['Country', 'Greece'],
        ['Post code', 'Existing postcode'],
      ]),
    );
  });
});
