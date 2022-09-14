import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '../../../../testing';
import { PermitApplicationModule } from '../../permit-application.module';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockState } from '../../testing/mock-state';
import { RegulatedActivitiesSummaryComponent } from './regulated-activities-summary.component';

describe('RegulatedActivitiesSummaryComponent', () => {
  let component: RegulatedActivitiesSummaryComponent;
  let fixture: ComponentFixture<RegulatedActivitiesSummaryComponent>;
  let page: Page;
  let store: PermitApplicationStore;

  class Page extends BasePage<RegulatedActivitiesSummaryComponent> {
    get regulatedActivities() {
      return this.queryAll<HTMLTableRowElement>('tbody tr')
        .map((row) => Array.from(row.querySelectorAll('td')))
        .map((row) => row.map((cell) => cell.textContent.trim()));
    }

    get notificationBanner() {
      return this.query('.govuk-notification-banner');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PermitApplicationModule, RouterTestingModule],
    }).compileComponents();
  });

  beforeEach(() => {
    const router = TestBed.inject(Router);
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { notification: true } } } as any);

    fixture = TestBed.createComponent(RegulatedActivitiesSummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
  });

  beforeEach(() => {
    store = TestBed.inject(PermitApplicationStore);
    store.setState(mockState);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the list of data', () => {
    expect(page.regulatedActivities).toEqual([
      ['Combustion', '34,344 MW(th)', 'Carbon dioxide'],
      ['Mineral oil refining', '34,345 MW', 'Carbon dioxide'],
    ]);
  });

  it('should display the notification banner', () => {
    expect(page.notificationBanner).toBeTruthy();
  });
});
