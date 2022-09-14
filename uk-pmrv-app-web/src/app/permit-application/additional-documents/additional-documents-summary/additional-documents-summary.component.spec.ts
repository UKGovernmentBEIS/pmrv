import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';
import { BasePage } from '@testing';

import { ListReturnLinkComponent } from '../../shared/list-return-link/list-return-link.component';
import { PermitTaskComponent } from '../../shared/permit-task/permit-task.component';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockState, mockStateBuild } from '../../testing/mock-state';
import { AdditionalDocumentsSummaryComponent } from './additional-documents-summary.component';

describe('AdditionalDocumentsSummaryComponent', () => {
  let component: AdditionalDocumentsSummaryComponent;
  let fixture: ComponentFixture<AdditionalDocumentsSummaryComponent>;
  let page: Page;
  let store: PermitApplicationStore;

  class Page extends BasePage<AdditionalDocumentsSummaryComponent> {
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
      imports: [SharedModule, RouterTestingModule],
      declarations: [AdditionalDocumentsSummaryComponent, PermitTaskComponent, ListReturnLinkComponent],
    }).compileComponents();
  });

  function createComponent() {
    fixture = TestBed.createComponent(AdditionalDocumentsSummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  }

  beforeEach(() => {
    const router = TestBed.inject(Router);
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { notification: true } } } as any);
    store = TestBed.inject(PermitApplicationStore);
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
