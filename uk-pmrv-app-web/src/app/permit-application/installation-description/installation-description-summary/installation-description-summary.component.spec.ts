import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { ActivatedRouteStub, BasePage } from '../../../../testing';
import { PermitApplicationModule } from '../../permit-application.module';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockPermitApplyPayload } from '../../testing/mock-permit-apply-action';
import { mockState } from '../../testing/mock-state';
import { InstallationDescriptionSummaryComponent } from './installation-description-summary.component';

describe('InstallationDescriptionSummaryComponent', () => {
  let component: InstallationDescriptionSummaryComponent;
  let fixture: ComponentFixture<InstallationDescriptionSummaryComponent>;
  let page: Page;
  let store: PermitApplicationStore;

  class Page extends BasePage<InstallationDescriptionSummaryComponent> {
    get summaryListValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }

    get notificationBanner() {
      return this.query('.govuk-notification-banner');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PermitApplicationModule, RouterTestingModule],
      providers: [{ provide: ActivatedRoute, useValue: new ActivatedRouteStub({ taskId: '123' }) }],
    }).compileComponents();
  });

  beforeEach(() => {
    const router = TestBed.inject(Router);
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { notification: true } } } as any);
    fixture = TestBed.createComponent(InstallationDescriptionSummaryComponent);
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
    const mockInstallationDescription = mockPermitApplyPayload.permit.installationDescription;

    expect(page.summaryListValues).toEqual([
      ['Activities at the installation', mockInstallationDescription.mainActivitiesDesc],
      ['Description of the site', mockInstallationDescription.siteDescription],
    ]);
  });

  it('should display the notification banner', () => {
    expect(page.notificationBanner).toBeTruthy();
  });
});
