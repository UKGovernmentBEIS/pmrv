import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { CountryService } from '@core/services/country.service';
import { CoordinatePipe } from '@shared/pipes/coordinate.pipe';
import { GovukDatePipe } from '@shared/pipes/govuk-date.pipe';
import { SharedModule } from '@shared/shared.module';
import { BasePage, CountryServiceStub } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { InstallationOperatorDetails, LocationOffShoreDTO, LocationOnShoreDTO } from 'pmrv-api';

import {
  mockOffshore,
  mockOnshore,
  mockPermitApplyPayload,
} from '../../../permit-application/testing/mock-permit-apply-action';
import { InstallationDetailsSummaryComponent } from './installation-details-summary.component';

describe('InstallationDetailsSummaryComponent', () => {
  let component: InstallationDetailsSummaryComponent;
  let fixture: ComponentFixture<InstallationDetailsSummaryComponent>;
  let page: Page;
  let coordinatePipe: CoordinatePipe;

  class Page extends BasePage<InstallationDetailsSummaryComponent> {
    get summaryPairText() {
      return Array.from(this.queryAll<HTMLDivElement>('.govuk-summary-list__row'))
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .filter(([, data]) => !!data)
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  const mock = mockPermitApplyPayload.installationOperatorDetails;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule],
      providers: [
        CoordinatePipe,
        GovukDatePipe,
        KeycloakService,
        { provide: CountryService, useClass: CountryServiceStub },
      ],
    }).compileComponents();
  });

  const createComponent = (installationOperatorDetails?: InstallationOperatorDetails) => {
    fixture = TestBed.createComponent(InstallationDetailsSummaryComponent);
    component = fixture.componentInstance;
    component.installationOperatorDetails = installationOperatorDetails ? installationOperatorDetails : mock;
    page = new Page(fixture);
    coordinatePipe = TestBed.inject(CoordinatePipe);
    fixture.detectChanges();
  };

  beforeEach(() => createComponent());

  afterEach(() => {
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the details for onshore', () => {
    const location = mockOnshore.installationLocation as LocationOnShoreDTO;

    expect(page.summaryPairText).toEqual([
      ['Installation name', mock.installationName],
      ['Site name', mock.siteName],
      [
        'Installation address',
        `${location.gridReference} ${location.address.line1}, ${location.address.line2}${location.address.city}${location.address.postcode}Greece`,
      ],

      ['Operator name', mock.operator],
      ['Legal status', 'Limited Company'],
      ['Companies registration number', mock.companyReferenceNumber],
      [
        'Operator address',
        `${mock.operatorDetailsAddress.line1}, ${mock.operatorDetailsAddress.line2} ${mock.operatorDetailsAddress.city}${mock.operatorDetailsAddress.postcode}Greece`,
      ],
    ]);
  });

  it('should display the details for offshore', () => {
    createComponent(mockOffshore);
    const location = mockOffshore.installationLocation as LocationOffShoreDTO;

    expect(page.summaryPairText).toEqual([
      ['Installation name', mock.installationName],
      ['Site name', mock.siteName],
      [
        'Coordinates',
        `Latitude${coordinatePipe.transform(location.latitude)}Longitude${coordinatePipe.transform(
          location.longitude,
        )}`,
      ],

      ['Operator name', mock.operator],
      ['Legal status', 'Limited Company'],
      ['Companies registration number', mock.companyReferenceNumber],
      [
        'Operator address',
        `${mock.operatorDetailsAddress.line1}, ${mock.operatorDetailsAddress.line2} ${mock.operatorDetailsAddress.city}${mock.operatorDetailsAddress.postcode}Greece`,
      ],
    ]);
  });
});
