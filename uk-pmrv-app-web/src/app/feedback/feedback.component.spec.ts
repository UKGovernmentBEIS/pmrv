import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { UsersService } from 'pmrv-api';

import { BasePage, MockType } from '../../testing';
import { SharedModule } from '../shared/shared.module';
import { FeedbackComponent } from './feedback.component';

describe('FeedbackComponent', () => {
  let component: FeedbackComponent;
  let fixture: ComponentFixture<FeedbackComponent>;
  let hostElement: HTMLElement;

  const usersService: MockType<UsersService> = {
    provideUserFeedbackUsingPOST: jest.fn().mockReturnValue(of(null)),
  };
  let page: Page;

  class Page extends BasePage<FeedbackComponent> {
    get userRegistrationRateRadios() {
      return this.queryAll<HTMLInputElement>('input[name$="userRegistrationRate"]');
    }
    get userRegistrationRateReason() {
      return this.getInputValue('#userRegistrationRateReason');
    }
    set userRegistrationRateReason(value: string) {
      this.setInputValue('#userRegistrationRateReason', value);
    }

    get onlineGuidanceRateRadios() {
      return this.queryAll<HTMLInputElement>('input[name$="onlineGuidanceRate"]');
    }
    get onlineGuidanceRateReason() {
      return this.getInputValue('#onlineGuidanceRateReason');
    }
    set onlineGuidanceRateReason(value: string) {
      this.setInputValue('#onlineGuidanceRateReason', value);
    }

    get creatingAccountRateRadios() {
      return this.queryAll<HTMLInputElement>('input[name$="creatingAccountRate"]');
    }
    get creatingAccountRateReason() {
      return this.getInputValue('#creatingAccountRateReason');
    }
    set creatingAccountRateReason(value: string) {
      this.setInputValue('#creatingAccountRateReason', value);
    }

    get onBoardingRateRadios() {
      return this.queryAll<HTMLInputElement>('input[name$="onBoardingRate"]');
    }
    get onBoardingRateReason() {
      return this.getInputValue('#onBoardingRateReason');
    }
    set onBoardingRateReason(value: string) {
      this.setInputValue('#onBoardingRateReason', value);
    }

    get tasksRateRadios() {
      return this.queryAll<HTMLInputElement>('input[name$="tasksRate"]');
    }
    get tasksRateReason() {
      return this.getInputValue('#tasksRateReason');
    }
    set tasksRateReason(value: string) {
      this.setInputValue('#tasksRateReason', value);
    }

    get satisfactionRateRadios() {
      return this.queryAll<HTMLInputElement>('input[name$="satisfactionRate"]');
    }
    get satisfactionRateReason() {
      return this.getInputValue('#satisfactionRateReason');
    }
    set satisfactionRateReason(value: string) {
      this.setInputValue('#satisfactionRateReason', value);
    }

    get improvementSuggestion() {
      return this.getInputValue('#improvementSuggestion');
    }
    set improvementSuggestion(value: string) {
      this.setInputValue('#improvementSuggestion', value);
    }

    get submitButton(): HTMLButtonElement {
      return this.query('button[type="submit"]');
    }

    get errorSummary(): HTMLDivElement {
      return this.query('.govuk-error-summary');
    }

    get errorSummaryErrorList() {
      return Array.from(this.query<HTMLDivElement>('.govuk-error-summary').querySelectorAll('a')).map((anchor) =>
        anchor.textContent.trim(),
      );
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [FeedbackComponent],
      imports: [RouterTestingModule, SharedModule],
      providers: [{ provide: UsersService, useValue: usersService }],
    }).compileComponents();
  });

  afterEach(() => jest.clearAllMocks());

  beforeEach(() => {
    fixture = TestBed.createComponent(FeedbackComponent);
    component = fixture.componentInstance;
    hostElement = fixture.nativeElement;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should send feedback form', () => {
    expect(page.userRegistrationRateRadios.length).toEqual(5);
    expect(page.onlineGuidanceRateRadios.length).toEqual(6);
    expect(page.creatingAccountRateRadios.length).toEqual(6);
    expect(page.onBoardingRateRadios.length).toEqual(6);
    expect(page.tasksRateRadios.length).toEqual(6);
    expect(page.satisfactionRateRadios.length).toEqual(5);

    expect(page.errorSummary).toBeFalsy();

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeTruthy();
    expect(page.errorSummaryErrorList).toEqual([
      'Select a rating for user registration',
      'Select a rating for online guidance and communication',
      'Select a rating for claiming an account',
      'Select a rating for on-boarding, identity and security checks',
      'Select a rating for viewing, searching and responding to tasks',
      'Select a rating for overall experience with this service so far',
    ]);
    expect(hostElement.textContent).not.toContain('Feedback sent');

    page.userRegistrationRateRadios[4].click();
    page.userRegistrationRateReason = 'bad UX';

    page.onlineGuidanceRateRadios[5].click();
    page.onlineGuidanceRateReason = 'no idea';

    page.creatingAccountRateRadios[5].click();
    page.onBoardingRateRadios[5].click();
    page.tasksRateRadios[5].click();
    page.satisfactionRateRadios[4].click();

    page.improvementSuggestion = 'fix UX';

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeFalsy();
    expect(usersService.provideUserFeedbackUsingPOST).toHaveBeenCalledTimes(1);
    expect(usersService.provideUserFeedbackUsingPOST).toHaveBeenCalledWith({
      userRegistrationRate: 'VERY_DISSATISFIED',
      userRegistrationRateReason: 'bad UX',

      onlineGuidanceRate: 'NOT_APPLICABLE_NOT_USED_YET',
      onlineGuidanceRateReason: 'no idea',

      creatingAccountRate: 'NOT_APPLICABLE_NOT_USED_YET',
      creatingAccountRateReason: null,

      onBoardingRate: 'NOT_APPLICABLE_NOT_USED_YET',
      onBoardingRateReason: null,

      tasksRate: 'NOT_APPLICABLE_NOT_USED_YET',
      tasksRateReason: null,

      satisfactionRate: 'VERY_DISSATISFIED',
      satisfactionRateReason: null,

      improvementSuggestion: 'fix UX',
    });
    expect(hostElement.textContent).toContain('Feedback sent');
  });
});
