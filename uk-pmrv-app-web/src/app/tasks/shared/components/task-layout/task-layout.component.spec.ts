import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';
import { BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { ItemDTO, RequestActionInfoDTO, RequestTaskItemDTO } from 'pmrv-api';

import { TaskSharedModule } from '../../task-shared-module';

describe('TaskLayoutComponent', () => {
  let page: Page;
  let hostComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;

  @Component({
    template: `
      <app-task-layout
        [header]="header"
        [requestTaskItem]="requestTaskItem"
        [relatedTasks]="relatedTasks"
        [timelineActions]="timelineActions"
      >
      </app-task-layout>
    `,
  })
  class TestComponent {
    header = 'Notification determination';
    requestTaskItem: RequestTaskItemDTO = {
      allowedRequestTaskActions: ['RFI_SUBMIT'],
      requestTask: {
        id: 1,
        type: 'PERMIT_NOTIFICATION_APPLICATION_REVIEW',
        assignable: true,
        daysRemaining: 13,
        assigneeFullName: 'John Doe',
      },
    };
    relatedTasks: ItemDTO[] = [
      {
        requestType: 'PERMIT_ISSUANCE',
        taskType: 'PERMIT_ISSUANCE_WAIT_FOR_AMENDS',
        taskId: 1,
        daysRemaining: 13,
      },
      {
        requestType: 'PERMIT_ISSUANCE',
        taskType: 'PERMIT_ISSUANCE_APPLICATION_AMENDS_SUBMIT',
        taskId: 2,
        daysRemaining: 14,
      },
    ];
    timelineActions: RequestActionInfoDTO[] = [
      {
        type: 'PERMIT_NOTIFICATION_APPLICATION_SUBMITTED',
        submitter: 'User',
      },
    ];
  }

  class Page extends BasePage<TestComponent> {
    get heading(): string {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }
    get relatedTasks() {
      return this.queryAll('app-related-tasks .govuk-heading-s')?.map((el) => el.textContent);
    }
    get TimelineTasks() {
      return this.queryAll('app-timeline-item .govuk-heading-s')?.map((el) => el.textContent);
    }
    get relatedActionsLinks() {
      return this.queryAll<HTMLLinkElement>('aside li a');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, TaskSharedModule, RouterTestingModule],
      providers: [KeycloakService],
      declarations: [TestComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    hostComponent = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(hostComponent).toBeTruthy();
  });

  it('should show appropriate content', () => {
    expect(page.heading).toEqual('Notification determination');
    expect(page.relatedActionsLinks.map((el) => [el.href, el.textContent])).toEqual([
      ['http://localhost/tasks/1/change-assignee', 'Reassign task'],
      ['http://localhost/rfi/1/questions', 'Request for information'],
    ]);
    expect(page.relatedTasks).toEqual(['Permit determination', 'Permit application']);
    expect(page.TimelineTasks).toEqual(['Notification submitted by User']);
  });
});