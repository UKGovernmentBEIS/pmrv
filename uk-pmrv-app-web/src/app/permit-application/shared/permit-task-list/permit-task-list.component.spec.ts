import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { Observable, of } from 'rxjs';

import { ActivatedRouteStub, BasePage } from '@testing';

import { ItemDTO, RequestActionInfoDTO } from 'pmrv-api';

import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockPermitCompletePayload } from '../../testing/mock-permit-apply-action';
import { mockState } from '../../testing/mock-state';
import { SharedPermitModule } from '../shared-permit.module';
import { PermitTaskListComponent } from './permit-task-list.component';

describe('PermitTaskListComponent', () => {
  let component: PermitTaskListComponent;
  let testComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let hostElement: HTMLElement;
  let page: Page;

  let permitApplicationStore: PermitApplicationStore;

  let route: ActivatedRouteStub;

  const mockParams = {
    taskId: 237,
  };

  @Component({
    template: `<app-permit-task-list
      [actions$]="actions$"
      [relatedTasks$]="relatedTasks$"
      [isHeadingVisible]="isHeadingVisible"
    ></app-permit-task-list>`,
  })
  class TestComponent {
    actions$: Observable<RequestActionInfoDTO[]> = of([
      {
        id: 1,
        creationDate: '2020-08-25 10:36:15.189643',
        submitter: 'Operator',
        type: 'INSTALLATION_ACCOUNT_OPENING_APPLICATION_SUBMITTED',
      },
      {
        id: 2,
        creationDate: '2020-08-26 10:36:15.189643',
        submitter: 'Regulator',
        type: 'INSTALLATION_ACCOUNT_OPENING_ACCEPTED',
      },
    ]);

    relatedTasks$: Observable<ItemDTO[]> = of([
      {
        requestType: 'PERMIT_ISSUANCE',
        taskType: 'PERMIT_ISSUANCE_WAIT_FOR_RFI_RESPONSE',
        taskId: 1,
      },
    ]);
    isHeadingVisible = true;
  }

  class Page extends BasePage<TestComponent> {
    get relatedActionsLinks() {
      return this.queryAll<HTMLLinkElement>('aside li a');
    }

    get sections(): HTMLOListElement[] {
      return Array.from(this.queryAll<HTMLOListElement>('ol > li'));
    }

    get monitoringApproachesSection(): HTMLOListElement {
      return this.sections.find(
        (section) => section.querySelector<HTMLHeadingElement>('h2').textContent.trim() === 'Monitoring approaches',
      );
    }

    get monitoringApproachesTasks(): HTMLLIElement[] {
      return Array.from(this.monitoringApproachesSection.querySelectorAll<HTMLLIElement>('li'));
    }

    get additionalInformationSection(): HTMLOListElement {
      return this.sections.find(
        (section) => section.querySelector<HTMLHeadingElement>('h2').textContent.trim() === 'Additional information',
      );
    }

    get additionalInformationTasks(): HTMLLIElement[] {
      return Array.from(this.additionalInformationSection.querySelectorAll<HTMLLIElement>('li'));
    }

    get notificationBanner() {
      return this.query('.govuk-notification-banner');
    }

    get formSubmissionPageTitle() {
      return this.query('.govuk-panel__title')?.textContent + ' ' + this.query('.govuk-panel__body')?.textContent;
    }

    get pageHeading() {
      return this.query('.govuk-heading-xl')?.textContent;
    }

    get headings() {
      return this.queryAll('.govuk-heading-m')?.map((el) => el.textContent);
    }
    get relatedTasks() {
      return this.queryAll('app-related-tasks .govuk-heading-m')?.map((el) => el.textContent);
    }

    get headingInfo() {
      return this.queryAll<HTMLParagraphElement>('.govuk-heading-xl p');
    }
  }

  beforeEach(async () => {
    route = new ActivatedRouteStub(mockParams, null, null, null, null);
    await TestBed.configureTestingModule({
      declarations: [TestComponent],
      imports: [RouterTestingModule, SharedPermitModule],
      providers: [{ provide: ActivatedRoute, useValue: route }],
    }).compileComponents();

    permitApplicationStore = TestBed.inject(PermitApplicationStore);
  });

  const createComponent = () => {
    permitApplicationStore.setState({
      ...mockState,
      requestTaskType: 'PERMIT_ISSUANCE_APPLICATION_SUBMIT',
      isRequestTask: true,
      isVariation: false,
    });

    fixture = TestBed.createComponent(TestComponent);
    testComponent = fixture.componentInstance;
    component = fixture.debugElement.query(By.directive(PermitTaskListComponent)).componentInstance;
    page = new Page(fixture);
    hostElement = fixture.nativeElement;
    fixture.detectChanges();
  };

  describe('submit permit application', () => {
    beforeEach(() => createComponent());

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it(`should not display cancel submit button`, () => {
      fixture.detectChanges();
      expect(Array.from(hostElement.querySelectorAll('a')).filter((anchor) => anchor.textContent === 'Cancel')).toEqual(
        [],
      );
    });

    it('should render the monitoring approaches tasks if defined', () => {
      permitApplicationStore.setState({
        ...permitApplicationStore.getState(),
        permit: {
          ...permitApplicationStore.getState().permit,
          monitoringApproaches: {
            INHERENT_CO2: { type: 'INHERENT_CO2' },
          },
        },
      });
      fixture.detectChanges();

      expect(page.monitoringApproachesTasks.length).toEqual(4);

      permitApplicationStore.setState({
        ...permitApplicationStore.getState(),
        permit: {
          ...permitApplicationStore.getState().permit,
          monitoringApproaches: {
            INHERENT_CO2: { type: 'INHERENT_CO2' },
            N2O: { type: 'N2O' },
          },
        },
      });

      fixture.detectChanges();

      expect(page.monitoringApproachesTasks.length).toEqual(5);
      expect(page.monitoringApproachesTasks[2].querySelector('a').textContent.trim()).toEqual(
        'Nitrous oxide (N2O) approach',
      );
      expect(page.monitoringApproachesTasks[3].querySelector('a').textContent.trim()).toEqual('Inherent CO2');
    });

    it('should display related action links', () => {
      expect(page.relatedActionsLinks.map((el) => [el.href, el.textContent])).toEqual([
        ['http://localhost/tasks/237/change-assignee', 'Reassign task'],
      ]);
    });

    it('should show heading info', () => {
      expect(page.headingInfo.map((el) => el.textContent.trim())).toEqual(['Assigned to: John Doe']);
    });
  });

  describe('amend permit application', () => {
    beforeEach(() => createComponent());

    beforeEach(() => {
      permitApplicationStore.setState({
        ...permitApplicationStore.getState(),
        daysRemaining: 13,
        requestId: '1',
        permit: {
          ...permitApplicationStore.getState().permit,
          monitoringApproaches: {
            INHERENT_CO2: { type: 'INHERENT_CO2' },
          },
        },
        requestTaskType: 'PERMIT_ISSUANCE_APPLICATION_AMENDS_SUBMIT',
        reviewGroupDecisions: {
          ADDITIONAL_INFORMATION: {
            type: 'OPERATOR_AMENDS_NEEDED',
            changesRequired: 'changes required',
            notes: 'notes',
          },
          CALCULATION: {
            type: 'OPERATOR_AMENDS_NEEDED',
            changesRequired: 'changes required',
            notes: 'notes',
          },
        },
      });
      fixture.detectChanges();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should render notification banner and the appropriate amend tasks', () => {
      expect(page.notificationBanner).toBeTruthy();

      expect(page.monitoringApproachesTasks.length).toEqual(5);
      expect(page.monitoringApproachesTasks[0].querySelector('a').textContent.trim()).toEqual(
        'Amends needed for monitoring approaches',
      );

      expect(page.additionalInformationTasks.length).toEqual(3);
      expect(page.additionalInformationTasks[0].querySelector('a').textContent.trim()).toEqual(
        'Amends needed for additional information',
      );
    });

    it('should display related action links', () => {
      expect(page.relatedActionsLinks.map((el) => [el.href, el.textContent])).toEqual([
        ['http://localhost/tasks/237/change-assignee', 'Reassign task'],
      ]);
    });

    it('should show heading info', () => {
      expect(page.headingInfo.map((el) => el.textContent.trim())).toEqual([
        'Assigned to: John Doe',
        'Days Remaining: 13',
      ]);
    });

    it('should show all headings', () => {
      expect(page.headings).toEqual(['Related tasks', 'Timeline', 'Related actions']);
    });

    it('should show related tasks', () => {
      expect(page.relatedTasks).toEqual(['Related tasks']);
    });
  });

  describe('permit variation', () => {
    beforeEach(() => createComponent());

    beforeEach(() => {
      permitApplicationStore.setState({
        ...permitApplicationStore.getState(),

        payloadType: 'PERMIT_VARIATION_APPLICATION_SUBMIT_PAYLOAD',
        requestTaskType: 'PERMIT_VARIATION_OPERATOR_APPLICATION_SUBMIT',
        isVariation: true,
      });
      testComponent.isHeadingVisible = false;
      fixture.detectChanges();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display variation heading', () => {
      expect(page.pageHeading).toEqual('');
    });

    it('should display variation details section', () => {
      const variationHeadingElement = page.sections.find(
        (section) => section.querySelector<HTMLHeadingElement>('h2').textContent.trim() === 'Variation',
      );
      expect(variationHeadingElement).toBeTruthy();

      expect(variationHeadingElement.querySelector<HTMLLIElement>('li a').textContent.trim()).toEqual(
        'About the variation',
      );
    });

    it('should display variation submit section', () => {
      expect(
        page.sections[page.sections.length - 1]
          .querySelector<HTMLLIElement>('li span.app-task-list__task-name')
          .textContent.trim(),
      ).toEqual('Send to the regulator');
    });

    it('should show cannot started yet when in submit section when permit is not completed', () => {
      expect(
        page.sections[page.sections.length - 1]
          .querySelector<HTMLLIElement>('li govuk-tag.app-task-list__tag')
          .textContent.trim(),
      ).toEqual('cannot start yet');
    });

    it('should show not started in submit section when permit is completed', () => {
      permitApplicationStore.setState({
        ...permitApplicationStore.getState(),
        permit: mockPermitCompletePayload.permit,
        permitSectionsCompleted: {
          ...mockPermitCompletePayload.permitSectionsCompleted,
        },
        permitVariationDetailsCompleted: true,
      });
      fixture.detectChanges();

      expect(
        page.sections[page.sections.length - 1]
          .querySelector<HTMLLIElement>('li govuk-tag.app-task-list__tag')
          .textContent.trim(),
      ).toEqual('not started');
    });

    it('should display related action links', () => {
      expect(page.relatedActionsLinks.map((el) => [el.href, el.textContent])).toEqual([
        ['http://localhost/tasks/237/change-assignee', 'Reassign task'],
      ]);
    });
  });
});
