import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { NotificationTemplatesService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../../testing';
import { SharedModule } from '../../../shared/shared.module';
import { OperatorTypePipe } from '../../operator-type.pipe';
import { mockedEmailTemplate } from '../../testing/mock-data';
import { EmailTemplateDetailsTemplateComponent } from '../email-template-details-template.component';
import { EmailTemplateComponent } from './email-template.component';

describe('EmailTemplateComponent', () => {
  let component: EmailTemplateComponent;
  let fixture: ComponentFixture<EmailTemplateComponent>;
  let page: Page;
  let activatedRoute: ActivatedRouteStub;

  const notificationTemplatesService = mockClass(NotificationTemplatesService);

  class Page extends BasePage<EmailTemplateComponent> {
    get title() {
      return this.query<HTMLDivElement>('.govuk-heading-l');
    }
    get templateSubjectValue() {
      return this.getInputValue('#subject');
    }
    set templateSubjectValue(value: string) {
      this.setInputValue('#subject', value);
    }
    get templateMessageValue() {
      return this.getInputValue('#message');
    }
    set templateMessageValue(value: string) {
      this.setInputValue('#message', value);
    }
    get submitBtn() {
      return this.query<HTMLButtonElement>('button');
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
      emailTemplate: mockedEmailTemplate,
    });
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule],
      declarations: [EmailTemplateComponent, EmailTemplateDetailsTemplateComponent, OperatorTypePipe],
      providers: [
        { provide: ActivatedRoute, useValue: activatedRoute },
        { provide: NotificationTemplatesService, useValue: notificationTemplatesService },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EmailTemplateComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should succesfully submit changes in email content', () => {
    expect(page.title.textContent).toEqual(`Edit ${mockedEmailTemplate.name}`);
    expect(page.templateSubjectValue).toEqual(mockedEmailTemplate.subject);
    expect(page.templateMessageValue).toEqual(mockedEmailTemplate.text);

    page.templateSubjectValue = '';
    page.templateMessageValue = '';
    page.submitBtn.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeTruthy();
    expect(page.errors.map((error) => error.textContent.trim())).toEqual([
      'Enter an email subject',
      'Enter an email message',
    ]);

    const updatedSubject = 'Updated email template subject';
    const updatedMessage = 'Updated email template message';
    page.templateSubjectValue = updatedSubject;
    page.templateMessageValue = updatedMessage;

    page.submitBtn.click();
    fixture.detectChanges();
    expect(page.errorSummary).toBeFalsy();

    expect(notificationTemplatesService.updateNotificationTemplateUsingPUT).toHaveBeenCalledTimes(1);
    expect(notificationTemplatesService.updateNotificationTemplateUsingPUT).toHaveBeenCalledWith(
      mockedEmailTemplate.id,
      {
        subject: updatedSubject,
        text: updatedMessage,
      },
    );
  });
});
