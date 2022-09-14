import { HttpResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { RequestTaskAttachmentsHandlingService } from 'pmrv-api';

import { asyncData, BasePage, mockClass } from '../../../../testing';
import { RfiModule } from '../../rfi.module';
import { RfiStore } from '../../store/rfi.store';
import { QuestionsComponent } from './questions.component';

describe('QuestionsComponent', () => {
  let component: QuestionsComponent;
  let fixture: ComponentFixture<QuestionsComponent>;
  let page: Page;
  const attachmentService = mockClass(RequestTaskAttachmentsHandlingService);
  let store: RfiStore;

  class Page extends BasePage<QuestionsComponent> {
    get fieldsets() {
      return this.queryAll<HTMLFieldSetElement>('fieldset');
    }

    get questions() {
      return this.queryAll<HTMLInputElement>('textarea[id^="questions"][id$="question"]');
    }

    get files() {
      return (
        this.queryAll<HTMLSpanElement>('.moj-multi-file-upload__filename') ??
        this.queryAll<HTMLSpanElement>('.moj-multi-file-upload__success')
      );
    }

    get filesText() {
      return this.queryAll<HTMLDivElement>('.moj-multi-file-upload__message');
    }

    set fileValue(value: File[]) {
      this.setInputValue('input[type="file"]', value);
    }

    get fileDeleteButton() {
      return this.queryAll<HTMLButtonElement>('.moj-multi-file-upload__delete');
    }

    set questionValues(values: string[]) {
      values.forEach((value, index) => this.setInputValue(`#questions.${index}.question`, value));
    }

    get addAnotherButton() {
      return this.query<HTMLButtonElement>('button.govuk-button--secondary:not(.moj-add-another__remove-button)');
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }

    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get errors() {
      return this.queryAll<HTMLLIElement>('.govuk-error-summary__list > li');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(QuestionsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  };

  beforeEach(async () => {
    attachmentService.uploadRequestTaskAttachmentUsingPOST.mockReturnValue(
      asyncData<any>(new HttpResponse({ body: { uuid: '60fe9548-ac65-492a-b057-60033b0fbbed' } })),
    );

    await TestBed.configureTestingModule({
      imports: [RfiModule, RouterTestingModule],
      providers: [{ provide: RequestTaskAttachmentsHandlingService, useValue: attachmentService }],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(RfiStore);
    store.setState({
      ...store.getState(),
      requestTaskId: 1,
    });

    fixture = TestBed.createComponent(QuestionsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display an empty form', () => {
    createComponent();

    expect(page.questions).toHaveLength(1);
    expect(page.files).toHaveLength(0);
  });

  it('should require all fields to be populated', () => {
    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeTruthy();
    expect(page.errors.map((error) => error.textContent.trim())).toEqual(['Enter a date', 'Enter a question']);
  });

  it('should add another question', () => {
    page.addAnotherButton.click();
    fixture.detectChanges();

    expect(page.fieldsets).toHaveLength(3);
  });

  it('should submit the form', () => {
    component.form.get('questions').setValue([{ question: 'what' }]);
    component.form.get('deadline').setValue(new Date(2030, 1, 1));

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errors).toHaveLength(0);
  });
});
