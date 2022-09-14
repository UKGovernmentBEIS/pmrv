import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { CountryService } from '@core/services/country.service';
import { SharedModule } from '@shared/shared.module';
import { mockAerApplyPayload, mockState } from '@tasks/aer/submit/testing/mock-permit-apply-action';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage, CountryServiceStub } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { LocationOnShoreDTO } from 'pmrv-api';

import { AerModule } from '../../aer.module';
import { InstallationDetailsComponent } from './installation-details.component';

describe('InstallationDetailsComponent', () => {
  let page: Page;
  let component: InstallationDetailsComponent;
  let fixture: ComponentFixture<InstallationDetailsComponent>;
  let store: CommonTasksStore;

  class Page extends BasePage<InstallationDetailsComponent> {
    get summaryPairText() {
      return Array.from(this.queryAll<HTMLDivElement>('.govuk-summary-list__row'))
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .filter(([, data]) => !!data)
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [KeycloakService, { provide: CountryService, useClass: CountryServiceStub }],
      imports: [SharedModule, RouterTestingModule, AerModule],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonTasksStore);
    store.setState(mockState);

    fixture = TestBed.createComponent(InstallationDetailsComponent);
    component = fixture.componentInstance;
    component.installationOperatorDetails$ = of(mockAerApplyPayload.installationOperatorDetails);
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display summary', () => {
    const location = mockAerApplyPayload.installationOperatorDetails.installationLocation as LocationOnShoreDTO;

    expect(page.summaryPairText).toEqual([
      ['Installation name', mockAerApplyPayload.installationOperatorDetails.installationName],
      ['Site name', mockAerApplyPayload.installationOperatorDetails.siteName],
      [
        'Installation address',
        `${location.gridReference} ${location.address.line1}, ${location.address.line2}${location.address.city}${location.address.postcode}Greece`,
      ],

      ['Operator name', mockAerApplyPayload.installationOperatorDetails.operator],
      ['Legal status', 'Limited Company'],
      ['Companies registration number', mockAerApplyPayload.installationOperatorDetails.companyReferenceNumber],
      [
        'Operator address',
        `${mockAerApplyPayload.installationOperatorDetails.operatorDetailsAddress.line1}, ${mockAerApplyPayload.installationOperatorDetails.operatorDetailsAddress.line2} ${mockAerApplyPayload.installationOperatorDetails.operatorDetailsAddress.city}${mockAerApplyPayload.installationOperatorDetails.operatorDetailsAddress.postcode}Greece`,
      ],
    ]);
  });
});
