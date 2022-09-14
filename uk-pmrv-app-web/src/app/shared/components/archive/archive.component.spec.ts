import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { BasePage, mockClass } from '../../../../testing';
import { SharedModule } from '../../shared.module';

describe('ArchiveComponent', () => {
  let page: Page;
  let hostComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  const tasksService = mockClass(TasksService);
  tasksService.processRequestTaskActionUsingPOST.mockReturnValue(of({}));

  @Component({
    template: `
      <app-archive
        [type]="type"
        [taskId]="taskId"
        [warningText]="warningText"
        [buttonText]="buttonText"
        (submitted)="submitted()"
      ></app-archive>
    `,
  })
  class TestComponent {
    type = 'SYSTEM_MESSAGE_DISMISS';
    taskId = 321;
    warningText = 'This task / item can be archived for future reference.';
    buttonText = 'Archive now and return to dashboard';

    // eslint-disable-next-line @typescript-eslint/no-empty-function
    submitted(): void {}
  }

  class Page extends BasePage<TestComponent> {
    get warningText(): string {
      return this.query('.govuk-warning-text__text').textContent.trim();
    }
    get buttonText(): string {
      return this.query('button').textContent.trim();
    }
    get button(): HTMLButtonElement {
      return this.query('button');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule],
      providers: [{ provide: TasksService, useValue: tasksService }],
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

  it('should display warning and a button', () => {
    expect(page.warningText).toEqual(
      'Warning No actions are currently required. This task / item can be archived for future reference.',
    );
    expect(page.buttonText).toEqual('Archive now and return to dashboard');

    hostComponent.warningText = 'Warn.';
    hostComponent.buttonText = 'Test';
    fixture.detectChanges();

    expect(page.warningText).toEqual('Warning No actions are currently required. Warn.');
    expect(page.buttonText).toEqual('Test');
  });

  it('should post archive and emit submitted', () => {
    const submitSpy = jest.spyOn(hostComponent, 'submitted');

    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(0);
    expect(submitSpy).toHaveBeenCalledTimes(0);

    page.button.click();

    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith({
      requestTaskActionPayload: {
        payloadType: 'EMPTY_PAYLOAD',
      },
      requestTaskActionType: 'SYSTEM_MESSAGE_DISMISS',
      requestTaskId: 321,
    });
    expect(submitSpy).toHaveBeenCalledTimes(1);
  });
});
