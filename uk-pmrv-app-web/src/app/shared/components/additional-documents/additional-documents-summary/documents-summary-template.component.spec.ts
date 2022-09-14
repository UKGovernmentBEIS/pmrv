import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';
import { BasePage } from '@testing';

import { AdditionalDocumentsSummaryTemplateComponent } from './documents-summary-template.component';

describe('DocumentsSummaryTemplateComponent', () => {
  let page: Page;
  let component: AdditionalDocumentsSummaryTemplateComponent;
  let fixture: ComponentFixture<AdditionalDocumentsSummaryTemplateComponent>;

  class Page extends BasePage<AdditionalDocumentsSummaryTemplateComponent> {
    get attachments() {
      return this.queryAll<HTMLLinkElement>('app-summary-download-files > a');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AdditionalDocumentsSummaryTemplateComponent);
    component = fixture.componentInstance;
    component.files = [
      { downloadUrl: '/tasks/384/file-download/f13d4ca2-0581-4bb4-9826-fa5d4201a45c', fileName: 'cover.jpg' },
      {
        downloadUrl: '/tasks/384/file-download/60fe9548-ac65-492a-b057-60033b0fbbed',
        fileName: 'PublicationAgreement.pdf',
      },
    ];
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary details', () => {
    expect(page.attachments).toHaveLength(2);
    expect(page.attachments.map((dd) => dd.textContent)).toEqual(['cover.jpg', 'PublicationAgreement.pdf']);
  });
});
