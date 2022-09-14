import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '../../../../testing';
import { PermitApplicationModule } from '../../permit-application.module';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockState } from '../../testing/mock-state';
import { ApproachesSummaryComponent } from './approaches-summary.component';

describe('ApproachesSummaryComponent', () => {
  let component: ApproachesSummaryComponent;
  let fixture: ComponentFixture<ApproachesSummaryComponent>;
  let page: Page;
  let store: PermitApplicationStore;

  class Page extends BasePage<ApproachesSummaryComponent> {
    get monitoringApproaches() {
      return this.queryAll('dt').map((dd) => dd.textContent.trim());
    }

    get notificationBanner() {
      return this.query('.govuk-notification-banner');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, PermitApplicationModule],
    }).compileComponents();
  });

  beforeEach(() => {
    const router = TestBed.inject(Router);
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { notification: true } } } as any);
    fixture = TestBed.createComponent(ApproachesSummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    store = TestBed.inject(PermitApplicationStore);
    store.setState(mockState);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the list of data', () => {
    expect(page.monitoringApproaches).toEqual([
      'Calculation approach',
      'Measurement approach',
      'Fall-back approach',
      'Nitrous oxide (N2O) approach',
      'Perfluorocarbons (PFC) approach',
      'Inherent CO2 approach',
      'Transferred CO2 approach',
    ]);
  });

  it('should display the notification banner', () => {
    expect(page.notificationBanner).toBeTruthy();
  });
});
