import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '../../../../testing';
import { PermitApplicationModule } from '../../permit-application.module';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockPermitApplyPayload } from '../../testing/mock-permit-apply-action';
import { mockState, mockStateBuild } from '../../testing/mock-state';
import { OtherPermitsSummaryComponent } from './other-permits-summary.component';

describe('OtherPermitsSummaryComponent', () => {
  let component: OtherPermitsSummaryComponent;
  let fixture: ComponentFixture<OtherPermitsSummaryComponent>;
  let page: Page;
  let store: PermitApplicationStore;

  class Page extends BasePage<OtherPermitsSummaryComponent> {
    get permits() {
      return this.queryAll<HTMLDListElement>('dl');
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

  function createComponent() {
    const router = TestBed.inject(Router);
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { notification: true } } } as any);

    fixture = TestBed.createComponent(OtherPermitsSummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  }

  describe('other permits exist', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(mockState);
    });

    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should list the other permits and licences', () => {
      expect(page.permits).toHaveLength(2);
      expect(
        page.permits.map((permit) => Array.from(permit.querySelectorAll('dd')).map((dd) => dd.textContent)),
      ).toEqual([
        ['Environmental Protection Regulations', '1234', 'FWW', 'GovUK'],
        ['Seaside Regulations', '1253', 'Peacegreen', 'GovUK'],
      ]);
    });

    it('should display the notification banner', () => {
      expect(page.notificationBanner).toBeTruthy();
    });
  });

  describe('other permits not exist', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);

      store.setState(
        mockStateBuild({ ...mockPermitApplyPayload.permit, environmentalPermitsAndLicences: { exist: false } }),
      );
    });

    beforeEach(createComponent);

    it('should mention that no other permits exist', () => {
      expect(page.permits).toHaveLength(1);
      expect(
        page.permits.map((permit) => Array.from(permit.querySelectorAll('dd')).map((dd) => dd.textContent)),
      ).toEqual([['No']]);
    });
  });
});
