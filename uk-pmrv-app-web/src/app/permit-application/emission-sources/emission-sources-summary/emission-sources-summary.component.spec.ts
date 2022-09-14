import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '../../../../testing';
import { PermitApplicationModule } from '../../permit-application.module';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockState } from '../../testing/mock-state';
import { EmissionSourcesSummaryComponent } from './emission-sources-summary.component';

describe('EmissionSourcesSummaryComponent', () => {
  let component: EmissionSourcesSummaryComponent;
  let fixture: ComponentFixture<EmissionSourcesSummaryComponent>;
  let page: Page;
  let store: PermitApplicationStore;

  class Page extends BasePage<EmissionSourcesSummaryComponent> {
    get emissionSources() {
      return this.queryAll<HTMLDListElement>('dl').map((emisisonSource) =>
        Array.from(emisisonSource.querySelectorAll('dd')).map((dd) => dd.textContent.trim()),
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
    fixture = TestBed.createComponent(EmissionSourcesSummaryComponent);
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
    expect(page.emissionSources).toEqual([['S1 Boiler'], ['S2 Boiler 2']]);
  });

  it('should display the notification banner', () => {
    expect(page.notificationBanner).toBeTruthy();
  });
});
