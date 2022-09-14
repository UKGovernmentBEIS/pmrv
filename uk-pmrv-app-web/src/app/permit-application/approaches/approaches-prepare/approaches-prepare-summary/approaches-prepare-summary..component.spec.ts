import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '../../../../../testing';
import { PermitApplicationModule } from '../../../permit-application.module';
import { PermitApplicationStore } from '../../../store/permit-application.store';
import { mockState } from '../../../testing/mock-state';
import { ApproachesPrepareSummaryComponent } from './approaches-prepare-summary.component';

describe('ApproachesPrepareSummaryComponent', () => {
  let component: ApproachesPrepareSummaryComponent;
  let fixture: ComponentFixture<ApproachesPrepareSummaryComponent>;
  let page: Page;

  class Page extends BasePage<ApproachesPrepareSummaryComponent> {
    get summaryListRows() {
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
    }).compileComponents();
  });

  function createComponent() {
    fixture = TestBed.createComponent(ApproachesPrepareSummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  }

  describe('prepare to define monitoring approaches summary', () => {
    beforeEach(() => {
      const store = TestBed.inject(PermitApplicationStore);
      store.setState(mockState);
      const router = TestBed.inject(Router);
      jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { notification: true } } } as any);
    });
    beforeEach(createComponent);

    it('should create component', () => {
      expect(component).toBeTruthy();
    });

    it('should display the read confirmation row', () => {
      expect(page.summaryListRows).toHaveLength(1);
      expect(page.summaryListRows).toEqual([['I have read Preparing to define monitoring approaches', 'Yes']]);
    });

    it('should display the notification banner', () => {
      expect(page.notificationBanner).toBeTruthy();
    });
  });
});
