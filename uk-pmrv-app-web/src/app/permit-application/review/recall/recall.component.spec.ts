import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../../testing';
import { ReviewModule } from '../review.module';
import { RecallComponent } from './recall.component';

describe('RecallComponent', () => {
  let page: Page;
  let component: RecallComponent;
  let fixture: ComponentFixture<RecallComponent>;

  const tasksService = mockClass(TasksService);

  const activatedRouteStub = new ActivatedRouteStub({ taskId: '237', index: '0' });

  class Page extends BasePage<RecallComponent> {
    get heading() {
      return this.query<HTMLHeadingElement>('h1');
    }
    get submitButton() {
      return this.queryAll<HTMLButtonElement>('button')[0];
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReviewModule, RouterTestingModule],
      providers: [
        { provide: ActivatedRoute, useValue: activatedRouteStub },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RecallComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should delete', () => {
    tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));

    expect(page.heading).toBeTruthy();
    expect(page.heading.textContent.trim()).toEqual('Are you sure you want to recall the permit ?');

    page.submitButton.click();

    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith({
      requestTaskActionType: 'PERMIT_ISSUANCE_RECALL_FROM_AMENDS',
      requestTaskId: 237,
      requestTaskActionPayload: {
        payloadType: 'EMPTY_PAYLOAD',
      },
    });
  });
});
