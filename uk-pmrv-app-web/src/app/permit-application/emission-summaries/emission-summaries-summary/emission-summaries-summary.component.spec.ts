import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '../../../../testing';
import { PermitApplicationModule } from '../../permit-application.module';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockState } from '../../testing/mock-state';
import { EmissionSummariesSummaryComponent } from './emission-summaries-summary.component';

describe('EmissionSummariesSummaryComponent', () => {
  let component: EmissionSummariesSummaryComponent;
  let fixture: ComponentFixture<EmissionSummariesSummaryComponent>;
  let page: Page;
  let store: PermitApplicationStore;

  class Page extends BasePage<EmissionSummariesSummaryComponent> {
    get emissionSummaries() {
      return this.queryAll<HTMLDListElement>('dl').map((emissionSummary) =>
        Array.from(emissionSummary.querySelectorAll('dd')).map((dd) => dd.textContent.trim()),
      );
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
    fixture = TestBed.createComponent(EmissionSummariesSummaryComponent);
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
    expect(page.emissionSummaries).toEqual([
      [
        '13123124 White Spirit & SBP',
        'S1 Boiler  S2 Boiler 2',
        'The big Ref Emission point 1  Yet another reference Point taken!',
        'Combustion',
      ],
    ]);
  });

  it('should display the notification banner', () => {
    expect(page.notificationBanner).toBeTruthy();
  });
});
