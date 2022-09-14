import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { AerModule } from '@tasks/aer/aer.module';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { mockState } from '../../testing/mock-permit-apply-action';
import { mockStateBuild } from '../../testing/mock-state';
import { SummaryComponent } from './summary.component';

describe('SummaryComponent', () => {
  let component: SummaryComponent;
  let fixture: ComponentFixture<SummaryComponent>;
  let page: Page;
  let store: CommonTasksStore;

  class Page extends BasePage<SummaryComponent> {
    get attachments() {
      return this.queryAll<HTMLLinkElement>('app-summary-download-files > a');
    }
    get summaryValues() {
      return this.queryAll<HTMLDListElement>('dd');
    }
    get summaryHeader() {
      return this.query<HTMLElement>('h2 span');
    }
    get notificationBanner() {
      return this.query('.govuk-notification-banner');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AerModule, RouterTestingModule],
      providers: [KeycloakService],
    }).compileComponents();
  });

  function createComponent() {
    fixture = TestBed.createComponent(SummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  }

  beforeEach(() => {
    const router = TestBed.inject(Router);
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { notification: true } } } as any);
    store = TestBed.inject(CommonTasksStore);
    store.setState(mockState);
  });

  beforeEach(createComponent);

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should list the file names', () => {
    expect(page.attachments).toHaveLength(2);
    expect(page.attachments.map((dd) => dd.textContent)).toEqual(['cover.jpg', 'PublicationAgreement.pdf']);
    expect(page.summaryHeader.textContent.trim()).toEqual('Uploaded additional documents and information');
    expect(page.summaryHeader.classList).not.toContain('govuk-visually-hidden');
    expect(page.notificationBanner).toBeTruthy();
  });

  it('should list the no value instead of file names', async () => {
    store.setState(mockStateBuild({ additionalDocuments: { exist: false } }));
    createComponent();

    expect(page.summaryValues.map((dd) => dd.textContent)).toEqual(['No']);
    expect(page.summaryHeader.classList).toContain('govuk-visually-hidden');
  });
});
