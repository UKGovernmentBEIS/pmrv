import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '../../../testing';
import { SharedModule } from '../../shared/shared.module';
import { OperatorTypePipe } from '../operator-type.pipe';
import { mockedDocumentTemplate } from '../testing/mock-data';
import { DocumentTemplateDetailsTemplateComponent } from './document-template-details-template.component';

describe('DocumentTemplateDetailsTemplateComponent', () => {
  let component: DocumentTemplateDetailsTemplateComponent;
  let fixture: ComponentFixture<TestComponent>;
  let page: Page;

  @Component({
    template: `<app-document-template-details-template
      [documentTemplate]="documentTemplate"
    ></app-document-template-details-template>`,
  })
  class TestComponent {
    documentTemplate = mockedDocumentTemplate;
  }

  class Page extends BasePage<TestComponent> {
    get details() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [DocumentTemplateDetailsTemplateComponent, OperatorTypePipe, TestComponent],
      imports: [RouterTestingModule, SharedModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    component = fixture.debugElement.query(By.directive(DocumentTemplateDetailsTemplateComponent)).componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the document template details', () => {
    expect(page.details).toEqual([
      ['Emails sending this document', 'View Email Template template'],
      ['Operator', 'Installations'],
      ['Workflow', 'PMRV workflow'],
      ['Last changed', '3 Mar 2022'],
    ]);
  });
});
