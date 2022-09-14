import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { CountryService } from '@core/services/country.service';
import { BasePage, CountryServiceStub } from '@testing';

import { LocationOnShoreDTO } from 'pmrv-api';

import { PermitApplicationModule } from '../permit-application.module';
import { PermitApplicationStore } from '../store/permit-application.store';
import { mockState } from '../testing/mock-state';
import { DetailsComponent } from './details.component';

describe('DetailsComponent', () => {
  let page: Page;
  let component: DetailsComponent;
  let fixture: ComponentFixture<DetailsComponent>;
  let store: PermitApplicationStore;

  class Page extends BasePage<DetailsComponent> {
    get summaryPairText() {
      return Array.from(this.queryAll<HTMLDivElement>('.govuk-summary-list__row'))
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .filter(([, data]) => !!data)
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [{ provide: CountryService, useClass: CountryServiceStub }],
      imports: [PermitApplicationModule, RouterTestingModule],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(PermitApplicationStore);
    store.setState(mockState);

    fixture = TestBed.createComponent(DetailsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display summary', () => {
    const location = mockState.installationOperatorDetails.installationLocation as LocationOnShoreDTO;

    expect(page.summaryPairText).toEqual([
      ['Installation name', mockState.installationOperatorDetails.installationName],
      ['Site name', mockState.installationOperatorDetails.siteName],
      [
        'Installation address',
        `${location.gridReference} ${location.address.line1}, ${location.address.line2}${location.address.city}${location.address.postcode}Greece`,
      ],

      ['Operator name', mockState.installationOperatorDetails.operator],
      ['Legal status', 'Limited Company'],
      ['Companies registration number', mockState.installationOperatorDetails.companyReferenceNumber],
      [
        'Operator address',
        `${mockState.installationOperatorDetails.operatorDetailsAddress.line1}, ${mockState.installationOperatorDetails.operatorDetailsAddress.line2} ${mockState.installationOperatorDetails.operatorDetailsAddress.city}${mockState.installationOperatorDetails.operatorDetailsAddress.postcode}Greece`,
      ],
    ]);
  });
});
