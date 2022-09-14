import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { DocumentTemplatesService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../../testing';
import { SharedModule } from '../../../shared/shared.module';
import { OperatorTypePipe } from '../../operator-type.pipe';
import { mockedDocumentTemplate } from '../../testing/mock-data';
import { DocumentTemplateDetailsTemplateComponent } from '../document-template-details-template.component';
import { DocumentTemplateComponent } from './document-template.component';

describe('DocumentTemplateComponent', () => {
  let component: DocumentTemplateComponent;
  let fixture: ComponentFixture<DocumentTemplateComponent>;
  let page: Page;
  let activatedRoute: ActivatedRouteStub;

  const documentTemplatesService = mockClass(DocumentTemplatesService);

  class Page extends BasePage<DocumentTemplateComponent> {
    get title() {
      return this.query<HTMLDivElement>('.govuk-heading-l');
    }
    get file() {
      return (
        this.query<HTMLSpanElement>('.moj-multi-file-upload__filename') ??
        this.query<HTMLSpanElement>('.moj-multi-file-upload__success')
      );
    }
    set fileValue(value: File) {
      this.setInputValue('input[type="file"]', value);
    }
    get deleteFileBtn() {
      return this.query<HTMLButtonElement>('button[type="button"]');
    }
    get submitBtn() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }
    get errors() {
      return this.queryAll<HTMLAnchorElement>('.govuk-error-summary li a');
    }
  }

  beforeEach(async () => {
    activatedRoute = new ActivatedRouteStub(undefined, undefined, {
      documentTemplate: mockedDocumentTemplate,
    });
    documentTemplatesService.updateDocumentTemplateUsingPOST.mockReturnValue(of(null));

    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule],
      declarations: [DocumentTemplateComponent, DocumentTemplateDetailsTemplateComponent, OperatorTypePipe],
      providers: [
        { provide: ActivatedRoute, useValue: activatedRoute },
        { provide: DocumentTemplatesService, useValue: documentTemplatesService },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DocumentTemplateComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should succesfully upload and submit new document file', async () => {
    expect(page.title.textContent).toEqual(`Edit ${mockedDocumentTemplate.name}`);
    expect(page.file.textContent.trim()).toEqual(mockedDocumentTemplate.filename);

    page.deleteFileBtn.click();
    fixture.detectChanges();
    expect(page.file).toBeNull();

    page.submitBtn.click();
    fixture.detectChanges();
    expect(page.errorSummary).toBeTruthy();
    expect(page.errors.map((error) => error.textContent.trim())).toEqual(['Select a file']);

    const filename = 'AnotherFile.docx';
    const file = new File(['file content'], filename);
    page.fileValue = file;
    fixture.detectChanges();
    await fixture.whenStable();
    fixture.detectChanges();

    page.submitBtn.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeFalsy();
    expect(documentTemplatesService.updateDocumentTemplateUsingPOST).toHaveBeenCalledTimes(1);
    expect(documentTemplatesService.updateDocumentTemplateUsingPOST).toHaveBeenCalledWith(
      mockedDocumentTemplate.id,
      file,
    );
  });
});
